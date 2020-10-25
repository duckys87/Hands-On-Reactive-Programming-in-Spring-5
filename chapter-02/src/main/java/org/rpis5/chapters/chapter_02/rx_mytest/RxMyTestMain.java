package org.rpis5.chapters.chapter_02.rx_mytest;

import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RxMyTestMain {
    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> observable =
                Observable
//                        .interval(1000,TimeUnit.MILLISECONDS)
                        //걍 map에 3,2,2,3,1 순서대로 담는거고 이후 아무것도 없으면 걍 onNext에 순차적으로 전달
//                        .just(3,2,2,3,1);됨
                        //0,1,2가 map에 담김
                        .range(3, 5)
                        //위 작업 3번 반복
                        .repeat(3)
//                        // 로그찍고 +10해서 리턴
                        .map(k->{
                            log.info(String.valueOf(k+10));
                            return k+10;
                        })
                        .filter(f->f>15)
                        //이 라인에서 2초 대기
                        //메인 Thread가 끝나면 안돌아서 아래 sleep 줬음
//                        .delay(2000, TimeUnit.MILLISECONDS)
                        //map 값 하나하나 음수부호로 변경
//                        .map(s -> -s)
                        //위 map으로 하면 비동기라 순서가 바뀜
                        //아래와 같이 concat으로 하면 순서는 유지되지만 성능은....
                        .concatMap(c->Observable
                                .just(c)
                                .delay(100, TimeUnit.MILLISECONDS)
                                .map(s -> -s)
                        )
                        //앞에 2개 값 스킵
                        .skip(2)
                        //현재 값 갯수를 바로 onNext에 전달하고 onCompleted해버림
//                        .count()
//                        //총 3개만 값 받기
                        .take(4);

//        19:29:13.327 [main] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - 10
//        19:29:13.330 [main] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - 11
//        19:29:13.330 [main] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - 12
//        19:29:13.331 [main] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - 10
//        19:29:13.331 [main] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - 11
//        19:29:15.332 [RxComputationScheduler-1] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - onNext() : -12
//        19:29:15.332 [RxComputationScheduler-1] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - onNext() : -10
//        19:29:15.332 [RxComputationScheduler-1] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - onNext() : -11
//        19:29:15.332 [RxComputationScheduler-1] INFO org.rpis5.chapters.chapter_02.rx_mytest.RxMyTestMain - onCompleted()

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                log.info("onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                log.info("onError()");
            }

            @Override
            public void onNext(Integer s) {
                log.info("onNext() : " + s );
            }
        };

        observable.subscribe(subscriber);
//        observable.subscribe(s->log.info("test: "+ s));

        Thread.sleep(5000);
    }
}
