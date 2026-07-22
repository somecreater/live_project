package com.live.main.video.database.entity;

public enum ProcessingStatus {
    CREATED,
    UPLOADING,
    UPLOADED,
    VALIDATING,
    TRANSCODING,
    READY,
    FAILED,
    DELETED
}
