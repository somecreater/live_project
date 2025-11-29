package com.live.main.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

  @Value("${app.async.io.core-pool-size}")
  private Integer io_core_pool_size;
  @Value("${app.async.io.max-pool-size}")
  private Integer io_max_pool_size;
  @Value("${app.async.io.queue-capacity}")
  private Integer io_queue_capacity;

  @Value("${app.async.cpu.core-pool-size}")
  private Integer cpu_core_pool_size;
  @Value("${app.async.cpu.max-pool-size}")
  private Integer cpu_max_pool_size;
  @Value("${app.async.cpu.queue-capacity}")
  private Integer cpu_queue_capacity;

  @Bean(name = "IOTaskExecutor")
  public Executor IOTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(io_core_pool_size);
    executor.setMaxPoolSize(io_max_pool_size);
    executor.setQueueCapacity(io_queue_capacity);
    executor.setThreadNamePrefix("IOExecutor-");
    executor.initialize();
    return executor;
  }

  @Bean(name = "CPUTaskExecutor")
  public Executor CPUTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(cpu_core_pool_size);
    executor.setMaxPoolSize(cpu_max_pool_size);
    executor.setQueueCapacity(cpu_queue_capacity);
    executor.setThreadNamePrefix("CPUExecutor-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
    return new AsyncUncaughtExceptionHandler() {
      @Override
      public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("비동기 메소드 예외 발생. 메소드명={}, 파라미터={}, 예외={}",
                method.getName(), params, ex.getMessage(), ex);
      }
    };
  }

}
