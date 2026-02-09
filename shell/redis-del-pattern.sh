#!/bin/bash

HOST="127.0.0.1"
PORT="6379"
export REDISCLI_AUTH=$(cat ~/.redis.pass)

PATTERN="$1"

if [ -z "$PATTERN" ]; then
  echo "Usage: ./redis-del-pattern.sh <pattern>"
  echo "Example: ./redis-del-pattern.sh 'sess:*'"
  exit 1
fi

if [[ "$HOST" == "localhost" || "$HOST" == "127.0.0.1" ]]
then
  echo "This Redis Server is LOCAL SERVER"
else
  echo "This Redis Server is AWS"
fi

echo "Deleting keys matching pattern: $PATTERN"

# --scan을 사용하여 메모리 부하 없이 검색 후 파이프라인으로 삭제
redis-cli -h "$HOST" -p "$PORT" --scan --pattern "$PATTERN" |\
xargs -r redis-cli -h "$HOST" -p "$PORT" del

echo "Done."

unset REDISCLI_AUTH