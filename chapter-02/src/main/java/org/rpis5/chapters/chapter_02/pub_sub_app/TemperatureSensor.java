package org.rpis5.chapters.chapter_02.pub_sub_app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

// springboot 기동 시 bean생성하며 PostConstruct에 의해 1초 후 온도체크함
// 이후 5초랜덤으로 계속 온도체크 수행
// 매 온도체크때마다 이벤트를 던짐 (클라가 있든 없든 던짐)
@Component
public class TemperatureSensor {
   //이벤트를 발생시킬 수 있음
   private final ApplicationEventPublisher publisher;
   private final Random rnd = new Random();
   private final ScheduledExecutorService executor =
           Executors.newSingleThreadScheduledExecutor();

   public TemperatureSensor(ApplicationEventPublisher publisher) {
      this.publisher = publisher;
   }

   //bean생성 시 한번 실행
   @PostConstruct
   //지정한 delay 이후 command 1회 수행
   public void startProcessing() {
      this.executor.schedule(this::probe, 1, SECONDS);
   }

   //비즈니스 로직
   private void probe() {
      //nextGaussian 랜덤 소수 출력
      double temperature = 16 + rnd.nextGaussian() * 10;
      //온도값을 담아서 이벤트를 던진다.
      publisher.publishEvent(new Temperature(temperature));

      //지정한 delay 이후 command 1회 수행 (5초내 랜덤)
      executor.schedule(this::probe, rnd.nextInt(5000), MILLISECONDS);
   }
}
