package cn.lili.modules.member.serviceimpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.enums.SwitchEnum;
import cn.lili.common.event.TransactionCommitSendMQEvent;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.properties.RocketmqCustomProperties;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.security.enums.UserEnums;
import cn.lili.common.sensitive.SensitiveWordsFilter;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dos.MemberEvaluation;
import cn.lili.modules.member.entity.dto.EvaluationQueryParams;
import cn.lili.modules.member.entity.dto.MemberEvaluationDTO;
import cn.lili.modules.member.entity.enums.EvaluationGradeEnum;
import cn.lili.modules.member.entity.vo.EvaluationNumberVO;
import cn.lili.modules.member.entity.vo.MemberEvaluationListVO;
import cn.lili.modules.member.entity.vo.MemberEvaluationVO;
import cn.lili.modules.member.entity.vo.StoreRatingVO;
import cn.lili.modules.member.mapper.MemberEvaluationMapper;
import cn.lili.modules.member.service.MemberEvaluationService;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.order.order.entity.dos.Order;
import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.entity.enums.CommentStatusEnum;
import cn.lili.modules.order.order.service.OrderItemService;
import cn.lili.modules.order.order.service.OrderService;
import cn.lili.mybatis.util.PageUtil;
import cn.lili.rocketmq.tags.GoodsTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 会员商品评价业务层实现
 *
 * @author Bulbasaur
 * @since 2020-02-25 14:10:16
 */
@Service
public class MemberEvaluationServiceImpl extends ServiceImpl<MemberEvaluationMapper, MemberEvaluation> implements MemberEvaluationService {

    /**
     * 订单
     */
    @Autowired
    private OrderService orderService;
    /**
     * 子订单
     */
    @Autowired
    private OrderItemService orderItemService;
    /**
     * 会员
     */
    @Autowired
    private MemberService memberService;
    /**
     * 商品
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * rocketMq配置
     */
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public IPage<MemberEvaluation> managerQuery(EvaluationQueryParams queryParams) {
        //获取评价分页
        return this.page(PageUtil.initPage(queryParams), queryParams.queryWrapper());
    }

    @Override
    public IPage<MemberEvaluationListVO> queryPage(EvaluationQueryParams evaluationQueryParams) {
        return this.baseMapper.getMemberEvaluationList(PageUtil.initPage(evaluationQueryParams), evaluationQueryParams.queryWrapper());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberEvaluationDTO addMemberEvaluation(MemberEvaluationDTO memberEvaluationDTO, Boolean isSelf) {
        //获取子订单信息
        OrderItem orderItem = orderItemService.getBySn(memberEvaluationDTO.getOrderItemSn());

        if (orderItem == null) {
            throw new ServiceException(ResultCode.ORDER_ITEM_NOT_EXIST);
        }

        //获取订单信息
        Order order = orderService.getBySn(orderItem.getOrderSn());

        if (order == null) {
            throw new ServiceException(ResultCode.ORDER_NOT_EXIST);
        }

        //检测是否可以添加会员评价
        Member member;

        checkMemberEvaluation(orderItem, order);

        if (Boolean.TRUE.equals(isSelf)) {
            //自我评价商品时，获取当前登录用户信息
            member = memberService.getUserInfo();
        } else {
            //获取用户信息 非自己评价时，读取数据库
            member = memberService.getById(order.getMemberId());
            if (member == null) {
                throw new ServiceException(ResultCode.USER_NOT_EXIST);
            }
        }

        //获取商品信息
        GoodsSku goodsSku = goodsSkuService.getGoodsSkuByIdFromCache(memberEvaluationDTO.getSkuId());

        if (goodsSku == null) {
            throw new ServiceException(ResultCode.GOODS_NOT_EXIST);
        }

        //新增用户评价
        MemberEvaluation memberEvaluation = new MemberEvaluation(memberEvaluationDTO, goodsSku, member, order);
        //过滤商品咨询敏感词
        memberEvaluation.setContent(SensitiveWordsFilter.filter(memberEvaluation.getContent()));
        //添加评价
        this.save(memberEvaluation);

        //修改订单货物评价状态为已评价
        orderItemService.updateCommentStatus(orderItem.getSn(), CommentStatusEnum.FINISHED);
        //发送商品评价消息
        applicationEventPublisher.publishEvent(new TransactionCommitSendMQEvent("同步商品评价消息",
                rocketmqCustomProperties.getGoodsTopic(), GoodsTagsEnum.GOODS_COMMENT_COMPLETE.name(), JSONUtil.toJsonStr(memberEvaluation)));
        return memberEvaluationDTO;
    }

    @Override
    public MemberEvaluationVO queryById(String id) {
        return new MemberEvaluationVO(this.getById(id));
    }

    @Override
    public boolean updateStatus(String id, String status) {
        UpdateWrapper updateWrapper = Wrappers.update();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status.equals(SwitchEnum.OPEN.name()) ? SwitchEnum.OPEN.name() : SwitchEnum.CLOSE.name());
        return this.update(updateWrapper);
    }

    @Override
    public boolean delete(String id) {
        LambdaUpdateWrapper<MemberEvaluation> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(MemberEvaluation::getDeleteFlag, true);
        updateWrapper.eq(MemberEvaluation::getId, id);
        return this.update(updateWrapper);
    }

    @Override
    public boolean reply(String id, String reply, String replyImage) {
        UpdateWrapper<MemberEvaluation> updateWrapper = Wrappers.update();
        updateWrapper.set("reply_status", true);
        updateWrapper.set("reply", reply);
        if (CharSequenceUtil.isNotEmpty(replyImage)) {
            updateWrapper.set("have_reply_image", true);
            updateWrapper.set("reply_image", replyImage);
        }
        updateWrapper.eq("id", id);
        return this.update(updateWrapper);
    }

    @Override
    public EvaluationNumberVO getEvaluationNumber(String goodsId) {
        EvaluationNumberVO evaluationNumberVO = new EvaluationNumberVO();
        List<Map<String, Object>> list = this.baseMapper.getEvaluationNumber(goodsId);


        Integer good = 0;
        Integer moderate = 0;
        Integer worse = 0;
        for (Map<String, Object> map : list) {
            if (map.get("grade").equals(EvaluationGradeEnum.GOOD.name())) {
                good = Integer.valueOf(map.get("num").toString());
            } else if (map.get("grade").equals(EvaluationGradeEnum.MODERATE.name())) {
                moderate = Integer.valueOf(map.get("num").toString());
            } else if (map.get("grade").equals(EvaluationGradeEnum.WORSE.name())) {
                worse = Integer.valueOf(map.get("num").toString());
            }
        }
        evaluationNumberVO.setAll(good + moderate + worse);
        evaluationNumberVO.setGood(good);
        evaluationNumberVO.setModerate(moderate);
        evaluationNumberVO.setWorse(worse);
        evaluationNumberVO.setHaveImage(this.count(new QueryWrapper<MemberEvaluation>()
                .eq("have_image", 1)
                .eq("status", SwitchEnum.OPEN.name())
                .eq("goods_id", goodsId)));

        return evaluationNumberVO;
    }

    @Override
    public long todayMemberEvaluation() {
        return this.count(new LambdaQueryWrapper<MemberEvaluation>().ge(MemberEvaluation::getCreateTime, DateUtil.beginOfDay(new DateTime())));
    }

    @Override
    public long getWaitReplyNum() {
        QueryWrapper<MemberEvaluation> queryWrapper = Wrappers.query();
        queryWrapper.eq(CharSequenceUtil.equals(UserContext.getCurrentUser().getRole().name(), UserEnums.STORE.name()),
                "store_id", UserContext.getCurrentUser().getStoreId());
        queryWrapper.eq("reply_status", false);
        return this.count(queryWrapper);
    }

    /**
     * 统计商品评价数量
     *
     * @param evaluationQueryParams 查询条件
     * @return 商品评价数量
     */
    @Override
    public long getEvaluationCount(EvaluationQueryParams evaluationQueryParams) {
        return this.count(evaluationQueryParams.queryWrapper());
    }

    @Override
    public List<Map<String, Object>> memberEvaluationNum(DateTime startDate, DateTime endDate) {
        return this.baseMapper.memberEvaluationNum(new QueryWrapper<MemberEvaluation>()
                .between("create_time", startDate, endDate));
    }

    @Override
    public StoreRatingVO getStoreRatingVO(String storeId, String status) {
        LambdaQueryWrapper<MemberEvaluation> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MemberEvaluation::getStoreId, storeId);
        lambdaQueryWrapper.eq(MemberEvaluation::getStatus, SwitchEnum.OPEN.name());
        return this.baseMapper.getStoreRatingVO(lambdaQueryWrapper);
    }

    /**
     * 检测会员评价
     *
     * @param orderItem 子订单
     * @param order     订单
     */
    public void checkMemberEvaluation(OrderItem orderItem, Order order) {

        //根据子订单编号判断是否评价过
        if (orderItem.getCommentStatus().equals(CommentStatusEnum.FINISHED.name())) {
            throw new ServiceException(ResultCode.EVALUATION_DOUBLE_ERROR);
        }

        //判断是否是当前会员的订单
        if (UserContext.getCurrentUser() != null && !order.getMemberId().equals(UserContext.getCurrentUser().getId())) {
            throw new ServiceException(ResultCode.ORDER_NOT_USER);
        }
    }

}