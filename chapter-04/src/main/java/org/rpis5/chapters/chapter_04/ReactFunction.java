package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;

@Slf4j
public class ReactFunction {
    public static void main(String[] args) throws InterruptedException {

        Flux.range(1,5)
                .map(s1->s1*100)    //map 값을 하나하나 받아서 하나하나 처리해서 넘
                .map(s2->s2-10)
                .subscribe(d -> log.info("onNext : " + d));

//        21:55:27.381 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onNext : 90
//        21:55:27.381 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onNext : 190
//        21:55:27.382 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onNext : 290
//        21:55:27.382 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onNext : 390
//        21:55:27.382 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onNext : 490

        Flux.interval(Duration.ofMillis(100))   //100ms마다 0,1,2.....
                .skipUntilOther(Mono.delay(Duration.ofSeconds(1)))   //1초 후부터 데이터를 가져온다
                .takeUntilOther(Mono.delay(Duration.ofSeconds(2)))    //2초까지만 데이터를 가져온다
                .subscribe(s->log.info(s+""));  //즉, 1초후 ~ 2초사이 데이터만 찍힘
        Thread.sleep(3000); //main thread가 종료되면 damon thread라 걍 같이 죽어버림

//        22:05:18.797 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 10
//        22:05:18.899 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 11
//        22:05:18.996 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 12
//        22:05:19.096 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 13
//        22:05:19.196 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 14
//        22:05:19.296 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 15
//        22:05:19.396 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 16
//        22:05:19.496 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 17
//        22:05:19.596 [parallel-3] INFO org.rpis5.chapters.chapter_04.ReactFunction - 18


        Flux.just(1,6,3,1,2,4,56,5,2)
                .collectSortedList(Comparator.reverseOrder())   //내림차순 정렬
                .subscribe(s->log.info(s+""));

//        22:07:32.192 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [56, 6, 5, 4, 3, 2, 2, 1, 1]


        Flux.just(1,6,3,1,2,4,56,5,2)
                .any(a->a%2==0) //2로 나눌때 나머지가 0인 데이터가 하나라도 있다면 true
                .subscribe(s->log.info(s+""));

//        22:08:55.810 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - true


        Flux.range(1,5)
//                .reduce(0,(a,b) -> a+b)     //결과만 보여줌 (둘다 동일하게 앞인자와 들어오는 인자를 계속 연산해줌, 기능은 동일
                .scan(0,(a,b) -> a+b)   //과정도 보여줌 (둘다 동일하게 앞인자와 들어오는 인자를 계속 연산해줌, 기능은 동일
                .subscribe(s->log.info(s+""));  //0+1 -> 1+2 -> 3+3 -> 6+4 -> 10+5 = 15

//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 15 //reduce 결과

//        22:13:50.100 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 0  //scan 결과
//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 1
//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 3
//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 6
//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 10
//        22:13:50.101 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 15


        Flux.range(1,3)
                .thenMany(Flux.just(4,5))   //상위 스트림은 무시하고 자기 스트림만 처리해서 다운스트림으로 보냄
                .subscribe(s->log.info(s+""));

//        22:17:26.057 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 4
//        22:17:26.057 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 5


        Flux.concat(    //안의 2개 스트림을 합친다.
                Flux.range(1,3),
                Flux.just(6,7)
                )
                .subscribe(s->log.info(s+""));

//        22:19:42.865 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 1
//        22:19:42.865 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 2
//        22:19:42.865 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 3
//        22:19:42.865 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 6
//        22:19:42.865 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 7


        Flux.range(1,10)
                .buffer(3)              //3개씩 받기 버퍼링
                .subscribe(s->log.info(s+""));

//        22:20:41.012 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [1, 2, 3]
//        22:20:41.012 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [4, 5, 6]
//        22:20:41.012 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [7, 8, 9]
//        22:20:41.012 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [10]


        Flux.range(101, 10)
                .windowUntil(s->s%3==0, true)   //3나눌때 나머지가 0이면 다운스트림 내려줌 true(해당값 앞에서 자름)
                .subscribe(
                        window -> window.collectList()  //다운스트림 내려온 값을 list로 묶어줌
                                .subscribe(e -> log.info(e+""))
                );
//        22:28:37.390 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [101]
//        22:28:37.390 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [102, 103, 104]
//        22:28:37.390 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [105, 106, 107]
//        22:28:37.391 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [108, 109, 110]


        Flux.range(1, 5)
                .groupBy(e -> e % 2 == 0 ? "a" : "b")   //2로 나눌때 나머지가 0이면 key가 a, 아니면 key가 b
                .subscribe(g -> g
                        .subscribe(e -> log.info(g.key()+" "+e)));

//        22:34:02.824 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - b 1
//        22:34:02.825 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - a 2
//        22:34:02.825 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - b 3
//        22:34:02.825 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - a 4
//        22:34:02.825 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - b 5


        Flux.range(1,5)
                .filter(f->f<3) //filter 조건에 맞을때만 다운스트림에 내려줌
                .subscribe(e -> log.info(e+""));

//        22:36:36.727 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 1
//        22:36:36.727 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - 2


        Flux.just("a","b","c")
                .flatMap(f->Flux.just("1","2","3")
                    .map(s->f+"-"+s)        //flatmap과 맵이 조합 가능
                    .map(s->"["+f+"]"+s)    //flatmap과 맵이 조합 가능
                )
                .subscribe(d -> log.info(d+""));

//        22:47:43.627 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [a]a-1
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [a]a-2
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [a]a-3
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [b]b-1
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [b]b-2
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [b]b-3
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [c]c-1
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [c]c-2
//        22:47:43.628 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - [c]c-3


//        Flux.range(1,1000)
//                .delayElements(Duration.ofMillis(10)) //이거나 아래나 똑같다
        Flux.interval(Duration.ofMillis(10))    //10ms 간격 0,1,2,3.....
                .sample(Duration.ofMillis(200)) //200ms마다 샘플링해서 다운스트림
                .subscribe(d -> log.info(d+""));    //실행때마다 결과는 다르다

        Thread.sleep(2000);

//        22:51:33.394 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 17
//        22:51:33.591 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 33
//        22:51:33.790 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 49
//        22:51:33.994 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 65
//        22:51:34.190 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 82
//        22:51:34.391 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 99
//        22:51:34.590 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 116
//        22:51:34.792 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 132
//        22:51:34.990 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 149
//        22:51:35.189 [parallel-1] INFO org.rpis5.chapters.chapter_04.ReactFunction - 166

        Flux.range(1,3)
            .concatWith(Flux.error(new Exception()))    //스트림을 순서대로 연결
            .doOnEach(s->log.info(s+""))    //onSubscribe, onNext, onError, onComplete 포함 모든 신호를 처리
            .subscribe();

//        22:57:50.980 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - doOnEach_onNext(1)
//        22:57:50.980 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - doOnEach_onNext(2)
//        22:57:50.980 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - doOnEach_onNext(3)
//        22:57:50.987 [main] INFO org.rpis5.chapters.chapter_04.ReactFunction - onError(java.lang.Exception)




    }



}
