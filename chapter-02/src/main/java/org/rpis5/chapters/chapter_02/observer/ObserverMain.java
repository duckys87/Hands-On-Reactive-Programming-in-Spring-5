package org.rpis5.chapters.chapter_02.observer;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ObserverMain {
    public static void main(String[] args){
//        Subject<String> sub = new ConcreteSubject();
        Subject<String> sub = new ParallelSubject();
        Observer<String> pubA = new ConcreteObserverA();
        Observer<String> pubB = new ConcreteObserverB();

        log.info("message broker start!!");

        sub.notifyObservers("message1");

        sub.registerObserver(pubA);
        sub.notifyObservers("message2");

        sub.registerObserver(pubB);
        sub.notifyObservers("message3");

        sub.unregisterObserver(pubA);
        sub.notifyObservers("message4");

        sub.unregisterObserver(pubB);
        sub.notifyObservers("message5");

        log.info("message broker end!!");

    }
}

