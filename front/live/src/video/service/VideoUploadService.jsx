import axios from "axios";
import ApiService from "../../common/api/ApiService";

/**
 * 동영상 업로드 서비스
 * 동영상 크기에 따라 일반 PUT 업로드와 Multipart 업로드를 구분하여 처리합니다.
 */
class VideoUploadService {
    // 100MB 기준 (추후 조정 가능)
    static MULTIPART_THRESHOLD = 100 * 1024 * 1024;
    static PRESIGN_BATCH_SIZE = 10; // URL 10개씩 요청
    static CONCURRENCY = 4; // 동시 업로드 4개
    static MAX_RETRIES = 3; // 재시도 3번


    /** 사용자 정보/내 채널 응답에서 업로드에 필요한 인증 컨텍스트를 찾는다. */
    static async resolveUploadContext() {
        const [userResponse, channelResponse] = await Promise.all([
            ApiService.user.info(),
            ApiService.channel.my_channel(),
        ]);

        const candidates = (response) => {
            const raw = response?.data ?? response;
            return raw;
        };

        const firstString = (items, names) => {
            for (const item of items) {
                for (const name of names) {
                    if (typeof item?.[name] === "string" && item[name].trim()) {
                        return item[name].trim();
                    }
                }
            }
            return null;
        };

        const userLoginId = firstString(candidates(userResponse), [
            "loginId", "login_id", "userLoginId", "user_login_id",
        ]);
        const channelName = firstString(candidates(channelResponse), [
            "name", "channelName", "channel_name",
        ]);

        if (!userLoginId) {
            throw new Error("회원 정보에서 loginId를 찾을 수 없습니다. /api/user/info 응답을 확인해주세요.");
        }
        if (!channelName) {
            throw new Error("내 채널 정보를 찾을 수 없습니다. /api/channel/my_channel 응답을 확인해주세요.");
        }

        return { userLoginId, channelName };
    }

    /** 실제 파일을 기준으로 메타데이터를 보정한다. */
    static buildVideoDto(file, videoData) {
        const extension = file.name.split(".").pop()?.toLowerCase();
        if (!file.size || !["mp4", "mov"].includes(extension)) {
            throw new Error("MP4 또는 MOV 파일만 업로드할 수 있습니다.");
        }

        return {
            ...videoData,
            title: videoData.title.trim(),
            description: videoData.description?.trim() ?? "",
            file_type: extension,
            size: file.size,
            visibility: videoData.visibility === "PUBLIC" ? "PUBLIC" : "PRIVATE",
            allow_comments: Boolean(videoData.allow_comments),
            duration_seconds: Number(videoData.duration_seconds) || 0,
            category: videoData.category || "ETC",
        };
    }

    static notify(onProgress, data) {
        onProgress?.(data);
    }

    static assertNotCanceled(signal) {
        if (signal?.aborted) {
            throw new DOMException("업로드가 취소되었습니다.", "AbortError");
        }
    }

    static isCanceled(error, signal) {
        return signal?.aborted || error?.name === "AbortError" || error?.code === "ERR_CANCELED";
    }

    /**
     * 동영상 업로드 시작
     * @param {File} file - 업로드할 파일
     * @param {Object} videoData - 이전 폼에서 입력받은 동영상 메타데이터
     * @param {function} onProgress - 진행률 콜백 (0~100)
     * @param {Object} signal - 비동기 처리 과정 제어
     */
    static async upload(file, videoData, onProgress, { signal } = {}) {
        if (!(file instanceof File)) {
            throw new Error("업로드할 파일이 없습니다.");
        }

        const dto = this.buildVideoDto(file, videoData);
        this.assertNotCanceled(signal);
        this.notify(onProgress, { phase: "preparing", percent: 0, uploadedBytes: 0, totalBytes: file.size });

        const context = await this.resolveUploadContext();
        this.assertNotCanceled(signal);

        // 소용량 업로드, 멀티파트 업로드 부분 간소화
        const result = file.size < this.MULTIPART_THRESHOLD
            ? await this.uploadSinglePut(file, dto, context, onProgress, signal)
            : await this.uploadMultipart(file, dto, context, onProgress, signal);

        // PUT 또는 Multipart Complete 이후에 검증을 요청한다. READY는 Kafka 트랜스코딩 후 별도로 전환된다.
        this.notify(onProgress, {
            phase: "validating", percent: 99, uploadedBytes: file.size,
            totalBytes: file.size, videoId: result.videoId,
        });

        await ApiService.video.validateUploadedVideo(result.videoId, context);
        this.notify(onProgress, {
            phase: "accepted", percent: 100, uploadedBytes: file.size,
            totalBytes: file.size, videoId: result.videoId,
        });

        return { result: true, videoId: result.videoId, processingStatus: "TRANSCODING" };
    }

    /**
     * 단일 PUT 업로드 (Small Files)
     */
    static async uploadSinglePut(file, dto, context, onProgress, signal) {
        try {
            /** 
             * 1. 업로드용 Presigned URL 요청
             * 백엔드 API인 createSingleUpload은 동영상 정보를 저장하고 
             * DB에 저장된 동영상에 대한 아이디와 업로드 가능한 PUT Presigned URL을 반환합니다.
             */
            const { data } = await ApiService.video.createSingleUpload(dto, context);
            const { videoId, uploadUrl } = data ?? {};
            if (!videoId || !uploadUrl) {
                throw new Error("단일 업로드 응답에 videoId 또는 uploadUrl이 없습니다.");
            }
            this.assertNotCanceled(signal);

            // 2. Axios를 사용하여 직접 PUT 업로드
            // URL 서명에 포함된 Content-Type과 동일하게 전달해야 한다.
            const contentType = dto.file_type === "mov" ? "video/quicktime" : "video/mp4";
            await axios.put(uploadUrl, file, {
                headers: { "Content-Type": contentType },
                signal,
                timeout: 0,
                withCredentials: false,
                onUploadProgress: ({ loaded }) => {
                    const uploaded = Math.min(file.size, loaded || 0);
                    this.notify(onProgress, {
                        phase: "uploading", percent: Math.min(99, Math.floor(uploaded / file.size * 100)),
                        uploadedBytes: uploaded, totalBytes: file.size, videoId,
                    });
                },
            });

            this.notify(onProgress, { phase: "finalizing", percent: 99, uploadedBytes: file.size, totalBytes: file.size, videoId });
            // 업로드 완료 후 비디오 아이디를 반환
            return { videoId };
        } catch (error) {
            console.error("Single PUT Upload Error:", error);
            throw error;
        }
    }

    /**
     * 멀티파트 업로드 (Large Files)
     * 대용량 파일을 청크 단위로 나누어 병렬/순차적으로 업로드합니다.
     */
    static async uploadMultipart(file, dto, context, onProgress, signal) {
        let session = null;
        let completionStarted = false;

        try {
            const { data } = await ApiService.video.createMultipartUpload(dto, context);
            session = data;
            const { videoId, uploadId, partSize, totalPartCount } = session ?? {};
            if (!videoId || !uploadId || !Number.isSafeInteger(partSize) || partSize <= 0 ||
                !Number.isSafeInteger(totalPartCount) || totalPartCount <= 0 ||
                Math.ceil(file.size / partSize) !== totalPartCount) {
                throw new Error("Multipart 세션 응답이 올바르지 않습니다.");
            }

            this.assertNotCanceled(signal);
            const completed = [];
            const progressByPart = new Map();

            const updateProgress = (partNumber, loaded, size) => {
                progressByPart.set(partNumber, Math.min(size, Math.max(0, loaded)));
                const uploadedBytes = [...progressByPart.values()].reduce((sum, value) => sum + value, 0);
                this.notify(onProgress, {
                    phase: "uploading", percent: Math.min(99, Math.floor(uploadedBytes / file.size * 100)),
                    uploadedBytes, totalBytes: file.size, videoId, uploadId, totalPartCount,
                });
            };
            for (let startPart = 1; startPart <= totalPartCount; startPart += this.PRESIGN_BATCH_SIZE) {
                this.assertNotCanceled(signal);
                const endPart = Math.min(totalPartCount, startPart + this.PRESIGN_BATCH_SIZE - 1);
                const partNumbers = Array.from({ length: endPart - startPart + 1 }, (_, i) => startPart + i);
                const response = await ApiService.video.createMultipartPartUrls(
                    { videoId, uploadId, partNumbers }, context
                );

                const urls = new Map((response.data ?? []).map(({ partNumber, uploadUrl }) => [partNumber, uploadUrl]));
                if (urls.size !== partNumbers.length) {
                    throw new Error("요청한 모든 Part의 Presigned URL을 받지 못했습니다.");
                }

                let nextIndex = 0;
                let firstError = null;
                const worker = async () => {
                    while (nextIndex < partNumbers.length && !firstError) {
                        this.assertNotCanceled(signal);
                        const partNumber = partNumbers[nextIndex++];
                        const begin = (partNumber - 1) * partSize;
                        const end = Math.min(file.size, begin + partSize);
                        const size = end - begin;
                        const url = urls.get(partNumber);
                        if (!url) throw new Error(`Part ${partNumber} 업로드 URL이 없습니다.`);
                        const blob = file.slice(begin, end);

                        try {
                            const etag = await this.uploadPartWithRetry(
                                url, blob, signal,
                                (loaded) => updateProgress(partNumber, loaded, size)
                            );
                            updateProgress(partNumber, size, size);
                            completed.push({ partNumber, etag });
                        } catch (error) {
                            firstError = error;
                            throw error;
                        }
                    }
                };
                const workers = Array.from(
                    { length: Math.min(this.CONCURRENCY, partNumbers.length) },
                    () => worker()
                );
                // 모든 워커가 정리된 뒤 오류를 전달한다 (미완료 요청이 남지 않도록).
                const outcomes = await Promise.allSettled(workers);
                const failed = outcomes.find((item) => item.status === "rejected");
                if (failed) throw failed.reason;
            }

            this.assertNotCanceled(signal);
            if (completed.length !== totalPartCount) {
                throw new Error("모든 Part가 업로드되지 않았습니다.");
            }

            completed.sort((a, b) => a.partNumber - b.partNumber);
            completionStarted = true;
            this.notify(onProgress, {
                phase: "finalizing", percent: 99, uploadedBytes: file.size,
                totalBytes: file.size, videoId, uploadId,
            });
            await ApiService.video.completeMultipartUpload({ videoId, uploadId, parts: completed }, context);
            return { videoId };
        } catch (error) {
            // 사용자 요청으로 취소한 경우에만 Abort. 일반 오류는 서버의 만료 정리/복구 대상이다.
            if (this.isCanceled(error, signal) && session?.videoId && session?.uploadId && !completionStarted) {
                try {
                    await ApiService.video.abortMultipartUpload(
                        { videoId: session.videoId, uploadId: session.uploadId }, context
                    );
                } catch (abortError) {
                    console.error("Multipart 취소 API 실패:", abortError);
                }
            }
            throw error;
        }
    }

    /** 네트워크 일시 오류에 한해 Part 전송을 최대 3회 시도한다. */
    static async uploadPartWithRetry(url, blob, signal, onLoaded) {
        let lastError;
        for (let attempt = 1; attempt <= this.MAX_RETRIES; attempt++) {
            this.assertNotCanceled(signal);
            try {
                const response = await axios.put(url, blob, {
                    headers: { "Content-Type": "application/octet-stream" },
                    signal,
                    timeout: 0,
                    withCredentials: false,
                    onUploadProgress: ({ loaded }) => onLoaded?.(loaded || 0),
                });
                const etag = response.headers?.etag;
                if (!etag) {
                    throw new Error("R2 ETag 헤더가 없습니다. R2 CORS ExposeHeaders에 ETag를 추가해주세요.");
                }
                return etag;
            } catch (error) {
                lastError = error;
                if (this.isCanceled(error, signal)) throw error;
                const status = error.response?.status;
                const retryable = !status || status === 408 || status === 429 || status >= 500;
                if (!retryable || attempt === this.MAX_RETRIES || !error.isAxiosError) break;
                onLoaded?.(0); // 실패한 요청에서 기록된 전송량을 다시 계산한다.
                await this.delay(500 * (2 ** (attempt - 1)), signal);
            }
        }
        throw lastError;
    }

    static delay(ms, signal) {
        return new Promise((resolve, reject) => {
            if (signal?.aborted) return reject(new DOMException("취소됨", "AbortError"));
            const timer = setTimeout(() => { cleanup(); resolve(); }, ms);
            const cancel = () => { clearTimeout(timer); cleanup(); reject(new DOMException("취소됨", "AbortError")); };
            const cleanup = () => signal?.removeEventListener("abort", cancel);
            signal?.addEventListener("abort", cancel, { once: true });
        });
    }

}

export default VideoUploadService;
