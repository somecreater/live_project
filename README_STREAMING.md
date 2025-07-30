# 실시간 영상 스트리밍 서비스(Spring Boot + React, nginx-rtmp) 프로젝트

## 개요
이 프로젝트는 `nginx-rtmp` 모듈과 `Spring Boot + React`를 사용한 실시간 동영상 스트리밍 서비스입니다.

## 서비스 구성

### 1. nginx-rtmp 스트리밍 서버
- **포트**: 1935 (RTMP), 8081 (HLS)
- **이미지**: tiangolo/nginx-rtmp
- **기능**: 실시간 스트리밍, HLS 변환, 다중 품질 지원

### 2. 모니터링 서비스
- **Prometheus**: 9090 포트
- **Grafana**: 3001 포트 (admin/admin)

### 3. API 서버
- **Spring Boot**: 8080 포트
- **React**: Spring Boot 프로젝트 내 포함

## 시작하기

### 1. 서비스 실행
```bash
# 전체 서비스 실행
docker-compose up -d

# nginx-rtmp만 실행
docker-compose up -d nginx-rtmp

# 모니터링만 실행
docker-compose up -d prometheus grafana
```

### 2. 스트리밍 시작 (OBS Studio)
1. OBS Studio에서 설정 → 스트림
2. 서비스: Custom
3. 서버: `rtmp://your-server-ip:1935/live`
4. 스트림 키: `your-stream-key`

### 3. 스트림 시청
- **HLS 스트림**: `http://your-server-ip:8081/hls/your-stream-key.m3u8`
- **상태 확인**: `http://your-server-ip:8081/stat`

## 스트리밍 애플리케이션

### 1. live (기본 스트리밍)
- **RTMP URL**: `rtmp://server:1935/live`
- **HLS URL**: `http://server:8081/hls/stream-name.m3u8`
- **품질**: 720p, 480p, 360p, 240p

### 2. hq (고품질 스트리밍)
- **RTMP URL**: `rtmp://server:1935/hq`
- **HLS URL**: `http://server:8081/hls/stream-name.m3u8`
- **품질**: 1080p, 720p, 480p

### 3. vod (VOD 스트리밍)
- **재생 경로**: `/tmp/stream/vod`

## 모니터링

### 1. Prometheus
- **URL**: `http://your-server-ip:9090`
- **기능**: 메트릭 수집 및 저장

### 2. Grafana
- **URL**: `http://your-server-ip:3001`
- **계정**: admin/admin
- **기능**: 대시보드 및 알림

### 3. nginx-rtmp 상태
- **상태 페이지**: `http://your-server-ip:8081/stat`
- **JSON API**: `http://your-server-ip:8081/stat.json`

## 설정 파일

### nginx.conf
- 메인 nginx 설정
- HLS 스트림 제공 설정
- 상태 페이지 설정

### rtmp.conf
- RTMP 스트리밍 설정
- 애플리케이션별 설정
- 품질 변형 설정

## 로드 밸런싱 및 오토 스케일링

### Docker Swarm 모드
```bash
# Swarm 초기화
docker swarm init

# 스택 배포
docker stack deploy -c docker-compose.yml live-streaming

# 서비스 스케일링
docker service scale live-streaming_nginx-rtmp=3
```

### Kubernetes
```bash
# Kubernetes 매니페스트 적용
kubectl apply -f k8s/

# HPA (Horizontal Pod Autoscaler) 설정
kubectl apply -f k8s/hpa.yaml
```


## 방화벽 설정

### 필요한 포트
- **1935**: RTMP 스트리밍
- **8081**: HLS 스트림 시청
- **9090**: Prometheus
- **3001**: Grafana

### UFW 설정 예시
```bash
sudo ufw allow 1935/tcp
sudo ufw allow 8081/tcp
sudo ufw allow 9090/tcp
sudo ufw allow 3001/tcp
```

## 문제 해결

### 1. 스트림이 보이지 않는 경우
```bash
# nginx-rtmp 로그 확인
docker-compose logs nginx-rtmp

# 스트림 디렉토리 확인
docker exec -it live-nginx-rtmp ls -la /tmp/stream/hls/
```

### 2. 포트 충돌
```bash
# 포트 사용 확인
netstat -tulpn | grep :1935
netstat -tulpn | grep :8081
```

### 3. 권한 문제
```bash
# 스트림 디렉토리 권한 설정
sudo mkdir -p /tmp/stream/hls
sudo chmod 755 /tmp/stream/hls
```

## CI/CD 파이프라인

GitHub Actions를 통해 자동 배포가 구성되어 있습니다:

1. **빌드 단계**: 코드 검증 및 테스트
2. **배포 단계**: nginx-rtmp 서비스 배포
3. **모니터링 배포**: Prometheus/Grafana 배포

## 확장 계획

### 1. Spring Boot 연동
- 스트림 상태 API 연동
- 사용자 인증 및 권한 관리
- 스트림 메타데이터 관리

### 2. CDN 연동
- Cloudflare 연동
- 글로벌 스트림 배포

### 3. 고급 모니터링
- 스트림 품질 모니터링
- 자동 알림 시스템
- 성능 최적화 