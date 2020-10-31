package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ReactShare {

    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 7)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s ->
                        log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();    //공유스트림 생성

            cachedSource.subscribe(e ->log.info("[S 1] onNext: {}",e));
            Thread.sleep(400);
            cachedSource.subscribe(e ->log.info("[S 2] onNext: {}",e)); //0.4초 후 들어오는 스트림부터 그대로 받음. (이전 스트림 버림)

            Thread.sleep(1000);
    }
}
