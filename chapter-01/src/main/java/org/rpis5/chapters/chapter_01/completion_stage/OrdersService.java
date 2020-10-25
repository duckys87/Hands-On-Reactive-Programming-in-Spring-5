package org.rpis5.chapters.chapter_01.completion_stage;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class OrdersService {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersService = new OrdersService();

        ordersService.process();
        ordersService.process();

        log.info("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));

        //main thread가 죽으면 CompletableFuture 콜백이 오지 않는다 ㅠㅠ
        //아마 CompletableFuture가 내부적으로 Damon Thread로 처리하나봄..
        Thread.sleep(2000);
    }

    void process() {
        log.info("process called and calculate call");
        calculate("testValue")
                //콜백이 오면 처리됨 (thenApply : 콜백으로 넘겨받은 값을 입력받고 처리 후 CompletionStage<String>로 반환)
                .thenApply(s -> s + " (thenApply1)")
                //콜백이 오면 처리됨 (thenApply : 콜백으로 넘겨받은 값을 입력받고 처리 후 CompletionStage<String>로 반환)
                .thenApply(s -> s + " (thenApply2)")
                //콜백이 오면 처리됨 (thenAccept : 콜백으로 넘겨받은 값을 받고 처리 후 아무것도 안하고 싶을때 - 즉 마지막에)
                .thenAccept(v -> log.info("callback value : " + v));
        //calculate가 비동기라서 응답을 기다리지 않고 바로 아래 로깅이 찍힌다.
        log.info("process ended");
    }


    public CompletionStage<String> calculate(String value) {
        //CompletableFuture는 Async callback을 지원해서 main thread가 블락킹될 일이 없고 별도의 Thread를 안만들어도 된다.
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value + " (added String)";
        });
    }
}
