package org.rpis5.chapters.chapter_01.futures;

import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Slf4j
public class OrdersService {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        OrdersService ordersService1 = new OrdersService();

        ordersService1.process();
        ordersService1.process();

        //위 future 작업이 끝날때까지 기다림 (즉 블로킹)
        log.info("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }

    void process() {
        String input = "myinput";
        Future<String> result = calculate(input);

        log.info("calculate called : " + input);

        try {
            //shopping service에서 Thread.sleep 1초 지연이 된 후 result.get()이 호출된다.
            //즉 callback형식으로 호출된다.
            log.info("Future result.get() value : "  + result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //future.get()을 받을때까지 블록킹 된다. (get의 특성임)
        log.info("process() ended");
    }

    //Future<String>을 return 타입으로 가지면
    //String 값을 콜백할 수 있다 -> 콜백되면 get()에 String이 들어간다.
    //Future 안의 처리로직은 main이 아닌 별도 thread에서 처리된다.
    public Future<String> calculate(String value) {
        //FutureTask안에 콜백할 코드를 넣을 수 있음
        FutureTask<String> future = new FutureTask<>(() -> {
            Thread.sleep(1000);
            log.info("return Future value with added String");
            return value+" - added String";
        });

        new Thread(future).start();

        return future;
    }

}
