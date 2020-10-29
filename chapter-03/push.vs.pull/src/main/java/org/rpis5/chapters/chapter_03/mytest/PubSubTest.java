package org.rpis5.chapters.chapter_03.mytest;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.*;

@Slf4j
public class PubSubTest {
    public static void main(String[] args) throws InterruptedException {

        Iterable<Integer> itr = Arrays.asList(1,2,3,4,5);

        //pub의 요청을 별도 thread로 돌려보자
        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher pub = new Publisher() {

            Iterator<Integer> it = itr.iterator();

            @Override
            public void subscribe(Subscriber s) {
                //onSubScribe를 무조건 호출해야함(표준임)
                s.onSubscribe(new Subscription() {
                    @Override
                    //여기서 onNext로 요청해주는 로직부분
                    public void request(long n) {
                        es.execute(()->{
                            //외부에서 정의한 n은 내부에서 수정(--)이 안돼서 i 추가정의함
                            int i=0;
                            try {
                                //n개만 보냄(sub이 보내달라고 하는 크기까지만)
                                while(i++ < n) {
                                    if (it.hasNext()) {
                                        s.onNext(it.next());
                                    } else {
                                        s.onComplete();
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                s.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> sub = new Subscriber<Integer>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = s;
                //1개만 처리할 수 있다고 보냄
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext " + item);
                //1개 더 보내달라고 pub에 요청
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(Thread.currentThread().getName() + " onError" + t.getMessage());

            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + " onComplete");

            }
        };
        //pub는 sub를 구독해야함
        pub.subscribe(sub);

        es.awaitTermination(10, TimeUnit.HOURS);
        es.shutdown();
        System.out.println(Thread.currentThread().getName() + " main End");

    }
}
