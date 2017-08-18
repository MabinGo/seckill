package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import java.util.Date;

public class Coupon<T> {
  private int id;

  private String couponId;

  private Date time;

  private Float discount;

  private T customerId;

  public Coupon(int id, String couponId, Date time, Float discount, T customerId) {
    this.id = id;
    this.couponId = couponId;
    this.time = time;
    this.discount = discount;
    this.customerId = customerId;
  }

  public Coupon(PromotionEvent event) {
    this.id = event.getId();
    this.couponId = event.getCouponId();
    this.time = event.getTime();
    this.discount = event.getDiscount();
    this.customerId = (T)event.getCustomerId();
  }

  public int getId() {
    return id;
  }

  public String getCouponId() {
    return couponId;
  }

  public Date getTime() {
    return time;
  }

  public Float getDiscount() {
    return discount;
  }

  public T getCustomerId() {
    return customerId;
  }
}
