#!/bin/bash
# 이 스크립트는 로컬 전용입니다.

# Redis 비밀번호 설정
export REDISCLI_AUTH=$(cat ~/.redis.pass)

# Redis 프로세스 확인
if pgrep "redis-server" > /dev/null
then
    echo "✅ Redis is running."
    # 간단한 상태 정보 출력 (메모리 사용량 등)
    redis-cli info memory | grep "used_memory_human"
else
    echo "⚠️ Redis is stopped. Starting Redis..."
    # 서비스 시작 (sudo 권한 필요시 비밀번호 입력 혹은 sudoers 설정 필요)
    sudo service redis-server start

    if pgrep "redis-server" > /dev/null
    then
        echo "🚀 Redis started successfully!"
    else
        echo "❌ Failed to start Redis."
    fi
fi

unset REDISCLI_AUTH