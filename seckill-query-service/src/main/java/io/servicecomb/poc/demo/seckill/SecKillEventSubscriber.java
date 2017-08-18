package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.event.PromotionEventType;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.util.List;
import java.util.stream.Collectors;

public class SecKillEventSubscriber {

  private SpringBasedPromotionEventRepository couponEventRepository;
  private PromotionRepository promotionRepository;

  public SecKillEventSubscriber(SpringBasedPromotionEventRepository couponEventRepository,PromotionRepository promotionRepository) {
    this.couponEventRepository = couponEventRepository;
    this.promotionRepository = promotionRepository;
  }

  public List<Coupon> querySuccessCoupon(String customerId){
    return couponEventRepository.findByCustomerId(customerId).stream().map(event -> new Coupon(event)).collect(Collectors.toList());
  }

  public Promotion queryCurrentPromotion(){
//    List<PromotionEvent> startEvents = couponEventRepository.findByTypeOrderByTimeDesc(PromotionEventType.Start);
//    for (PromotionEvent startEvent : startEvents) {
//      PromotionEvent finishEvent = couponEventRepository.findTopByCouponIdAndTypeOrderByIdDesc(startEvent.getCouponId(),PromotionEventType.Finish);
//    }
    //return null;

    List<PromotionEvent> startEvents = couponEventRepository.findByTypeOrderByTimeDesc(PromotionEventType.Start);
    if(startEvents.size() != 0)
    {
      PromotionEvent startEvent = startEvents.get(startEvents.size() -1);
      PromotionEvent finishEvent = couponEventRepository
          .findTopByCouponIdAndTypeOrderByIdDesc(startEvent.getCouponId(),PromotionEventType.Finish);
      if(finishEvent == null){
        return promotionRepository.findOne(finishEvent.getCouponId());
      }
    }
     return null;

  }
}
