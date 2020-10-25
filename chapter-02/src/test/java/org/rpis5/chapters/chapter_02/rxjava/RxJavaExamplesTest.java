package org.rpis5.chapters.chapter_02.rxjava;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RxJavaExamplesTest {

   @Test
   @SuppressWarnings("Depricated")
   public void simpleRxJavaWorkflow() {
      Observable<String> observable = Observable.create(
         new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> sub) {
               sub.onNext("Hello, reactive world!");
               sub.onCompleted();
            }
         }
      );
   }

   @Test
   @SuppressWarnings("Depricated")
   public void simpleRxJavaWorkflowWithLambdas() {
      Observable.create(
         sub -> {
            sub.onNext("Hello, reactive world!");
            sub.onCompleted();
         }
      ).subscribe(
         System.out::println,
         System.err::println,
         () -> System.out.println("Done!")
      );
   }

   @Test
   public void creatingRxStreams() {
      Observable.just("1", "2", "3", "4");
      Observable.from(new String[]{"A", "B", "C"});
      Observable.from(Collections.<String>emptyList());

      Observable<String> hello = Observable.fromCallable(() -> "Hello ");
      Future<String> future =
              Executors.newCachedThreadPool().submit(() -> "World");
      Observable<String> world = Observable.from(future);

      Observable.concat(hello, world, Observable.just("!\n"))
         .forEach(System.out::print);
   }

//   Hello World!

   @Test
   public void zipOperatorExample() {
      Observable.zip(
              Observable.just("A", "B", "C"),
              Observable.just("1", "2", "3"),
              (x, y) -> x + "|" + y
      ).forEach(System.out::println);
   }

//   A|1
//   B|2
//   C|3

   @Test
   public void timeBasedSequenceExample() throws InterruptedException {
      Observable.interval(1, TimeUnit.SECONDS)
         .subscribe(e -> System.out.println("Received: " + e));

      Thread.sleep(5000);
   }

//   Received: 0
//   Received: 1
//   Received: 2
//   Received: 3
//   Received: 4

   @Test
   public void managingSubscription() {
      AtomicReference<Subscription> subscription = new AtomicReference<>();
      subscription.set(Observable.interval(100, MILLISECONDS)
         .subscribe(e -> {
            System.out.println("Received: " + e);
            if (e >= 3) {
               subscription.get().unsubscribe();
            }
         }));

      do {
         // executing something useful...
      } while (!subscription.get().isUnsubscribed());
   }

//   Received: 0
//   Received: 1
//   Received: 2
//   Received: 3

   @Test
   public void managingSubscription2() throws InterruptedException {
      CountDownLatch externalSignal = new CountDownLatch(3);

      Subscription subscription = Observable
              .interval(100, MILLISECONDS)
              .subscribe(System.out::println);

      externalSignal.await(450, MILLISECONDS);
      subscription.unsubscribe();
   }

//           0
//           1
//           2
//           3

   @Test
   public void deferSynchronousRequest() throws Exception {
      String query = "testResult";
      Observable.fromCallable(() -> doSlowSyncRequest(query))
         // SubscribeOn은 구독(subscribe)에서 사용할 스레드를 지정
         // Schedulers.io() - 동기 I/O를 별도로 처리시켜 비동기 효율을 얻기 위한 스케줄러입니다. 자체적인 스레드 풀에 의존합니다.
         .subscribeOn(Schedulers.io())
         .subscribe(this::processResult);

      Thread.sleep(1000);
   }

   private String doSlowSyncRequest(String query) {
      return query;
   }

   private void processResult(String result) {
      System.out.println(Thread.currentThread().getName() + ": " + result);
   }

//   RxIoScheduler-2: testResult
}
