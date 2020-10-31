package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
public class ColdHotPublisher {
    public static void main(String[] args) throws InterruptedException {


            Flux<Integer> coldPublisher = Flux.range(0,3)
                    .doOnSubscribe(s ->
                            log.info("new subscription for the cold publisher"));

            log.info("No data was generated so far");
            coldPublisher.subscribe(e -> log.info("onNext: {}", e));
            coldPublisher.subscribe(e -> log.info("onNext: {}", e));
            log.info("Data was generated twice for two subscribers");   //위 구독 작업이 다 끝나야 이 로그찍힘

//20:47:57.003 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - No data was generated so far
//20:47:57.007 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - new subscription for the cold publisher
//20:47:57.007 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 0
//20:47:57.008 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 1
//20:47:57.008 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 2
//20:47:57.009 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - new subscription for the cold publisher
//20:47:57.009 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 0
//20:47:57.009 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 1
//20:47:57.009 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - onNext: 2
//20:47:57.009 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - Data was generated twice for two subscribers
            log.info("=========================================================");

            /* ConnectableFlux는 connect의 방식이라  */
            Flux<Integer> source = Flux.range(0, 3)
                    .doOnSubscribe(s ->
                            log.info("new subscription for the cold publisher"));   //1번만 찍힌다!! connect라..

            ConnectableFlux<Integer> conn = source.publish();   //ConnectableFlux이 핵심!!!

            conn.subscribe(e -> log.info("[Subscriber 1] onNext: {}", e));
            conn.subscribe(e -> log.info("[Subscriber 2] onNext: {}", e));

            log.info("all subscribers are ready, connecting"); //구독작업보다 먼저 로깅됨
            conn.connect(); //이때부터 구독작업 시작됨!

//20:41:50.776 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - all subscribers are ready, connecting
//20:41:50.778 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - new subscription for the cold publisher
//20:41:50.780 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 1] onNext: 0
//20:41:50.781 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 2] onNext: 0
//20:41:50.781 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 1] onNext: 1
//20:41:50.781 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 2] onNext: 1
//20:41:50.781 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 1] onNext: 2
//20:41:50.781 [main] INFO org.rpis5.chapters.chapter_04.ColdHotPublisher - [Subscriber 2] onNext: 2
    }
}
