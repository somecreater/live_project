# Live Project

실시간 스트리밍 및 라이브 방송 플랫폼을 위한 풀스택 애플리케이션입니다.

## 🚀 프로젝트 개요

이 프로젝트는 실시간 비디오 스트리밍, 사용자 관리, 구독 시스템, 게시물 관리 등의 기능을 제공하는 종합적인 라이브 방송 플랫폼입니다.

## 🏗️ 기술 스택

### Backend
- **Java 21** - 메인 프로그래밍 언어
- **Spring Boot 3.5.4** - 웹 애플리케이션 프레임워크
- **Spring Security** - 인증 및 권한 관리
- **Spring Data JPA** - 데이터베이스 접근
- **MySQL** - 메인 데이터베이스
- **Redis** - 세션 및 캐시 관리
- **WebSocket** - 실시간 통신
- **JWT** - 토큰 기반 인증
- **Spring Batch** - 배치 처리
- **Quartz** - 스케줄링

### Frontend
- **JavaScript** - 프론트 엔드 프로그래밍 언어
- **React 19** - 사용자 인터페이스
- **Vite** - 빌드 도구
- **ESLint** - 코드 품질 관리

### Infra & DevOps & Monitoring
- **Github Action** - CI/CD 파이프라인
- **Github** - 코드 버전 관리
- **AWS Route 53** - DNS 서버 운영
- **AWS EC2** - API 서버 운영
- **AWS RDS** - 데이터베이스 운영
- **AWS ElastiCache** - 캐시 데이터베이스 운영
- **AWS S3** - 파일 저장소
- **AWS SNS** - 이벤트 알림 전송
- **Docker** - 컨테이너화
- **Docker Compose** - 멀티 컨테이너 관리
- **Prometheus** - 메트릭 수집
- **Grafana** - 모니터링 대시보드

### Streaming Infrastructure
- **FFmpeg** - 비디오 인코딩 및 실시간 스트리밍
- **VMware** - 가상화 환경에서 인코딩 처리
- **AWS CloudFront** - 글로벌 CDN 배포
- **hls.js** - HLS 플레이어 라이브러리

## 📁 프로젝트 구조(추후 변경 가능성이 있습니다!)

```
demo/
├── src/main/java/com/live/
│   ├── common/          # 공통 유틸리티
│   ├── demo/            # 메인 애플리케이션
│   ├── post/            # 게시물 관리
│   ├── streaming/       # 실시간 스트리밍
│   ├── subscription/    # 구독 시스템
│   ├── user/            # 사용자 관리
│   └── video/           # 비디오 관리
├── front/live/          # React 프론트엔드
├── monitoring/          # 모니터링 설정
├── nginx/              # Nginx 설정(선택)
└── streaming/          # 스트리밍 관련 설정
```

## 🚀 시작하기

### 사전 요구사항
- Java 21
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0+
- Redis 6.0+
- FFmpeg 4.0+
- VMware/VirtualBox (가상화 환경)
- AWS (S3, CloudFront 등)

### 백엔드 실행

1. **의존성 설치**
   ```bash
   ./gradlew build
   ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

### 프론트엔드 실행

1. **디렉토리 이동**
   ```bash
   cd front/live
   ```

2. **의존성 설치**
   ```bash
   npm install
   ```

3. **개발 서버 실행**
   ```bash
   npm run dev
   ```

### Docker를 통한 전체 실행

```bash
# 모니터링 서비스 실행
docker-compose -f docker-compose.monitoring.yml up -d

# 전체 서비스 실행
docker-compose up -d
```

## 📊 모니터링

프로젝트는 Prometheus와 Grafana를 통한 종합적인 모니터링을 제공합니다.

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin)

## 🎥 스트리밍 아키텍처

### VOD 스트리밍 (온디맨드)
1. **업로드**: 사용자가 비디오 파일을 AWS(VMware)에 업로드
2. **VMware 인코딩**: FFmpeg를 통해 HLS(.m3u8 + .ts) 형식으로 변환
3. **S3 저장**: 인코딩된 파일을 AWS S3 버킷에 저장
4. **CloudFront 배포**: 글로벌 CDN을 통해 전 세계 배포
5. **재생**: React 앱에서 hls.js를 통해 스트리밍 재생

### 실시간 스트리밍
1. **외부 RTSP/RTMP 스트림 수신**: VMware가 외부 IP 카메라/스트림 소스로부터 실시간 스트림 수신
2. **FFmpeg HLS 변환**: VMware에서 FFmpeg를 통해 RTSP/RTMP 스트림을 HLS 세그먼트(.ts)로 변환
3. **실시간 세그먼트 업로드**: 생성된 .ts 파일을 AWS S3에 실시간 업로드
4. **플레이리스트 동기화**: .m3u8 파일을 실시간으로 업데이트하여 최신 세그먼트 정보 제공
5. **CloudFront 글로벌 배포**: AWS CloudFront를 통한 전 세계 사용자에게 실시간 스트리밍 제공

### 설정 예시

#### VMware에서 FFmpeg VOD 인코딩 명령어
```bash
# 비디오 파일을 HLS로 인코딩 (고품질)
ffmpeg -i input.mp4 \
  -c:v libx264 -preset fast -crf 23 \
  -c:a aac -b:a 128k \
  -f hls \
  -hls_time 10 \
  -hls_list_size 0 \
  -hls_segment_filename "output_%03d.ts" \
  output.m3u8

# 빠른 인코딩 (품질 희생)
ffmpeg -i input.mp4 \
  -c:v libx264 -preset ultrafast \
  -c:a aac -b:a 128k \
  -f hls \
  -hls_time 10 \
  -hls_list_size 0 \
  -hls_segment_filename "output_%03d.ts" \
  output.m3u8

# S3에 업로드
aws s3 sync ./ s3://your-video-bucket/videos/video-id/ --exclude "*" --include "*.m3u8" --include "*.ts"
```

#### VMware에서 FFmpeg 실시간 스트리밍 명령어
```bash
# RTSP 스트림을 HLS로 변환 (안정적 연결)
ffmpeg -i rtsp://camera-ip:554/stream \
  -rtsp_transport tcp \
  -stimeout 5000000 \
  -reconnect 1 \
  -reconnect_at_eof 1 \
  -reconnect_streamed 1 \
  -c:v libx264 -preset fast -crf 23 \
  -c:a aac -b:a 128k \
  -f hls \
  -hls_time 2 \
  -hls_list_size 3 \
  -hls_flags delete_segments \
  -hls_segment_filename "live_%03d.ts" \
  live.m3u8 &

# 빠른 스트리밍 (품질 희생)
ffmpeg -i rtsp://camera-ip:554/stream \
  -rtsp_transport tcp \
  -c:v libx264 -preset ultrafast \
  -c:a aac -b:a 128k \
  -f hls \
  -hls_time 2 \
  -hls_list_size 3 \
  -hls_flags delete_segments \
  -hls_segment_filename "live_%03d.ts" \
  live.m3u8 &

# 백그라운드에서 S3 동기화
while true; do
  aws s3 sync ./ s3://your-video-bucket/live/stream-id/ --delete
  sleep 5
done
```

```bash
# IP 카메라
ffmpeg -i rtsp://192.168.1.100:554/stream1

# 보안 카메라 (인증 포함)
ffmpeg -i rtsp://username:password@192.168.1.100:554/stream

# ONVIF 카메라
ffmpeg -i rtsp://192.168.1.100:554/onvif1

# 스트리밍 서버
ffmpeg -i rtsp://streaming-server.com:554/live/stream

# 데스크톱 화면 캡처 (테스트용)
ffmpeg -f x11grab -r 30 -s 1920x1080 -i :0.0 -c:v libx264 -preset ultrafast -f hls live.m3u8

# 웹캠 캡처
ffmpeg -f v4l2 -r 30 -s 1280x720 -i /dev/video0 -c:v libx264 -preset ultrafast -f hls live.m3u8
```

#### React HLS 플레이어 컴포넌트
```jsx
import React, { useRef, useEffect } from 'react';
import Hls from 'hls.js';

export default function VideoPlayer({ url }) {
  const videoRef = useRef();

  useEffect(() => {
    if (Hls.isSupported()) {
      const hls = new Hls();
      hls.loadSource(url);
      hls.attachMedia(videoRef.current);
      return () => hls.destroy();
    } else {
      videoRef.current.src = url;
    }
  }, [url]);

  return <video ref={videoRef} controls style={{ width: '100%' }} />;
}
```

## 🔧 주요 기능

### 사용자 관리
- 회원가입/로그인
- OAuth2 소셜 로그인
- JWT 토큰 기반 인증
- 사용자 프로필 관리

### 실시간 스트리밍
- **HLS 기반 실시간 비디오 스트리밍** (10-30초 지연시간)
- **RTSP/RTMP 스트림 수신 및 HLS 변환** (VMware FFmpeg)
- **AWS S3 + CloudFront 글로벌 배포**
- **실시간 채팅** (WebSocket 기반)
- **방송 관리** (시작/종료/일시정지)
- **다중 해상도 지원** (1080p, 720p, 480p)

### 구독 시스템
- 스트리머 구독
- 구독자 관리
- 알림 시스템

### 게시물 관리
- 게시물 작성/수정/삭제
- 댓글 시스템
- 좋아요 기능

### 파일 관리
- 로컬 파일 시스템, AWS S3를 통한 파일 업로드
- 이미지/비디오 처리
- AWS S3를 통한 비디오 파일 저장

### VOD 스트리밍 (온디맨드)
- VMware에서 FFmpeg를 통한 HLS 인코딩
- AWS S3 + CloudFront 기반 글로벌 배포
- hls.js를 통한 React 플레이어 연동
- 다중 해상도 지원 (1080p, 720p, 480p)

## 🔒 보안

- Spring Security를 통한 인증/인가
- JWT 토큰 기반 세션 관리
- OAuth2 소셜 로그인 지원
- CORS 설정
- XSS/CSRF 방어
- AWS IAM을 통한 S3 접근 제어
- CloudFront Signed URL/Cookie를 통한 비디오 접근 제어

## 📈 성능 최적화

- Redis를 통한 세션 및 캐시 관리
- 데이터베이스 연결 풀링
- 비동기 처리
- VMware 가상머신 리소스 최적화 (CPU, RAM, Storage)
- CloudFront 캐싱 정책 최적화
- S3 → CloudFront 오리진 리전 내 전송으로 대역폭 비용 절약
- HLS 세그먼트 크기 및 지속시간 최적화
- Docker 컨테이너 리소스 최적화

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest
```

## 📝 API 문서

- 나중에 **Notion**을 통해 제공할 예정입니다!

## 💰 비용 최적화

### 현재 아키텍처 (VMware + AWS)
- **VMware**: 로컬 리소스 사용 (무료)
- **AWS S3**: 스토리지 비용만 발생 (GB당 $0.023)
- **CloudFront**: 데이터 전송 비용만 발생 (GB당 $0.085)
- **총 비용**: 월 $10-50 (사용량에 따라)

### 비용 절약 전략
- **S3 Intelligent Tiering**: 자동 비용 최적화
- **CloudFront 캐싱**: 반복 요청 비용 절약
- **HLS 세그먼트 최적화**: 적절한 세그먼트 크기 설정
- **VMware 리소스 최적화**: CPU/RAM 효율적 사용

### 추후 확장 시 비용 비교
- **AWS MediaConvert**: 사용량 기반 과금 (분당 $0.0175)
- **EC2 기반 인코딩**: 고정 비용 + 사용량 비용
- **하이브리드**: VMware + AWS 조합으로 비용 최적화

## 🎬 스트리밍 설정 가이드

### VMware 가상머신 설정

#### **권장 사양 (성능 최적화)**
- **CPU**: 8-16 vCPU (인코딩 성능 고려)
- **RAM**: 16-32GB (비디오 처리용)
- **Storage**: 500GB+ SSD (임시 파일 저장용)
- **Network**: 1Gbps 이상

#### **최소 사양 (테스트용)**
- **CPU**: 4-6 vCPU
- **RAM**: 8-16GB
- **Storage**: 200GB+
- **Network**: 100Mbps 이상

#### **VMware 특정 설정**
- **VMware Tools 설치**: 성능 최적화 및 네트워크 안정성 향상
- **CPU 가상화**: Intel VT-x/AMD-V 활성화
- **메모리 가상화**: EPT/RVI 활성화
- **네트워크 어댑터**: 브리지 모드 설정 (외부 접근용)

### 소프트웨어 설치 및 설정

#### **필수 소프트웨어 설치**
```bash
# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# FFmpeg 설치 (4.1.5 이상 필요)
sudo apt install ffmpeg
ffmpeg -version  # 버전 확인

# Java 21 설치 (Spring Boot API 서버용)
sudo apt install openjdk-21-jdk

# AWS CLI 설치 (S3 업로드용)
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Nginx 설치 (미디어 서버용, 선택사항)
sudo apt install nginx

# 시스템 모니터링 도구
sudo apt install htop iotop nethogs
```

#### **네트워크 및 보안 설정**
```bash
# 방화벽 설정
sudo ufw allow 8080  # Spring Boot API 서버
sudo ufw allow 80    # Nginx (선택사항)
sudo ufw allow 22    # SSH
sudo ufw allow 554  # RTSP (실시간 스트리밍용)
sudo ufw enable

# 작업 디렉토리 생성
sudo mkdir -p /opt/streaming/{temp,output,logs,config}
sudo chown -R $USER:$USER /opt/streaming
chmod 755 /opt/streaming
```

#### **AWS 자격 증명 설정**
```bash
# AWS CLI 설정
aws configure
# AWS Access Key ID 입력
# AWS Secret Access Key 입력
# Default region 입력 (예: us-east-1)
# Default output format 입력 (json)
```

### AWS S3 + CloudFront 설정
1. **S3 버킷 생성**: 비디오 파일 저장용 버킷 생성
2. **CloudFront 배포**: S3 버킷을 오리진으로 하는 배포 생성
3. **캐싱 정책**: `.m3u8`과 `.ts` 파일에 대한 적절한 TTL 설정
4. **CORS 설정**: `Access-Control-Allow-Origin: *` 헤더 추가

### Content-Type 설정
- `.m3u8` 파일: `application/vnd.apple.mpegurl`
- `.ts` 파일: `video/MP2T`

### CloudFront Behaviors 설정
- **Allowed HTTP Methods**: `GET, HEAD, OPTIONS`
- **Cached HTTP Methods**: `GET, HEAD`
- **VOD**: 긴 TTL (1시간~24시간)
- **Live**: 짧은 TTL (0~10초)

### 외부 접근 설정 (실시간 스트리밍용)

#### **방법 1: VMware NAT 모드 + Virtual Network Editor (권장)**
```bash
# VMware Virtual Network Editor 설정
1. Edit → Virtual Network Editor → NAT (vmnet8) → NAT Settings
2. Port Forwarding → Add
3. 설정:
   - Host Port: 8554 → Virtual Machine Port: 554 (RTSP)
   - Host Port: 1935 → Virtual Machine Port: 1935 (RTMP)
   - Virtual Machine IP: VMware IP 주소
   - Type: TCP
```

**사용 예시:**
```
외부 IP 카메라 → rtsp://호스트IP:8554/stream → VMware:554 → FFmpeg 인코딩
```

#### **방법 2: 공유기 포트 포워딩 (선택사항)**
```bash
# 공유기 관리 페이지 → 포트 포워딩 설정
외부 포트 554 → 내부 IP (VMware IP) → 내부 포트 554 (RTSP)
외부 포트 1935 → 내부 IP (VMware IP) → 내부 포트 1935 (RTMP)
```

**사용 예시:**
```
외부 IP 카메라 → rtsp://공유기외부IP:554/stream → VMware:554 → FFmpeg 인코딩
```

#### **권장사항**
- **개발/테스트**: VMware NAT 모드 (간단하고 안전)
- **프로덕션**: 공유기 포트 포워딩 (안정적이고 독립적)
- **보안**: 방화벽 설정 및 IP 화이트리스트 권장

### 성능 최적화 설정

#### **커널 파라미터 최적화**
```bash
# /etc/sysctl.conf에 추가
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216

# 적용
sudo sysctl -p
```

#### **파일 시스템 최적화**
```bash
# ext4 파일 시스템 최적화
sudo tune2fs -O has_journal /dev/sda1
sudo tune2fs -m 0 /dev/sda1
```

### 모니터링 및 로깅

#### **시스템 모니터링**
```bash
# 실시간 시스템 리소스 모니터링
htop

# I/O 모니터링
iotop

# 네트워크 모니터링
nethogs
```

#### **로그 설정**
```bash
# 로그 로테이션 설정
sudo nano /etc/logrotate.d/streaming

# 로그 디렉토리 권한
sudo chmod 755 /opt/streaming/logs
```

### 백업 및 복구

#### **VMware 스냅샷**
- 설정 변경 전 스냅샷 생성
- 정기적인 스냅샷 백업

#### **데이터 백업**
```bash
# 중요 설정 파일 백업
sudo tar -czf /opt/backup/config-$(date +%Y%m%d).tar.gz /opt/streaming/config/
```

## ☁️ AWS 클라우드 마이그레이션 (추후 확장)

### AWS MediaConvert로 전환
- **MediaConvert 작업**: FFmpeg 대신 AWS MediaConvert 사용
- **작업 템플릿**: HLS 인코딩을 위한 MediaConvert 작업 생성
- **IAM 역할**: MediaConvert 서비스 역할 설정
- **S3 권한**: 입력/출력 버킷 접근 권한 설정

### EC2 기반 인코딩
- **EC2 인스턴스**: VMware 대신 EC2에서 FFmpeg 실행
- **오토스케일링**: 트래픽에 따른 자동 확장
- **Spot Instance**: 비용 절약을 위한 Spot Instance 활용

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 다음 단계를 따라주세요:

1. **Fork the Project** - 프로젝트를 포크합니다
2. **Create your Feature Branch** - 새로운 기능 브랜치를 생성합니다
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your Changes** - 변경사항을 커밋합니다
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the Branch** - 브랜치에 푸시합니다
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request** - Pull Request를 생성합니다

### 기여 가이드라인
- 코드 스타일을 일관성 있게 유지해주세요
- 새로운 기능 추가 시 테스트 코드도 함께 작성해주세요
- 문서 업데이트가 필요한 경우 함께 수정해주세요

## 📞 문의

프로젝트에 대한 문의사항이나 버그 리포트가 있으시면 다음 방법으로 연락해주세요:

- **GitHub Issues**: [이슈 생성하기](https://github.com/your-repo/issues)
- **기능 요청**: 새로운 기능 제안은 GitHub Issues에 "Feature Request" 라벨로 등록
- **버그 리포트**: 버그 발견 시 상세한 재현 단계와 함께 등록

### 문의 시 포함해주세요
- 사용 중인 운영체제 및 버전
- 발생한 오류 메시지 (있다면)
- 재현 가능한 단계
- 예상 동작과 실제 동작의 차이점
