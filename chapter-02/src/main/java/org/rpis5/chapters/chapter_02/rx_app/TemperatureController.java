package org.rpis5.chapters.chapter_02.rx_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Subscriber;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TemperatureController {
   private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);

   private final TemperatureSensor temperatureSensor;

   public TemperatureController(TemperatureSensor temperatureSensor) {
      this.temperatureSensor = temperatureSensor;
   }

   @RequestMapping(value = "/temperature-stream", method = RequestMethod.GET)
   public SseEmitter events(HttpServletRequest request) {
      //subscriber 클래스!!
      RxSeeEmitter emitter = new RxSeeEmitter();
      log.info("[{}] Rx SSE stream opened for client: {}",
         emitter.getSessionId(), request.getRemoteAddr());

      //구독 수행!!
      temperatureSensor.temperatureStream()
         .subscribe(emitter.getSubscriber());

      return emitter;
   }

   @ExceptionHandler(value = AsyncRequestTimeoutException.class)
   public ModelAndView handleTimeout(HttpServletResponse rsp) throws IOException {
      if (!rsp.isCommitted()) {
         rsp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      }
      return new ModelAndView();
   }

   //구독자의 onNext, onError, onCompleted를 직접 구현해줌
   static class RxSeeEmitter extends SseEmitter {
      static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
      private final static AtomicInteger sessionIdSequence = new AtomicInteger(0);

      //서로 다른 RxSeeEmitter 객체이지만 incrementAndGet에 의해 다른 값이 나옴
      // 1,2,3,4.....
      private final int sessionId = sessionIdSequence.incrementAndGet();
      private final Subscriber<Temperature> subscriber;

      RxSeeEmitter() {
         super(SSE_SESSION_TIMEOUT);

         //subscriber 구현!!
         this.subscriber = new Subscriber<Temperature>() {
            @Override
            public void onNext(Temperature temperature) {
               try {
                  //클라이언트달(브라우저)에 온도 값 전
                  RxSeeEmitter.this.send(temperature);
                  log.info("[{}] << {} ", sessionId, temperature.getValue());
               } catch (IOException e) {
                  log.warn("[{}] Can not send event to SSE, closing subscription, message: {}",
                     sessionId, e.getMessage());
                  unsubscribe();
               }
            }

            @Override
            public void onError(Throwable e) {
               log.warn("[{}] Received sensor error: {}", sessionId, e.getMessage());
            }

            @Override
            public void onCompleted() {
               log.warn("[{}] Stream completed", sessionId);
            }
         };

         //Emitter 구현함수인듯
         onCompletion(() -> {
            log.info("[{}] SSE completed", sessionId);
            subscriber.unsubscribe();
         });
         //Emitter 구현함수인듯
         onTimeout(() -> {
            log.info("[{}] SSE timeout", sessionId);
            subscriber.unsubscribe();
         });
      }

      Subscriber<Temperature> getSubscriber() {
         return subscriber;
      }

      int getSessionId() {
         return sessionId;
      }
   }

}

