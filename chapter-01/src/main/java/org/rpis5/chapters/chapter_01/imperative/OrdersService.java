package org.rpis5.chapters.chapter_01.imperative;

import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_01.commons.Input;
import org.rpis5.chapters.chapter_01.commons.Output;

@Slf4j
public class OrdersService {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        OrdersService ordersService = new OrdersService();

        ordersService.process();
        ordersService.process();

        //앞 sync 요청이 모두 처리 완료된 후 실행된다.
        log.info("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }

    void process() {
        log.info("calculate call");
        log.info("calculate value : "+calculate("test"));

        log.info("process ended");
    }

    public String calculate(String value) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return value + " added value";
    }
}
