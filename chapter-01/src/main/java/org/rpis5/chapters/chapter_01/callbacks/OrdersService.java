package org.rpis5.chapters.chapter_01.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

@Slf4j
public class OrdersService {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        OrdersService ordersService = new OrdersService();

        ordersService.process(false);   //sync
        ordersService.process(true);    //async
        ordersService.process(true);    //async

        //async call 콜백이 오기전 아래 로깅이 찍히고, main thread는 종료된다.
        log.info("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }

    void process(Boolean isAsync) {
        log.info("service called : " + isAsync);
        if(isAsync) {
            Input input = new Input();
            calculateAsyn("myAsyncValue", output -> {
                //콜백이 오면 처리된다
                log.info("ASync completed - call value result : " + output);
            });
        }
        else{
            Input input = new Input();
            calculateSync("mySyncValue", output -> {
                //콜백이 오면 처리된다
                log.info("Sync completed - call value result : " + output);
            });
        }
    }

    //Consumer<String>을 매개변수로 잡고 위 process()에서 output처럼 선언하면 callback 선언이 가능하다.
    public void calculateAsyn(String value, Consumer<String> c) {
        //콜백처리를 별도 Thread로 돌리면 main Thread가 블로킹되지 않는다.
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //콜백을 return
            c.accept(value + " return Async ");
        }).start();
    }

    public void calculateSync(String value, Consumer<String> c) {
        //콜백를처리를을 main Thread로 돌리면 main Thread가 블로킹된다.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //콜백을 return
        c.accept(value + " return Sync ");
    }
}
