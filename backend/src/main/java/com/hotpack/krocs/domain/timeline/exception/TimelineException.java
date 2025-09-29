package com.hotpack.krocs.domain.timeline.exception;

public class TimelineException extends RuntimeException {
    public TimelineException(TimelineExceptionType message) {
        super(message);
    }
}
