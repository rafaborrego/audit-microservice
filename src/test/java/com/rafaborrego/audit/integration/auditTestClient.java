package com.rafaborrego.audit.integration;

import com.rafaborrego.audit.controller.Endpoint;
import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Client to do requests on the audit integration tests.
 * Some methods return a map instead of a audit DTO so we can check the error audit in the tests
 */

@Component
class auditTestClient {

    @Autowired
    private TestRestTemplate restTemplate;

    ResponseEntity<auditOutputDto> getauditById(Long auditId) {

        return restTemplate.getForEntity(Endpoint.BASE_DOMAIN_URL + "/" + auditId, auditOutputDto.class);
    }

    ResponseEntity<auditsDto> getaudits() {

        return restTemplate.getForEntity(Endpoint.BASE_DOMAIN_URL, auditsDto.class);
    }

    ResponseEntity<Map> createaudit(String content) {

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("content", content);

        return restTemplate.postForEntity(Endpoint.BASE_DOMAIN_URL, requestBodyMap, Map.class);
    }

    ResponseEntity<Map> modifyaudit(Long auditId, String content) {

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("content", content);

        String modificationUrl = Endpoint.BASE_DOMAIN_URL + "/" + auditId;

        HttpEntity<auditOutputDto> requestEntity = new HttpEntity(requestBodyMap);

        return restTemplate.exchange(modificationUrl, HttpMethod.PUT, requestEntity, Map.class);
    }

    ResponseEntity<Map> deleteaudit(Long auditId) {

        String deletionUrl = Endpoint.BASE_DOMAIN_URL + "/" + auditId;

        return restTemplate.exchange(deletionUrl, HttpMethod.DELETE, null, Map.class);
    }
}
