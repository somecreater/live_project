import axios from "axios";
import ApiService from "../../common/api/ApiService";

/**
 * 동영상 업로드 서비스
 * 동영상 크기에 따라 일반 PUT 업로드와 Multipart 업로드를 구분하여 처리합니다.
 */
class VideoUploadService {
    // 100MB 기준 (추후 조정 가능)
    static MULTIPART_THRESHOLD = 100 * 1024 * 1024;

    /**
     * 동영상 업로드 시작
     * @param {File} file - 업로드할 파일
     * @param {Object} videoData - 이전 폼에서 입력받은 동영상 메타데이터
     * @param {function} onProgress - 진행률 콜백 (0~100)
     */
    static async upload(file, videoData, onProgress) {
        if (file.size < this.MULTIPART_THRESHOLD) {
            return this.uploadSinglePut(file, videoData, onProgress);
        } else {
            return this.uploadMultipart(file, videoData, onProgress);
        }
    }

    /**
     * 단일 PUT 업로드 (Small Files)
     */
    static async uploadSinglePut(file, videoData, onProgress) {
        try {
            /** 
             * 1. 업로드용 Presigned URL 요청
             * 백엔드 API인 upload_url은 동영상 정보를 저장하고 
             * DB에 저장된 동영상에 대한 아이디와 업로드 가능한 PUT Presigned URL을 반환합니다.
             */
            const response = await ApiService.video.upload_url({
                ...videoData
            });

            const { uploadUrl, video_id } = response.data;

            if (!uploadUrl) {
                throw new Error("업로드 URL을 가져오지 못했습니다.");
            }

            // 2. Axios를 사용하여 직접 PUT 업로드
            const uploadResponse = await axios.put(uploadUrl, file, {
                headers: {
                    "Content-Type": file.type || "video/mp4",
                },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round(
                        (progressEvent.loaded * 100) / progressEvent.total
                    );
                    onProgress(percentCompleted);
                },
            });

            // 업로드 완료 후 비디오 아이디를 포함하여 반환
            return {
                ...uploadResponse,
                video_id: video_id
            };
        } catch (error) {
            console.error("Single PUT Upload Error:", error);
            throw error;
        }
    }

    /**
     * 멀티파트 업로드 (Large Files)
     * 대용량 파일을 청크 단위로 나누어 병렬/순차적으로 업로드합니다.
     */
    static async uploadMultipart(file, videoData, onProgress) {
        const PRESIGN_BATCH_SIZE = 10;       // URL 10개씩 요청
        const CONCURRENCY = 4;               // 동시 업로드 4개
        const MAX_RETRIES = 3;
        const RETRY_BASE_DELAY_MS = 1000;

        try {
            console.log("Multipart upload started for file size:", file.size);

            // TODO: 아래는 구현 가이드라인입니다.

            /* 1. 멀티파트 업로드 초기화 (서버로부터 uploadId 발급)
               const initResponse = await ApiService.video.multipart_upload_url_request({ 
                   id: videoId,
                   file_name: file.name,
                   total_chunks: Math.ceil(file.size / CHUNK_SIZE)
               });
               const { uploadId } = initResponse.data;
            */

            /* 2. 파일을 청크로 분할하여 개별 파트 업로드
               - 각 파트별로 Presigned URL을 발급받거나 사전에 받은 URL 사용
               - Axios 등을 사용하여 각 파트 업로드
               - 업로드 성공 시 ETag 등의 정보를 수집
            */

            /* 3. 멀티파트 업로드 완료 요청 (수집된 ETag들과 함께)
               await ApiService.video.multipart_upload_complete({
                   id: videoId,
                   uploadId: uploadId,
                   parts: [ { partNumber: 1, etag: "..." }, ... ]
               });
            */

            // 현재는 100MB 이상일 경우 에러 메시지로 안내
            throw new Error(`대용량 파일(${Math.round(file.size / 1024 / 1024)}MB) 업로드를 위해 멀티파트 로직 구현이 필요합니다. (VideoUploadService.js 참조)`);

        } catch (error) {
            console.error("Multipart Upload Error:", error);
            throw error;
        }
    }
}

export default VideoUploadService;
