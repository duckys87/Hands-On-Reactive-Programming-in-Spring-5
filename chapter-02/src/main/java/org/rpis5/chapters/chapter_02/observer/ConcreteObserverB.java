package org.rpis5.chapters.chapter_02.observer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteObserverB implements Observer<String> {
   @Override
   public void observe(String event) {
      log.info("Observer B: " + event);
   }
}
