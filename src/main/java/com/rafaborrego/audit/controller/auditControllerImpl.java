package com.rafaborrego.audit.controller;

import com.rafaborrego.audit.dto.auditInputDto;
import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;
import com.rafaborrego.audit.entity.Invoice;
import com.rafaborrego.audit.mapper.BeanMapper;
import com.rafaborrego.audit.service.auditService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = com.rafaborrego.audit.controller.Endpoint.BASE_DOMAIN_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class auditControllerImpl implements auditController {

    private final BeanMapper beanMapper;

    private final auditService auditService;

    public auditControllerImpl(auditService auditService, BeanMapper beanMapper) {
        this.auditService = auditService;
        this.beanMapper = beanMapper;
    }

    @Override
    @GetMapping(Endpoint.PATH_DOMAIN_ID)
    public auditOutputDto getauditById(Long id) {

        return beanMapper.map(auditService.getauditById(id), auditOutputDto.class);
    }

    @Override
    @GetMapping
    public auditsDto getaudits() {

        Iterable<audit> audits = auditService.getaudits();

        return convertauditsToauditsDto(audits);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public auditOutputDto createaudit(@ApiParam("audit data") @Validated @RequestBody auditInputDto auditData) {

        audit audit = auditService.createaudit(auditData);

        return beanMapper.map(audit, auditOutputDto.class);
    }

    @Override
    @PutMapping(Endpoint.PATH_DOMAIN_ID)
    public auditOutputDto modifyaudit(@ApiParam("audit id") @PathVariable Long auditId,
    @ApiParam("audit data") @Validated @RequestBody auditInputDto auditData) {

        audit audit = auditService.modifyaudit(auditId, auditData);

        return beanMapper.map(audit, auditOutputDto.class);
    }

    @Override
    @DeleteMapping(Endpoint.PATH_DOMAIN_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteaudit(@ApiParam("audit id") @PathVariable Long auditId) {

        auditService.deleteaudit(auditId);
    }

    private auditsDto convertauditsToauditsDto(Iterable<audit> audits) {

        List<auditOutputDto> convertedaudits = beanMapper.mapAsList(audits, auditOutputDto.class);

        auditsDto auditsDto = new auditsDto();
        auditsDto.setaudits(convertedaudits);

        return auditsDto;
    }
}
