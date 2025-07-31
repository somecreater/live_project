# Spring Boot + React 기반 실시간 영상 스트리밍 프로젝트

## 개요
이 프로젝트는 **Spring Boot + React**를 사용한 실시간 영상 스트리밍 애플리케이션입니다.
(추후 새로운 기술을 추가할 예정입니다!)

## 서비스 구성

### 1. API 서버 (Spring Boot)
- **포트**: 8080
- **기능**: REST API, 사용자 인증, 데이터 관리

### 2. 프론트엔드 (React)
- **기술**: React + Vite
- **기능**: 사용자 인터페이스, API 연동

### 3. 모니터링 서비스
- **Prometheus**: 9090 포트
- **Grafana**: 3001 포트 (admin/admin)

### 4. 실시간 영상 스트리밍, 업로드 서비스
- (추후 추가 예정)

## 시작하기

### 1. 서비스 실행
```bash
# 전체 서비스 실행 (모니터링 포함)
docker-compose up -d

# 모니터링만 실행
docker-compose -f docker-compose.monitoring.yml up -d

# 실시간 영상 스트리밍만 실행(추후 추가)
```


### 2. 개발 환경
```bash
# Spring Boot 실행
./gradlew bootRun

# React 개발 서버 실행
cd front
npm run dev
```

## 포트 안내
- **8080**: Spring Boot API 서버
- **3000**: React 개발 서버
- **9090**: Prometheus
- **3001**: Grafana

## 모니터링
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3001` (admin/admin)

## 확장 계획
- 실시간 영상 스트리밍, 업로드 기능 (추후 기술 검토 후 구현)
- 사용자 관리 시스템
- 데이터 분석 기능
- CDN 연동

---

문의 및 추가 설정이 필요하면 언제든 말씀해 주세요! 