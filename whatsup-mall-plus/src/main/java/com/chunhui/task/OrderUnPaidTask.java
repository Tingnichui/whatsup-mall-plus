package com.chunhui.task;

import com.chunhui.common.Constants;
import com.chunhui.common.NewBeeMallOrderStatusEnum;
import com.chunhui.dao.NewBeeMallGoodsMapper;
import com.chunhui.dao.NewBeeMallOrderItemMapper;
import com.chunhui.dao.NewBeeMallOrderMapper;
import com.chunhui.dao.NewBeeMallSeckillMapper;
import com.chunhui.entity.NewBeeMallOrder;
import com.chunhui.entity.NewBeeMallOrderItem;
import com.chunhui.redis.RedisCache;
import com.chunhui.service.NewBeeMallCouponService;
import com.chunhui.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 未支付订单超时自动取消任务
 */
public class OrderUnPaidTask extends Task {
    /**
     * 默认延迟时间30分钟，单位毫秒
     */
    private static final long DELAY_TIME = 30 * 60 * 1000;

    private final Logger log = LoggerFactory.getLogger(OrderUnPaidTask.class);
    /**
     * 订单id
     */
    private final Long orderId;

    public OrderUnPaidTask(Long orderId, long delayInMilliseconds) {
        super("OrderUnPaidTask-" + orderId, delayInMilliseconds);
        this.orderId = orderId;
    }

    public OrderUnPaidTask(Long orderId) {
        super("OrderUnPaidTask-" + orderId, DELAY_TIME);
        this.orderId = orderId;
    }

    @Override
    public void run() {
        log.info("系统开始处理延时任务---订单超时未付款--- {}", this.orderId);

        NewBeeMallOrderMapper newBeeMallOrderMapper = SpringContextUtil.getBean(NewBeeMallOrderMapper.class);
        NewBeeMallOrderItemMapper newBeeMallOrderItemMapper = SpringContextUtil.getBean(NewBeeMallOrderItemMapper.class);
        NewBeeMallGoodsMapper newBeeMallGoodsMapper = SpringContextUtil.getBean(NewBeeMallGoodsMapper.class);
        NewBeeMallCouponService newBeeMallCouponService = SpringContextUtil.getBean(NewBeeMallCouponService.class);

        NewBeeMallOrder order = newBeeMallOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.info("系统结束处理延时任务---订单超时未付款--- {}", this.orderId);
            return;
        }
        if (order.getOrderStatus() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            log.info("系统结束处理延时任务---订单超时未付款--- {}", this.orderId);
            return;
        }

        // 设置订单为已取消状态
        order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus());
        order.setUpdateTime(new Date());
        if (newBeeMallOrderMapper.updateByPrimaryKey(order) <= 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<NewBeeMallOrderItem> newBeeMallOrderItems = newBeeMallOrderItemMapper.selectByOrderId(orderId);
        for (NewBeeMallOrderItem orderItem : newBeeMallOrderItems) {
            if (orderItem.getSeckillId() != null) {
                Long seckillId = orderItem.getSeckillId();
                NewBeeMallSeckillMapper newBeeMallSeckillMapper = SpringContextUtil.getBean(NewBeeMallSeckillMapper.class);
                RedisCache redisCache = SpringContextUtil.getBean(RedisCache.class);
                if (!newBeeMallSeckillMapper.addStock(seckillId)) {
                    throw new RuntimeException("秒杀商品货品库存增加失败");
                }
                redisCache.increment(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
            } else {
                Long goodsId = orderItem.getGoodsId();
                Integer goodsCount = orderItem.getGoodsCount();
                if (!newBeeMallGoodsMapper.addStock(goodsId, goodsCount)) {
                    throw new RuntimeException("商品货品库存增加失败");
                }
            }
        }

        // 返还优惠券
        newBeeMallCouponService.releaseCoupon(orderId);
        log.info("系统结束处理延时任务---订单超时未付款--- {}", this.orderId);
    }
}
