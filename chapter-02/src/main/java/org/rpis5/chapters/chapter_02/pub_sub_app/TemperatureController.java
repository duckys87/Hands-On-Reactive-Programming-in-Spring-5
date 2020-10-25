package org.rpis5.chapters.chapter_02.pub_sub_app;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.lang.String.format;

@Slf4j
@RestController
public class TemperatureController {
   static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;

   private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

   //해당 요청이 들어올 때 Set<SseEmitter> clients에 client가 1명씩 등록됨
   @RequestMapping(value = "/temperature-stream", method = RequestMethod.GET)
   //SseEmitter를 리턴타입으로 가질경우 stream 방식으로 연속되게 응답을 줄 수 있음.
   public SseEmitter events(HttpServletRequest request) {
      log.info("SSE stream opened for client: " + request.getRemoteAddr());
      SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);
      clients.add(emitter);

      // 타임아웃 혹은 완료되면 client 제거해줌
      emitter.onTimeout(() -> clients.remove(emitter));
      emitter.onCompletion(() -> clients.remove(emitter));

      //이렇게 return해주면 준비끝난거다.
      //이벤트 리스너에서 client.send 해주면 stream 방식으로 응답이 나간다.
      return emitter;
   }

   //Async는 비동기 실행이고 별도 구현한 Thread Pool로 실행냄(sse- thread)
   @Async
   //이벤트 수신 리스너 지정
   @EventListener
   public void handleMessage(Temperature temperature) {
      log.info(format("Temperature: %4.2f C, active subscribers: %d",
         temperature.getValue(), clients.size()));

      List<SseEmitter> deadEmitters = new ArrayList<>();
      //client가 존재하면 client에 JSON형식으로 온도 정보를 보냄
      //client는 /temperature-stream 호출 시 등록됨 (여러명 등록도 됨)
      clients.forEach(emitter -> {
         try {
            Instant start = Instant.now();
            //실제 클라에 온도정보를 보내는 코드
            emitter.send(temperature, MediaType.APPLICATION_JSON);
            //Duration.between는 걍 시간간격 찍어주는거(오래걸린거 찾아낼 때 좋을듯)
            log.info("Sent to client, took: {}", Duration.between(start, Instant.now()).getNano());
         } catch (Exception ignore) {
            deadEmitters.add(emitter);
         }
      });
      clients.removeAll(deadEmitters);
   }

   @ExceptionHandler(value = AsyncRequestTimeoutException.class)
   public ModelAndView handleTimeout(HttpServletResponse rsp) throws IOException {
      if (!rsp.isCommitted()) {
         rsp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      }
      return new ModelAndView();
   }
}
