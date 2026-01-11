package com.live.main.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class AWSConfig {

  @Value("${spring.cloud.aws.region.static}")
  private String region;

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
             .region(Region.of(region))
             .build();
  }
}
