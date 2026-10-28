package com.mediconnect.backend.feature.agent;

import com.mediconnect.backend.feature.agent.controller.AgentController;
import com.mediconnect.backend.feature.agent.dto.AgentRequest;
import com.mediconnect.backend.feature.agent.dto.AgentResponse;
import com.mediconnect.backend.feature.agent.dto.DoctorMatchDto;
import com.mediconnect.backend.feature.agent.service.AgentService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private AgentService agentService;
    private AgentController agentController;

    @BeforeEach
    void setUp() {
        agentService = new AgentService(restTemplate);
        ReflectionTestUtils.setField(agentService, "aiServiceBaseUrl", "http://localhost:8000");
        agentController = new AgentController(agentService);
    }

    @Test
    void askAgent_successfulResponse() {
        DoctorMatchDto doctor = new DoctorMatchDto(
                1L,
                "Dr. Sarah Connor",
                "Cardiology",
                "MD, FACC",
                "English",
                "New York",
                new BigDecimal("200.00")
        );
        AgentResponse expectedResponse = new AgentResponse(
                "You should consult a cardiologist.",
                List.of(1L),
                List.of(doctor)
        );

        when(restTemplate.postForEntity(
                eq("http://localhost:8000/agent"),
                ArgumentMatchers.<HttpEntity<AgentRequest>>any(),
                eq(AgentResponse.class)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        AgentRequest request = new AgentRequest("I have chest pain");
        AgentResponse actualResponse = agentController.ask(request);

        assertNotNull(actualResponse);
        assertEquals("You should consult a cardiologist.", actualResponse.summary());
        assertEquals(1, actualResponse.doctorIds().size());
        assertEquals(1L, actualResponse.doctorIds().get(0));
        assertEquals("Dr. Sarah Connor", actualResponse.doctors().get(0).name());
    }

    @Test
    void askAgent_emptyQuery_throwsBadRequest() {
        AgentRequest request = new AgentRequest("   ");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> agentService.askAgent(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void askAgent_serviceUnavailable_throwsServiceUnavailable() {
        when(restTemplate.postForEntity(
                eq("http://localhost:8000/agent"),
                ArgumentMatchers.<HttpEntity<AgentRequest>>any(),
                eq(AgentResponse.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        AgentRequest request = new AgentRequest("I have a fever");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> agentService.askAgent(request));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }
}

