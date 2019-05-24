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

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:cleanUp.sql", "classpath:testauditsInsertionScript.sql"})
public class auditDeletionIntegrationTest {

    private static final Long ACTIVE_DOMAIN_ID = 25L;
    private static final Long DELETED_DOMAIN_ID = 26L;
    private static final Long NON_EXISTING_DOMAIN_ID = 100L;

    @Autowired
    private auditTestClient auditTestClient;

    @Test
    public void deleteauditShouldReturnEmptyBodyAndNoContentStatusWhenDeletesExistingaudit() {

        // When
        ResponseEntity responseEntity = auditTestClient.deleteaudit(ACTIVE_DOMAIN_ID);

        // Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        // And
        assertThat(responseEntity.getBody(), is(nullValue()));
    }
    
    @Test
    public void deleteShouldNotFailWhenDeletingDeletedaudit() {

        // When
        ResponseEntity responseEntity = auditTestClient.deleteaudit(DELETED_DOMAIN_ID);

        // Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        // And
        assertThat(responseEntity.getBody(), is(nullValue()));
    }

    @Test
    public void deleteauditShouldReturnErrorWhenauditDoesNotExist() {

        // When
        ResponseEntity responseEntity = auditTestClient.deleteaudit(NON_EXISTING_DOMAIN_ID);

        // Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));

        // And
        String erroraudit = IntegrationTestUtil.getErrorMessage(responseEntity);
        String expectedErroraudit = "The audit " + NON_EXISTING_DOMAIN_ID + " was not found";

        assertThat(erroraudit, is(equalTo(expectedErroraudit)));
    }

    @Test
    public void getauditsShouldNotReturnauditAfterDeletingIt() {

        // Given
        ResponseEntity<auditsDto> responseEntity = auditTestClient.getaudits();
        auditsDto auditsBeforeDeleting = responseEntity.getBody();

        // When
        auditTestClient.deleteaudit(ACTIVE_DOMAIN_ID);

        // Then
        responseEntity = auditTestClient.getaudits();
        auditsDto auditsAfterDeleting = responseEntity.getBody();

        assertTrue(containsauditWithId(auditsBeforeDeleting, ACTIVE_DOMAIN_ID));
        assertFalse(containsauditWithId(auditsAfterDeleting, ACTIVE_DOMAIN_ID));
    }

    private boolean containsauditWithId(auditsDto auditsDto, Long auditId) {

        return auditsDto.getaudits().stream()
                .anyMatch(audit -> audit.getId().equals(auditId));
    }
}
