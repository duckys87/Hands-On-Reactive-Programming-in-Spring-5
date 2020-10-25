package org.rpis5.chapters.chapter_02.pub_sub_app;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

//비동기 통신처리하겠다는 의미
@EnableAsync
@SpringBootApplication
//AsyncConfigurer을 구현하고 getAsyncExecutor()와 getAsyncUncaughtExceptionHandler()를 오버라이딩
//@Async로 지정된 메소드를 실행하는 기본 실행자가 해당 설정으로 변경된다
//설정안하면 default 는 SimpleAsyncTaskExecutor 이다.(즉 SimpleAsyncTaskExecutor로 안하려면 커스터마이징 하는것)
public class Application implements AsyncConfigurer {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Override
   public Executor getAsyncExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setThreadNamePrefix("sse-");
      executor.setCorePoolSize(2);
      executor.setMaxPoolSize(100);
      executor.setQueueCapacity(5);
      executor.initialize();
      return executor;
   }

   @Override
   public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
      return new SimpleAsyncUncaughtExceptionHandler();
   }
}
