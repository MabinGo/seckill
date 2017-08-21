package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
import io.servicecomb.poc.demo.seckill.repositories.PromotionRepository;
import io.servicecomb.poc.demo.seckill.repositories.SpringBasedPromotionEventRepository;
import java.nio.charset.Charset;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandQueryApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class SecKillQueryServiceApplicationTest {

  @Autowired
  private SpringBasedPromotionEventRepository repository;

  @Autowired
  private PromotionRepository promotionRepository;

  @Autowired
  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(
      MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8")
  );

  @Test
  public void testQuerySuccess() throws Exception {
    repository.deleteAll();

    Date startTime = new Date();
    Date finishTime = new Date(startTime.getTime()+5*60*1000);

    Promotion promotionCouponsTest = new Promotion(startTime,finishTime,5,0.8f);
    PromotionEvent<String> seckillEvent = PromotionEvent.genSecKillCouponEvent(promotionCouponsTest,"mb");
    repository.save((PromotionEvent<String>)seckillEvent);

    this.mockMvc.perform(get("/query/coupons/mb").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString("mb")));
  }

  @Test
  public void testQueryCurrent() throws Exception {
    repository.deleteAll();

    Date startTime = new Date();
    Date finishTime = new Date(startTime.getTime()+10*60*1000);


    Promotion promotionTest = new Promotion(startTime,finishTime,3,0.7f);
    promotionRepository.save(promotionTest);

    PromotionEvent<String> startEvent = PromotionEvent.genStartCouponEvent(promotionTest);
    repository.save((PromotionEvent<String>)startEvent);

//    PromotionEvent<String> finishEvent = PromotionEvent.genFinishCouponEvent(promotionTest);
//    repository.save((PromotionEvent<String>)finishEvent);

    this.mockMvc.perform(get("/query/promotion").contentType(contentType))
            .andExpect(status().isOk()).andExpect(content().string(containsString(startEvent.getCouponId())));
  }
}