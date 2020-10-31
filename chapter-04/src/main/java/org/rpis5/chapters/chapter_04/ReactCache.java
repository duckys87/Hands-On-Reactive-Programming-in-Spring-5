package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ReactCache {

    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 2)
                .doOnSubscribe(s ->
                        log.info("캐시가 없을경우 새로운 구독실행!!"));

        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));   //1초 캐시 - 1초 후 새구독 필요

        cachedSource.subscribe(e -> log.info("[S 1] onNext: {}", e));
        cachedSource.subscribe(e -> log.info("[S 2] onNext: {}", e));

        Thread.sleep(1100); //1.1초가 지났으니 새 구독이 필요

        cachedSource.subscribe(e -> log.info("[S 3] onNext: {}", e));
    }
}
