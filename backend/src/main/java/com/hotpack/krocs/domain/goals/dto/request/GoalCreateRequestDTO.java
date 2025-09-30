package com.hotpack.krocs.domain.goals.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotpack.krocs.global.common.constant.ValidationConstants;
import com.hotpack.krocs.global.common.entity.Color;
import com.hotpack.krocs.global.common.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class GoalCreateRequestDTO {

  @NotBlank(message = "{goal.title.notBlank}")
  @Size(max = ValidationConstants.TITLE_MAX, message = "{goal.title.size}")
  private String title;

  @Builder.Default
  private Priority priority = Priority.MEDIUM;

  @Builder.Default
  private Color color = Color.BLUE;

  @NotNull(message = "{goal.date.startRequired}")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @NotNull(message = "{goal.date.endRequired}")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;
}
