package io.servicecomb.poc.demo.seckill.web;

import org.hibernate.annotations.Type;

import java.util.Date;

public class CouponInfo {

  private int id;
  private Date time;
  private String customerId;
  private int count;
  private float discount;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }

  public CouponInfo(int id, Date time, Object customerId, Integer count, Float discount) {
  }

  public CouponInfo(int id, Date time, String customerId, int count, float discount) {
    this.id = id;
    this.time = time;
    this.customerId = customerId;
    this.count = count;
    this.discount = discount;
  }
}