package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ReactElapsed {
    public static void main(String[] args) throws InterruptedException {
        Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .elapsed()  //이전 이벤트와의 시간간격 측
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));
        Thread.sleep(1000);
    }

}
