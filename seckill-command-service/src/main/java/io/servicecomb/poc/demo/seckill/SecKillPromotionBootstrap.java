/*
 *   Copyright 2017 Huawei Technologies Co., Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.repositories.CouponEventRepository;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecKillPromotionBootstrap<T> {

  private static final Logger logger = LoggerFactory.getLogger(SecKillPromotionBootstrap.class);

  private final PromotionRepository promotionRepository;
  private final CouponEventRepository eventRepository;
  private final List<SecKillCommandService<T>> commandServices;
  private final List<SecKillPersistentRunner<T>> persistentRunners;

  public SecKillPromotionBootstrap(
      PromotionRepository promotionRepository,
      CouponEventRepository eventRepository,
      List<SecKillCommandService<T>> commandServices, List<SecKillPersistentRunner<T>> persistentRunners) {
    this.promotionRepository = promotionRepository;
    this.eventRepository = eventRepository;
    this.commandServices = commandServices;
    this.persistentRunners = persistentRunners;
  }

  private Promotion findPromotion() {
    Iterable<Promotion> promotions = promotionRepository.findAll();
    for (Promotion promotion : promotions) {
      if (promotion.getPublishTime().before(new Date())) {
        return promotion;
      }
    }
    return null;
  }

  public void run() {
    CompletableFuture.runAsync(() -> {
      boolean promotionLoaded = false;
      while (!Thread.currentThread().isInterrupted() && !promotionLoaded) {
        try {
          Promotion promotion = findPromotion();
          if (promotion != null) {
            startUpPromotion(promotion);
            promotionLoaded = true;
            logger.info("Promotion started = {}", promotion);
          }
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  private void startUpPromotion(Promotion promotion) {
    BlockingQueue<T> couponQueue = new ArrayBlockingQueue<>(promotion.getNumberOfCoupons());
    AtomicInteger claimedCoupons = new AtomicInteger();
    SeckillRecoveryCheckResult recoveryInfo = new SeckillRecoveryCheckResult(promotion.getNumberOfCoupons());
    SecKillPersistentRunner<T> persistentRunner = new SecKillPersistentRunner<>(promotion,
        couponQueue,
        claimedCoupons,
        eventRepository,
        recoveryInfo);
    persistentRunners.add(persistentRunner);
    persistentRunner.run();

    commandServices.add(new SecKillCommandService<>(promotion,
        couponQueue,
        claimedCoupons,
        recoveryInfo.getClaimedCustomers()));
  }
}