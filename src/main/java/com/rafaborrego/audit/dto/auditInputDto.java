package com.rafaborrego.audit.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class auditInputDto {

    // Sample field so we can do some validations on the tests
    @ApiModelProperty(notes = "The audit content")
    private String content;
}
