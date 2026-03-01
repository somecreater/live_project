#!/bin/bash

HOST="127.0.0.1"

# 1초마다 주요 상태 확인 (연결 수, CPU부하, 메모리 등)
watch -n 1 "redis-cli -h $HOST info | grep -E 'connected_clients|used_memory_human|instantaneous_ops_per_sec|rejected_connections'"