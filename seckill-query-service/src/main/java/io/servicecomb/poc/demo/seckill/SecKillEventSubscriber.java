package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SecKillEventSubscriber {

  private SpringBasedPromotionEventRepository couponEventRepository;
  private PromotionRepository promotionRepository;

  public SecKillEventSubscriber(SpringBasedPromotionEventRepository couponEventRepository,
      PromotionRepository promotionRepository) {
    this.couponEventRepository = couponEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public List<Coupon> querySuccessCoupon(String customerId){
    return couponEventRepository.findByCustomerId(customerId).stream().map(event -> new Coupon(event)).collect(Collectors.toList());
  }

  public List<Promotion> queryCurrentPromotion(){
    List<PromotionEvent> startEvents = couponEventRepository.findByTypeOrderByTimeDesc(PromotionEventType.Start);

    List<Promotion> activepromotions = new ArrayList<Promotion>();

    for (PromotionEvent startEvent : startEvents) {
      String couponId = startEvent.getCouponId();
      PromotionEvent finishEvent = couponEventRepository.findTopByCouponIdAndTypeOrderByIdDesc(couponId,PromotionEventType.Finish);
      if(finishEvent == null){
        Promotion promotionElem = promotionRepository.findOne(couponId);
        activepromotions.add(promotionElem);
      }
    }

    return activepromotions;
  }
}
