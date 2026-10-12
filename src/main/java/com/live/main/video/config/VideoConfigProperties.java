package com.live.main.video.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.r2")
//@Validated
public record VideoConfigProperties(
  @NotBlank String videoBuketName,
  @NotBlank String originalVideoBucketName,
  @NotBlank String transcodingVideoBucketName,
  @NotBlank String cdnBaseUrl,
  @Positive long videoLimitSize,
  @Positive long multiPartSize,
  @Positive long videoUploadUrlDuration,
  @Positive long partUrlDuration
) {

    public String normalizedOriginalPrefix() {
        return normalizePrefix(originalVideoBucketName);
    }

    public String normalizedTranscodingPrefix() {
        return normalizePrefix(transcodingVideoBucketName);
    }

    public String normalizedCdnBaseUrl() {
        return cdnBaseUrl.endsWith("/")
                ? cdnBaseUrl.substring(0, cdnBaseUrl.length() - 1)
                : cdnBaseUrl;
    }

    private static String normalizePrefix(String value) {
        String normalized = value.startsWith("/") ? value.substring(1) : value;
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }
}
