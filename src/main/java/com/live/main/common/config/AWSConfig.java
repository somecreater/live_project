package com.live.main.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class AWSConfig {

  @Value("${spring.cloud.aws.region.static}")
  private String region;

  @Bean("awsPresigner")
  @Primary
  public S3Presigner awsPresigner() {
    log.info("AWS S3 Presigner initialized with region: {}", region);
    return S3Presigner.builder()
             .region(Region.of(region))
             .build();
  }
}
