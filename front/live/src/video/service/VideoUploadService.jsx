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
        const CHUNK_SIZE = 50 * 1024 * 1024;
        const PRESIGN_BATCH_SIZE = 10;       // URL 10개씩 요청
        const CONCURRENCY = 4;               // 동시 업로드 4개
        const MAX_RETRIES = 3;
        const RETRY_BASE_DELAY_MS = 1000;
        let uploadId = null;

        const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

        const splitFileIntoParts = (targetFile, chunkSize) => {
            const parts = [];
            let start = 0;
            let partNumber = 1;

            while (start < targetFile.size) {
                const end = Math.min(start + chunkSize, targetFile.size);

                parts.push({
                    partNumber,
                    start,
                    end,
                    size: end - start,
                    blob: targetFile.slice(start, end)
                });

                start = end;
                partNumber++;
            }

            return parts;
        };

        const chunkArray = (array, size) => {
            const result = [];
            for (let i = 0; i < array.length; i += size) {
                result.push(array.slice(i, i + size));
            }
            return result;
        };

        const shouldRetryUploadError = (error) => {
            const status = error?.response?.status;

            if (!status) {
                return true;
            }

            if (status >= 500 || status === 408 || status === 429) {
                return true;
            }

            return false;
        };

        const retryUploadPart = async (task, maxRetries = MAX_RETRIES) => {
            let lastError = null;

            for (let attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    return await task();
                } catch (error) {
                    lastError = error;

                    if (!shouldRetryUploadError(error) || attempt === maxRetries) {
                        break;
                    }

                    const delay = RETRY_BASE_DELAY_MS * attempt;
                    console.warn(`Part upload retry ${attempt}/${maxRetries}, wait ${delay}ms`, error);
                    await sleep(delay);
                }
            }

            throw lastError;
        };

        const uploadSinglePart = async ({ part, url, onPartProgress }) => {
            const response = await axios.put(url, part.blob, {
                headers: {
                    "Content-Type": "application/octet-stream"
                },
                onUploadProgress: (progressEvent) => {
                    const loaded = progressEvent.loaded || 0;
                    if (onPartProgress) {
                        onPartProgress(loaded);
                    }
                }
            });

            const etag =
                response.headers?.etag ||
                response.headers?.ETag ||
                response.headers?.["etag"] ||
                response.headers?.["ETag"];

            if (!etag) {
                throw new Error(`part ${part.partNumber} 업로드 응답에 ETag가 없습니다. R2 CORS ExposeHeaders 설정을 확인하세요.`);
            }

            return {
                partNumber: part.partNumber,
                etag,
                size: part.size
            };
        };

        const uploadPartsInParallel = async ({
            parts,
            presignedParts,
            onBatchProgress,
            onBatchPartCompleted
        }) => {
            const results = [];
            let currentIndex = 0;

            const urlMap = new Map();
            for (const item of presignedParts) {
                const partNumber = item.partNumber ?? item.part_number;
                const url = item.url;
                urlMap.set(partNumber, url);
            }

            const worker = async () => {
                while (true) {
                    const index = currentIndex++;
                    if (index >= parts.length) {
                        return;
                    }

                    const part = parts[index];
                    const url = urlMap.get(part.partNumber);

                    if (!url) {
                        throw new Error(`part ${part.partNumber}의 presigned URL이 없습니다.`);
                    }

                    const uploadedPart = await retryUploadPart(() =>
                        uploadSinglePart({
                            part,
                            url,
                            onPartProgress: (loaded) => {
                                if (onBatchProgress) {
                                    onBatchProgress({
                                        partNumber: part.partNumber,
                                        loaded
                                    });
                                }
                            }
                        })
                    );

                    results.push(uploadedPart);

                    if (onBatchPartCompleted) {
                        onBatchPartCompleted(uploadedPart);
                    }
                }
            };

            const workerCount = Math.min(CONCURRENCY, parts.length);
            await Promise.all(Array.from({ length: workerCount }, () => worker()));

            return results.sort((a, b) => a.partNumber - b.partNumber);
        };

        try {

            if (!file) {
                throw new Error("업로드할 파일이 없습니다.");
            }

            if (!videoData) {
                throw new Error("videoData가 없습니다.");
            }

            console.log("Multipart upload started for file size:", file.size);

            // 멀티파트 업로드에 필요한 상태 객체들 (지역 변수)
            let multipartUploadRequest = {
                videoId: "",
                key: "",
                uploadId: "",
                partSize: 0,
                totalPartCount: 0
            };

            let presignPartsRequest = {
                videoId: "",
                key: "",
                uploadId: "",
                partNumbers: []
            };

            let partPresignedUrlResponse = [];

            let completeUploadRequest = {
                videoId: "",
                key: "",
                uploadId: "",
                parts: []
            };
            videoData.size = file.size;
            const initResponse = await ApiService.video.multipart_upload_url_request({
                ...videoData
            });
            multipartUploadRequest = initResponse.data.multipart_upload_request;

            if (!multipartUploadRequest) {
                throw new Error("multipart_upload_request 응답이 없습니다.");
            }

            if (!multipartUploadRequest.videoId
                || !multipartUploadRequest.key
                || !multipartUploadRequest.uploadId
                || !multipartUploadRequest.partSize
                || !multipartUploadRequest.totalPartCount) {
                console.log(multipartUploadRequest.videoId);
                console.log(multipartUploadRequest.key);
                console.log(multipartUploadRequest.uploadId);
                console.log(multipartUploadRequest.partSize);
                console.log(multipartUploadRequest.totalPartCount);
                throw new Error("multipart_upload_request 응답이 올바르지 않습니다.");
            }

            const allParts = splitFileIntoParts(file, multipartUploadRequest.partSize);

            if (allParts.length !== multipartUploadRequest.totalPartCount) {
                throw new Error(`분할된 파트 수(${allParts.length})가 예상 수(${multipartUploadRequest.totalPartCount})와 일치하지 않습니다.`);
            }

            const totalPartCount = multipartUploadRequest.totalPartCount;
            const partBatches = chunkArray(allParts, PRESIGN_BATCH_SIZE);

            const progressMap = new Map();
            let completedBytes = 0;
            let inFlightLoadedTotal = 0;
            const uploadedParts = [];

            for (const batchParts of partBatches) {
                presignPartsRequest = {
                    videoId: multipartUploadRequest.videoId,
                    key: multipartUploadRequest.key,
                    uploadId: multipartUploadRequest.uploadId,
                    partNumbers: batchParts.map((part) => part.partNumber)
                };

                const partUrlResponse = await ApiService.video.multipart_upload_url({
                    ...presignPartsRequest
                });

                partPresignedUrlResponse = partUrlResponse.data.multipart_upload_url;

                if (!partPresignedUrlResponse
                    || !Array.isArray(partPresignedUrlResponse)
                    || partPresignedUrlResponse.length == 0) {
                    throw new Error("multipart_upload_url 응답이 올바르지 않습니다.");
                }

                const batchUploadedParts = await uploadPartsInParallel({
                    parts: batchParts,
                    presignedParts: partPresignedUrlResponse,
                    onBatchProgress: ({ partNumber, loaded }) => {
                        const previousLoaded = progressMap.get(partNumber) || 0;
                        progressMap.set(partNumber, loaded);

                        inFlightLoadedTotal += (loaded - previousLoaded);

                        const currentUploadedBytes = completedBytes + inFlightLoadedTotal;
                        const percent = Math.min(100, Math.round((currentUploadedBytes / file.size) * 100));

                        if (onProgress) {
                            onProgress({
                                percent,
                                uploadedBytes: currentUploadedBytes,
                                totalBytes: file.size,
                                videoId: multipartUploadRequest.videoId,
                                uploadId: multipartUploadRequest.uploadId,
                                totalPartCount
                            });
                        }
                    },
                    onBatchPartCompleted: (uploadedPart) => {
                        const previousLoaded = progressMap.get(uploadedPart.partNumber) || 0;
                        progressMap.delete(uploadedPart.partNumber);

                        inFlightLoadedTotal -= previousLoaded;
                        completedBytes += uploadedPart.size;

                        const percent = Math.min(100, Math.round((completedBytes / file.size) * 100));

                        if (onProgress) {
                            onProgress({
                                percent,
                                uploadedBytes: completedBytes,
                                totalBytes: file.size,
                                videoId: multipartUploadRequest.videoId,
                                uploadId: multipartUploadRequest.uploadId,
                                totalPartCount
                            });
                        }
                    }
                });

                uploadedParts.push(...batchUploadedParts);
            }

            completeUploadRequest = {
                videoId: multipartUploadRequest.videoId,
                key: multipartUploadRequest.key,
                uploadId: multipartUploadRequest.uploadId,
                parts: uploadedParts.map((part) => ({
                    partNumber: part.partNumber,
                    etag: part.etag
                })).sort((a, b) => a.partNumber - b.partNumber)
            };

            await ApiService.video.multipart_upload_complete({
                ...completeUploadRequest
            });

            if (onProgress) {
                onProgress({
                    percent: 100,
                    uploadedBytes: file.size,
                    totalBytes: file.size,
                    videoId: multipartUploadRequest.videoId,
                    uploadId: multipartUploadRequest.uploadId,
                    totalPartCount
                });
            }

            return {
                result: true,
                multipartUploadRequest,
                completeUploadRequest
            };
        } catch (error) {
            console.error("Multipart Upload Error:", error);

            if (uploadId) {
                try {
                    await ApiService.video.multipart_upload_abort({
                        video_id: multipartUploadRequest.videoId,
                        key: multipartUploadRequest.key,
                        upload_id: multipartUploadRequest.uploadId
                    });
                } catch (abortError) {
                    console.error("Multipart abort failed:", abortError);
                }
            }
            throw error;
        }
    }
}

export default VideoUploadService;
