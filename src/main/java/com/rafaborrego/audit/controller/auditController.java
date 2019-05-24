package com.rafaborrego.audit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.rafaborrego.audit.entity.audit;
import com.rafaborrego.audit.dto.auditInputDto;
import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;

@Api(value="audit-service", description="audit operations")
public interface auditController {

    @ApiOperation(value = "Gets a audit")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "audit returned successfully"),
                            @ApiResponse(code = 404, message = "audit not found")})
    auditOutputDto getauditById(Long id);

    @ApiOperation(value = "Get audits")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "audits returned successfully")
    })
    auditsDto getaudits();

    @ApiOperation(value = "Create a audit")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "audit added successfully"),
            @ApiResponse(code = 400, message = "The audit has invalid content")
    })
    auditOutputDto createaudit(auditInputDto auditData);
    
    
    @ApiOperation(value = "Modify a audit")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "audit modified successfully"),
            @ApiResponse(code = 404, message = "audit not found"),
            @ApiResponse(code = 400, message = "The audit has invalid content")
    })
    auditOutputDto modifyaudit(Long auditId, auditInputDto auditData);
    
    @ApiOperation(value = "Delete a audit")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "audit deleted successfully"),
            @ApiResponse(code = 404, message = "audit not found")
    })
    void deleteaudit(Long auditId);
}

