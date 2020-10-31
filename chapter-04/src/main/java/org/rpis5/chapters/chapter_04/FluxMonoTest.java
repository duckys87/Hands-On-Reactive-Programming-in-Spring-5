package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

@Slf4j
public class FluxMonoTest {
    public static void main(String[] args) throws InterruptedException {
        /*  다양한 선언 방법들  */
        Flux<String> f1 = Flux.just("test1","test2","test3");
        Flux<String> f2 = Flux.fromArray(new String[]{"test","zz"});
        Flux<String> f3 = Flux.fromIterable(Arrays.asList("test","zz"));
        Flux<Integer> f4 = Flux.range(1,5); //1~5
        Flux<String> f5 = Flux.empty(); //빈 스트림
        Flux<String> f6 = Flux.never(); //빈 스트림

        Mono<String> m1 = Mono.just("test");
        //Mono<String> m1 = Mono.just("test","Zz"); 값이 2개라 에러
        Mono<String> m2 = Mono.error(new RuntimeException("Mono Exception Test"));  //onError 전달

        f1.subscribe(
                s->log.info("onNext: " + s),
                e->log.info("onError: " + e),
                ()-> log.info("onComplete")
                );
//        21:38:31.316 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: test1
//        21:38:31.316 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: test2
//        21:38:31.316 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: test3
//        21:38:31.316 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onComplete

        f4.subscribe(
                s->log.info("onNext: " + s),
                e->log.info("onError: " + e),
                ()-> log.info("onComplete"),
                subscription -> {
                    subscription.request(3);    //request(처리허용량)
                    subscription.cancel();
                }
        );
//        21:42:37.532 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 1
//        21:42:37.533 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 2
//        21:42:37.533 [main] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 3

        Disposable disposable = Flux.interval(Duration.ofMillis(50))    //50ms마다 0,1,2.....
                .subscribe(s -> log.info("onNext: " + s));
        Thread.sleep(200);
        disposable.dispose();      //200ms지연 후 구독 취소(그래서 결과는 4개뿐임)

//        21:45:37.123 [parallel-1] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 0
//        21:45:37.171 [parallel-1] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 1
//        21:45:37.222 [parallel-1] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 2
//        21:45:37.272 [parallel-1] INFO org.rpis5.chapters.chapter_04.FluxMonoTest - onNext: 3



    }




}

