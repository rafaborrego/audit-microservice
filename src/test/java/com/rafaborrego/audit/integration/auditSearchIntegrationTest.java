package com.rafaborrego.audit.integration;

import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:cleanUp.sql", "classpath:testauditsInsertionScript.sql"})
public class auditSearchIntegrationTest {

    @Autowired
    private auditTestClient auditTestClient;

    @Test
    public void getauditsShouldReturnAllNonDeletedaudits() {

        // Given
        int expectedNumberaudits = 5;

        // When
        ResponseEntity<auditsDto> responseEntity = auditTestClient.getaudits();
        List<auditOutputDto> audits = responseEntity.getBody().getaudits();

        // Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));

        // And
        assertThat(audits.size(), is(equalTo(expectedNumberaudits)));
        assertFalse(auditsContainDeletedaudits(audits));
    }
    
    private boolean auditsContainDeletedaudits(List<auditOutputDto> audits) {

        return audits.stream().anyMatch(audit -> audit.isDeleted());
    }
}
