package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.IntStream;

@Slf4j
public class ReactFactory {
    public static void main(String[] args) throws InterruptedException {
        Flux.push(                              //1~30까지를 FluxSink타입으로 전송
            emitter -> IntStream.range(1,10)
            .forEach(t -> emitter.next(t))
        )
        .delayElements(Duration.ofMillis(1))
        .subscribe(d->log.info(d+""));

        Thread.sleep(100);
//        23:44:38.130 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFactory - 1
//        23:44:38.131 [parallel-2] INFO org.rpis5.chapters.chapter_04.ReactFactory - 2
//        23:44:38.132 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFactory - 3
//        23:44:38.134 [parallel-4] INFO org.rpis5.chapters.chapter_04.ReactFactory - 4
//        23:44:38.135 [parallel-5] INFO org.rpis5.chapters.chapter_04.ReactFactory - 5
//        23:44:38.136 [parallel-6] INFO org.rpis5.chapters.chapter_04.ReactFactory - 6
//        23:44:38.138 [parallel-7] INFO org.rpis5.chapters.chapter_04.ReactFactory - 7
//        23:44:38.139 [parallel-8] INFO org.rpis5.chapters.chapter_04.ReactFactory - 8
//        23:44:38.140 [parallel-9] INFO org.rpis5.chapters.chapter_04.ReactFactory - 9


        Flux.generate(
            ()-> Tuples.of(0,1),    //초기값
            (state,sink)->{     //0 1, 1 1, 1 2, 2 3, 3 5, 5 8, 8 13
                sink.next(state.getT2());   //2번째 파라미터(T2)를 1번씩 보내줌
                return Tuples.of(state.getT2(),(state.getT1()+state.getT2()));  //재귀함수 피보나치
            }
        )
        .take(7)
        .subscribe(d->log.info(d+""));
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 1
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 1
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 2
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 3
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 5
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 8
//        23:51:41.087 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - 13


        Flux.using(     //using을 사용해 여러 작업을 하나의 라이프사이클로 지정가능 (아래 일부러 지연을 줘봤는데 비동기지만 순차적으로 처리해줌)
                Connection::newConnection,
                connection->Flux.fromIterable(connection.getData()),
                Connection::close
        ).subscribe(
                data -> log.info("Data : " + data),
                e -> log.error("Error : " + e.getMessage()),
                ()->log.info("onComplete")
        );

//16:11:49.827 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory$Connection - IO Connection created
//16:11:50.834 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - Data : Some
//16:11:50.834 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - Data : data
//16:11:51.839 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory$Connection - IO Connection closed
//16:11:51.839 [main] INFO org.rpis5.chapters.chapter_04.ReactFactory - onComplete




    }


    @Slf4j
    static class Connection implements AutoCloseable {
        public static Connection newConnection() {
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            log.info("IO Connection created");
            return new Connection();
        }

        public Iterable<String> getData() {
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            return Arrays.asList("Some", "data");
        }

        @Override
        public void close() {
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            log.info("IO Connection closed");
        }
    }

}
