package com.nh.nsight.messaging.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MessageCreateRequest(
        @NotBlank @Size(max = 50) String messageCode,
        @NotBlank @Size(max = 100) String messageName,
        @NotBlank @Size(max = 20) String messageType,
        @NotBlank @Size(max = 30) String channelCode,
        @NotBlank @Size(max = 10) String locale,
        @NotBlank @Size(max = 4000) String messageContent,
        LocalDateTime displayStartAt,
        LocalDateTime displayEndAt,
        @NotBlank @Size(max = 1) String useYn
) {
}
