package io.servicecomb.poc.demo.seckill;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.servicecomb.poc.demo.CommandQueryApplication;
import io.servicecomb.poc.demo.seckill.event.PromotionEvent;
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
  private MockMvc mockMvc;

  private MediaType contentType = new MediaType(
      MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8")
  );


  @Test
  public void testQueryCurrent() throws Exception {
    repository.deleteAll();
    Promotion promotionTest = new Promotion(new Date(),new Date(),3,0.7f);
    PromotionEvent<String> event = PromotionEvent.genStartCouponEvent(promotionTest);
    repository.save((PromotionEvent<String>)event);

    this.mockMvc.perform(get("/query/promotion").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString(event.getCouponId())));
  }


  @Test
  public void testQuerySuccess() throws Exception {
    repository.deleteAll();
    Coupon couponsTest = new Coupon(1,"mb",new Date(),0.7f,1);
    Promotion promotionCouponsTest = new Promotion(new Date(),new Date(),5,0.8f);
    PromotionEvent<String> event = PromotionEvent.genSecKillCouponEvent(promotionCouponsTest,"mb");
    repository.save((PromotionEvent<String>)event);


    this.mockMvc.perform(get("/query/coupons/mb").contentType(contentType))
        .andExpect(status().isOk()).andExpect(content().string(containsString(event.getCustomerId())));
  }
}