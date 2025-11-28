package com.live.main.profile.database.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUploadRequest {
  private MultipartFile file;
}
