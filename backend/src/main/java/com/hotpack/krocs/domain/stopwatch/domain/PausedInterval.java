package com.hotpack.krocs.domain.stopwatch.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class PausedInterval {
    private LocalDateTime pauseTime;
    private LocalDateTime resumeTime;
    // constructor, getter 추가
}