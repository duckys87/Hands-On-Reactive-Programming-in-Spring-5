package org.rpis5.chapters.chapter_02.rx_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class TemperatureSensor {
   private static final Logger log = LoggerFactory.getLogger(TemperatureSensor.class);
   private final Random rnd = new Random();

   private final Observable<Temperature> dataStream =
      Observable
         //해당 범위만큼 숫자 생성
         .range(0, Integer.MAX_VALUE)
         //순서대로 데이터 처리를 보장해주는 map
         .concatMap(ignore -> Observable
            //just는 들어온 인자 순서대로 데이터를 차례로 발생(이 경우는 인자가 1개니 걍 1이 발행됨)
            .just(1)
            //5초내 랜덤하게 delay
            .delay(rnd.nextInt(5000), MILLISECONDS)
            //받아들인 데이터를 새로운 형태로 변
            .map(ignore2 -> this.probe()))
         //Observable을 ConnectableObservable로 변
         .publish()
         //몇명의 구독자가 있는지 알려줌
         .refCount();

   public Observable<Temperature> temperatureStream() {
     return dataStream;
   }

   private Temperature probe() {
      double actualTemp = 16 + rnd.nextGaussian() * 10;
      log.info("Asking sensor, sensor value: {}", actualTemp);
      return new Temperature(actualTemp);
   }
}
