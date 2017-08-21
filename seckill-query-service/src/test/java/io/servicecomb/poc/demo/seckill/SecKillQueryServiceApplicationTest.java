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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
    Date startTime = new Date();
    Date finishTime = new Date(startTime.getTime()+10*60*1000);

    Promotion promotionCouponsTest = new Promotion(startTime,finishTime,5,0.8f);
    PromotionEvent<String> seckillEvent = PromotionEvent.genSecKillCouponEvent(promotionCouponsTest,"iTest");
    repository.save((PromotionEvent<String>)seckillEvent);

    this.mockMvc.perform(get("/query/coupons/iTest").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString("iTest")));

    repository.deleteAll();
  }

  @Test
  public void testQueryCurrent() throws Exception {

    //test null promotions
    this.mockMvc.perform(get("/query/promotion").contentType(contentType))
        .andExpect(content().string(containsString("[]")));

    //inject test promotion
    List<String> expectCouponIdList = new ArrayList<String>();
    for (int i = 1; i <= 5; i++) {
      long startTimes = new Date().getTime() + i*60*1000;
      long finishTimes = startTimes + 10*60*1000;

      Date startTime = new Date(startTimes);
      Date finishTime = new Date(finishTimes);

      Promotion promotionTest = new Promotion(startTime,finishTime,i+1,0.7f);
      promotionRepository.save(promotionTest);
      expectCouponIdList.add(promotionTest.getId());

      PromotionEvent<String> startEvent = PromotionEvent.genStartCouponEvent(promotionTest);
      repository.save((PromotionEvent<String>)startEvent);
    }

    //check the query result wether is matching
    ResultActions resultFill = this.mockMvc.perform(get("/query/promotion").contentType(contentType));
    resultFill.andReturn().getResponse().getContentLength();
    for (String couponId : expectCouponIdList) {
      resultFill.andExpect(content().string(containsString(couponId)));
    }

    repository.deleteAll();
    promotionRepository.deleteAll();
  }
}