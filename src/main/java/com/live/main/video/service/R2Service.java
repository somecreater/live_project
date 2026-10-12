package com.live.main.video.service;

import com.live.main.video.config.VideoConfigProperties;
import com.live.main.video.database.dto.CompletePartRequest;
import com.live.main.video.service.Interface.R2ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class R2Service implements R2ServiceInterface {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final VideoConfigProperties properties;


    public R2Service(
            @Qualifier("r2Client") S3Client s3Client,
            @Qualifier("r2Presigner") S3Presigner s3Presigner,
            VideoConfigProperties properties
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    @Override
    public URL createPutPresignedUrl(String objectKey, String contentType) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(properties.videoBuketName())
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(
                                properties.videoUploadUrlDuration()
                        ))
                        .putObjectRequest(objectRequest)
                        .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }

    @Override
    public String createMultipartUpload(String objectKey, String contentType) {
        CreateMultipartUploadRequest request =
                CreateMultipartUploadRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .contentType(contentType)
                        .build();

        return s3Client.createMultipartUpload(request).uploadId();
    }

    @Override
    public URL createPartPresignedUrl(String objectKey, String uploadId, int partNumber) {
        UploadPartRequest uploadPartRequest =
                UploadPartRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

        UploadPartPresignRequest presignRequest =
                UploadPartPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(
                                properties.partUrlDuration()
                        ))
                        .uploadPartRequest(uploadPartRequest)
                        .build();

        return s3Presigner.presignUploadPart(presignRequest).url();
    }

    @Override
    public void completeMultipartUpload(String objectKey, String uploadId, List<CompletePartRequest> parts) {
        List<CompletedPart> completedParts = parts.stream()
                .sorted(Comparator.comparingInt(
                        CompletePartRequest::getPartNumber
                ))
                .map(part -> CompletedPart.builder()
                        .partNumber(part.getPartNumber())
                        .eTag(part.getEtag().trim())
                        .build()
                )
                .toList();

        CompleteMultipartUploadRequest request =
                CompleteMultipartUploadRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .uploadId(uploadId)
                        .multipartUpload(
                                CompletedMultipartUpload.builder()
                                        .parts(completedParts)
                                        .build()
                        )
                        .build();

        s3Client.completeMultipartUpload(request);
    }

    @Override
    public void abortMultipartUpload(String objectKey, String uploadId) {
        AbortMultipartUploadRequest request =
                AbortMultipartUploadRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .uploadId(uploadId)
                        .build();

        s3Client.abortMultipartUpload(request);
    }

    @Override
    public HeadObjectResponse headObject(String objectKey) {
        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .build()
        );
    }

    @Override
    public boolean objectExists(String objectKey) {
        try {
            headObject(objectKey);
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }
            throw exception;
        }
    }

    @Override
    public byte[] readRange(String objectKey, long start, long end) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(properties.videoBuketName())
                .key(objectKey)
                .range("bytes=" + start + "-" + end)
                .build();

        ResponseBytes<GetObjectResponse> response =
                s3Client.getObjectAsBytes(request);

        return response.asByteArray();
    }

    @Override
    public void deleteObject(String objectKey) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(properties.videoBuketName())
                        .key(objectKey)
                        .build()
        );
    }
}
