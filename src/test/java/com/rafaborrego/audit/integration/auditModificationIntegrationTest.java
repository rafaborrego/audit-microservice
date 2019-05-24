package com.rafaborrego.audit.integration;

import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:cleanUp.sql", "classpath:testauditsInsertionScript.sql"})
public class auditModificationIntegrationTest {

    private static final Long DELETED_DOMAIN_ID = 26L;
    private static final Long NON_EXISTING_DOMAIN_ID = 100L;

    @Autowired
    private auditTestClient auditTestClient;

    @Test
    public void modifyauditShouldReturnModifiedaudit() {

        // Given
        String contentBefore = "Before";
        String contentAfter = "After";

        ResponseEntity<Map> creationResponseEntity = auditTestClient.createaudit(contentBefore);
        auditOutputDto audit = IntegrationTestUtil.convertMapToauditDto(creationResponseEntity.getBody());
        
        LocalDateTime creationTimeBefore = audit.getCreationTimestamp();
        LocalDateTime lastUpdatedTimeBefore = audit.getLastUpdateTimestamp();

        // When
        ResponseEntity<Map> modificationResponseEntity = auditTestClient.modifyaudit(audit.getId(), contentAfter);

        // Then
        assertThat(modificationResponseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));

        // And
        auditOutputDto auditDto = IntegrationTestUtil.convertMapToauditDto(modificationResponseEntity.getBody());
        String auditContentAfter = auditDto.getContent();
        LocalDateTime creationTimeAfter = auditDto.getCreationTimestamp();
        LocalDateTime lastUpdatedTimeAfter = auditDto.getLastUpdateTimestamp();

        assertThat(auditContentAfter, is(equalTo(contentAfter)));
        assertThat(creationTimeAfter, is(equalTo(creationTimeBefore)));
        assertThat(lastUpdatedTimeAfter, greaterThan(lastUpdatedTimeBefore));
    }
    
    @Test
    public void modifyauditShouldReturnErrorWhenauditDoesNotExist() {

        // When
        ResponseEntity<Map> modificationResponseEntity = auditTestClient.modifyaudit(NON_EXISTING_DOMAIN_ID, "content");

        // Then
        assertThat(modificationResponseEntity.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));

        // And
        String erroraudit = IntegrationTestUtil.getErrorMessage(modificationResponseEntity);
        String expectedErroraudit = "The audit " + NON_EXISTING_DOMAIN_ID + " was not found";

        assertThat(erroraudit, is(equalTo(expectedErroraudit)));
    }

    @Test
    public void modifyauditShouldReturnErrorWhenauditIsDeleted() {

        // When
        ResponseEntity<Map> modificationResponseEntity = auditTestClient.modifyaudit(DELETED_DOMAIN_ID, "content");

        // Then
        assertThat(modificationResponseEntity.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));

        // And
        String erroraudit = IntegrationTestUtil.getErrorMessage(modificationResponseEntity);
        String expectedErroraudit = "The audit " + DELETED_DOMAIN_ID + " was not found";

        assertThat(erroraudit, is(equalTo(expectedErroraudit)));
    }

    @Test
    public void getauditsShouldReturnModifiedaudit() {

        // Given
        String auditBefore = "Content before";
        ResponseEntity<Map> createdauditsResponseEntity = auditTestClient.createaudit(auditBefore);
        auditOutputDto audit = IntegrationTestUtil.convertMapToauditDto(createdauditsResponseEntity.getBody());

        // When
        String newContent = "New content";
        auditTestClient.modifyaudit(audit.getId(), newContent);

        // Then
        ResponseEntity<auditsDto> auditsAfterResponseEntity = auditTestClient.getaudits();
        String auditContentAfter = findauditContentById(auditsAfterResponseEntity.getBody(), audit.getId());

        assertThat(auditContentAfter, is(equalTo(newContent)));
    }

    private String findauditContentById(auditsDto auditsDto, Long auditId) {

        return auditsDto.getaudits().stream()
                .filter(audit -> auditId.equals(audit.getId()))
                .findFirst().get()
                .getContent();
    }
}
