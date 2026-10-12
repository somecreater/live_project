package com.live.main.video.service.Interface;

import com.live.main.video.database.dto.CompletePartRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.net.URL;
import java.util.List;

/**CloudFlare R2 관련 각종 기능*/
public interface R2ServiceInterface {

    /** 클라이언트가 단일 객체를 R2에 직접 PUT할 수 있는 임시 Presigned URL을 생성 */
    public URL createPutPresignedUrl(String objectKey, String contentType);
    /** R2 Multipart Upload를 시작하고 이후 Part 업로드에 사용할 uploadId를 반환 */
    public String createMultipartUpload(String objectKey, String contentType);
    /** 지정된 Multipart Part를 R2에 직접 업로드할 수 있는 Presigned URL을 생성 */
    public URL createPartPresignedUrl(String objectKey, String uploadId, int partNumber);
    /** 클라이언트가 전달한 Part 번호와 ETag를 이용해 R2 Multipart Upload를 최종 결합 */
    public void completeMultipartUpload(String objectKey, String uploadId, List<CompletePartRequest> parts);
    /** 더 이상 사용하지 않는 R2 Multipart Upload를 취소해 업로드된 임시 Part를 정리 */
    public void abortMultipartUpload(String objectKey, String uploadId);

    /** 객체 본문을 내려받지 않고 R2 객체의 존재 여부와 크기·메타데이터를 조회 */
    public HeadObjectResponse headObject(String objectKey);
    /** HEAD 요청을 이용해 지정된 R2 객체가 실제로 존재하는지 확인 */
    public boolean objectExists(String objectKey);
    /** 객체 전체를 읽지 않고 지정된 바이트 범위만 가져온다. 영상 헤더 검사 등에 사용 */
    public byte[] readRange(String objectKey, long start, long end);
    /** 지정된 Object Key의 R2 객체를 삭제 */
    public void deleteObject(String objectKey);
}
