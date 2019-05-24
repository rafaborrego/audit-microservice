package com.rafaborrego.audit.integration;

import com.rafaborrego.audit.dto.auditOutputDto;
import com.rafaborrego.audit.dto.auditsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:cleanUp.sql", "classpath:testauditsInsertionScript.sql"})
public class auditCreationIntegrationTest {

    private final static String SAMPLE_CONTENT = "Sample content";

    @Autowired
    private auditTestClient auditTestClient;

    @Test
    public void createauditShouldCreateauditWithExpectedContentAndStatusCreated() {

        // When
        ResponseEntity<Map> responseEntity = auditTestClient.createaudit(SAMPLE_CONTENT);

        // Then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        // And
        auditOutputDto createdaudit = IntegrationTestUtil.convertMapToauditDto(responseEntity.getBody());
        assertThat(createdaudit.getContent(), is(equalTo(SAMPLE_CONTENT)));
        assertThat(createdaudit.getCreationTimestamp(), is(notNullValue()));
        assertThat(createdaudit.getLastUpdateTimestamp(), is(notNullValue()));
        assertThat(createdaudit.getLastUpdateTimestamp(), is(equalTo(createdaudit.getCreationTimestamp())));
    }
    
    @Test
    public void getauditsShouldReturnauditAfterCreatingIt() {

        // Given
        ResponseEntity<auditsDto> responseEntity = auditTestClient.getaudits();
        List<auditOutputDto> auditsBefore = responseEntity.getBody().getaudits();

        // When
        auditTestClient.createaudit(SAMPLE_CONTENT);

        // Then
        responseEntity = auditTestClient.getaudits();
        List<auditOutputDto> auditsAfter = responseEntity.getBody().getaudits();

        assertThat(auditsAfter.size(), is(equalTo(auditsBefore.size() + 1)));
        assertThat(SAMPLE_CONTENT, is(equalTo(auditsAfter.get(0).getContent())));
    }
}
