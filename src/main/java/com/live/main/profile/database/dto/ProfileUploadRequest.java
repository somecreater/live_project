package com.live.main.profile.database.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUploadRequest {
  private String file_name;
  private MultipartFile file;
}
