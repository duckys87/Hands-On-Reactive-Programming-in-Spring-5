package org.rpis5.chapters.chapter_04;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.lang.management.ManagementFactory;
import java.time.Duration;

import static java.lang.String.format;
import static java.time.Instant.now;

@Slf4j
public class CpuLoadTest {

   @Test
   public void loadCpuData() throws InterruptedException {

      OperatingSystemMXBean osMXBean =
         (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

      Flux.interval(Duration.ofMillis(1000))
         .map(ignore -> osMXBean.getSystemCpuLoad())
         .filter(load -> !load.isNaN())
         .take(3)
         .subscribe(load ->
                      log.info(format("[%s] System CPU load: %2.2f %%", now(), load * 100.0)));

      log.info("Main Thread Done");
      Thread.sleep(10_000);
   }
}
