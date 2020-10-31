package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Slf4j
public class ReactError {
    public static void main(String[] args) throws InterruptedException {
        /*  성공확률이 있는 모듈을 리트라이, 타임아웃 등으로 어느정도 커버해주는 비동기 논블록킹 프로그램 */
        ReactError reactError = new ReactError();
        Flux.just("user1")
                .flatMap(user -> reactError.test(user)
                        .retryBackoff(5, Duration.ofMillis(100))    //100ms간격 재시도 최대5회
                        .timeout(Duration.ofSeconds(3))                       //timeout 3초
                        .onErrorResume(e->Flux.just("error Resume.."))        //최종적으로 에러로 처리할때(타임아웃이든 뭐든..)
                )
                .subscribe(
                        d -> log.info("on Next: " + d),
                        e -> log.error("on Error: " + e.getMessage()),
                        () -> log.info("onComplete")
                );

        Thread.sleep(5000); //데몬 쓰레드때문에 걸어줬음

/*  성공 케이스  */
//17:55:19.081 [main] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
//17:55:19.131 [main] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 0
//17:55:19.131 [main] INFO org.rpis5.chapters.chapter_04.ReactError - success
//17:55:19.134 [main] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:55:19.191 [parallel-2] INFO org.rpis5.chapters.chapter_04.ReactError - on Next: test1
//17:55:19.242 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactError - on Next: test2
//17:55:19.243 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactError - onComplete

/*  실패 케이스  */
//17:54:53.984 [main] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 7
//17:54:53.985 [main] INFO org.rpis5.chapters.chapter_04.ReactError - failed
//17:54:53.987 [main] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:54:54.137 [parallel-2] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 7
//17:54:54.137 [parallel-2] INFO org.rpis5.chapters.chapter_04.ReactError - failed
//17:54:54.137 [parallel-2] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:54:54.269 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 3
//17:54:54.269 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactError - failed
//17:54:54.269 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:54:54.848 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 7
//17:54:54.848 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactError - failed
//17:54:54.848 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:54:56.009 [parallel-5] INFO org.rpis5.chapters.chapter_04.ReactError - buf : 4
//17:54:56.009 [parallel-5] INFO org.rpis5.chapters.chapter_04.ReactError - failed
//17:54:56.009 [parallel-5] INFO org.rpis5.chapters.chapter_04.ReactError - for : user1
//17:54:56.983 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactError - on Next: error Resume..
//17:54:56.983 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactError - onComplete

    }

    public Flux<String> test(String test){
        Random random = new Random();
        return Flux.defer(()-> {    //최종 subscribe될때까지 대기
                    int buf = random.nextInt(10);
                    log.info("buf : " + buf);
                    if ( buf > 1) {         //조건을 일부로 타이트하게 줬음
                        log.info("failed");
                        return Flux.<String>error(new RuntimeException("runtime error!!"))
                                .delayElements(Duration.ofMillis(100));
                    } else {
                        log.info("success");
                        return Flux.just("test1", "test2")
                                .delayElements(Duration.ofMillis(50));
                    }
                }
                ).doOnSubscribe(s->log.info("for : {}",test));
    }
}
