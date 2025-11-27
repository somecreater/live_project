package com.live.main.common.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

  @Bean
  public Tika tika(){
    return new Tika();
  }
}
