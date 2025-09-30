package com.hotpack.krocs.domain.timeline.exception;

import com.hotpack.krocs.global.common.response.exception.GeneralException;
import lombok.Getter;

@Getter
public class TimelineException extends GeneralException {

    private final TimelineExceptionType timelineExceptionType;

    public TimelineException(TimelineExceptionType timelineExceptionType) {
        super(timelineExceptionType);
        this.timelineExceptionType = timelineExceptionType;
    }
}