package io.servicecomb.poc.demo.seckill;

import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecKillQueryConfig {

  @Bean
  SecKillEventSubscriber secKillEventSubscriber(SpringBasedPromotionEventRepository couponEventRepository,PromotionRepository promotionRepository){
    return new SecKillEventSubscriber(couponEventRepository,promotionRepository);
  }
}
