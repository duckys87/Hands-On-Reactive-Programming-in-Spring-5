package org.rpis5.chapters.chapter_06.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Slf4j
public class TestWebClient {

    public static void main(String[] args) throws InterruptedException {
        String test = WebClient.create("http://localhost:8001/webclient")                           // (1)
                 .get()                                                    // (2)
                 .uri("/users/{id}", 10)
                 .header("test", "test!!!!")
                 .cookie("cTest","cTest!!!!")
                 .retrieve()  //바로 응답온다
                 .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                 .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                 .bodyToMono(String.class)
                 .block();    //응답이 올때까지 대기한다(동기)

          log.info("result : " + test);

//        Mono<String> mono = WebClient.create("http://localhost:8001/webclient")                           // (1)
//                .get()                                                    // (2)
//                .uri("/users/{id}", 10)
//                .header("test", "test!!!!")
//                .cookie("cTest","cTest!!!!")
//                .retrieve()  //바로 응답온다
//                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
//                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
//                .bodyToMono(String.class);
//
//        mono.subscribe(s -> {
//            log.info("result : " + s);  //비동기로 응답이 오면 실행된다.
//        },
//        e-> {
//            log.info("error result : " + e.getMessage());
//        });

        log.info("this is last log");   //이 로그가 찍히는 시점이 중요하다.

        Thread.sleep(3000); //비동기때는 main thread가 죽으면 응답 못받아서 sleep 걸어줌 ㅠ

    }
}
