package com.rafaborrego.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class auditOutputDto {

    private static final String DATETIME_DISPLAY_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @ApiModelProperty(notes = "The audit id")
    private Long id;
    
    @ApiModelProperty(notes = "When the audit was created")
    @JsonFormat(pattern = DATETIME_DISPLAY_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationTimestamp;

    @ApiModelProperty(notes = "The last time the audit was modified")
    @JsonFormat(pattern = DATETIME_DISPLAY_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdateTimestamp;

    @ApiModelProperty(notes = "If the audit is deleted")
    private boolean deleted;

    // Sample field so we can do some validations on the tests
    @ApiModelProperty(notes = "The audit content")
    private String content;
}
