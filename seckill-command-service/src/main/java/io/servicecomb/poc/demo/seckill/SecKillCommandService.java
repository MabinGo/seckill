package io.servicecomb.poc.demo.seckill;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SecKillCommandService<T> {

  private final Queue<T> coupons;
  private final int totalCoupons;
  private final AtomicInteger claimedCoupons;

  public SecKillCommandService(Queue<T> coupons, int totalCoupons) {
    this.coupons = coupons;
    this.totalCoupons = totalCoupons;
    this.claimedCoupons = new AtomicInteger();
  }

  public boolean addCouponTo(T customerId) {
    while (claimedCoupons.get() < totalCoupons) {
      int oldVal = claimedCoupons.get();
      int newVal = oldVal + 1;
      if (claimedCoupons.compareAndSet(oldVal, newVal)) {
        return coupons.offer(customerId);
      }
    }
    return false;
  }
}
