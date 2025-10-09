package com.hotpack.krocs.domain.stopwatch.exception;

import com.hotpack.krocs.global.common.response.exception.GeneralException;
import lombok.Getter;

@Getter
public class StopwatchException extends GeneralException {

  private final StopwatchExceptionType stopwatchExceptionType;

  public StopwatchException(StopwatchExceptionType message) {
    super(message);
    this.stopwatchExceptionType = message;
  }
}