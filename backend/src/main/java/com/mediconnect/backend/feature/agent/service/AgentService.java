package com.mediconnect.backend.feature.agent.service;

import com.mediconnect.backend.feature.agent.dto.AgentRequest;
import com.mediconnect.backend.feature.agent.dto.AgentResponse;
import com.mediconnect.backend.feature.agent.dto.DoctorMatchDto;
import com.mediconnect.backend.feature.doctorsearch.dto.DoctorSearchResponse;
import com.mediconnect.backend.feature.doctorsearch.service.DoctorSearchService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final RestTemplate restTemplate;
    private final DoctorSearchService doctorSearchService;

    @Value("${ai.service.base-url:${embedding.service.base-url:http://localhost:8000}}")
    private String aiServiceBaseUrl;

    public AgentService(RestTemplate restTemplate) {
        this(restTemplate, null);
    }

    @Autowired
    public AgentService(RestTemplate restTemplate, @Autowired(required = false) DoctorSearchService doctorSearchService) {
        this.restTemplate = restTemplate;
        this.doctorSearchService = doctorSearchService;
    }

    public AgentResponse askAgent(AgentRequest request) {
        if (request == null || request.query() == null || request.query().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query cannot be empty");
        }

        String url = aiServiceBaseUrl + "/agent";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AgentRequest> httpEntity = new HttpEntity<>(request, headers);

        try {
            log.info("Forwarding query to AI agent service at {}", url);
            ResponseEntity<AgentResponse> response = restTemplate.postForEntity(url, httpEntity, AgentResponse.class);
            if (response.getBody() == null) {
                log.error("AI agent service returned an empty response");
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI agent service returned an empty response");
            }
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            log.warn("AI agent service returned status {}: {}. Attempting fallback doctor search.", ex.getStatusCode(), ex.getResponseBodyAsString());
            if (doctorSearchService != null) {
                try {
                    return fallbackDoctorSearch(request.query().trim());
                } catch (Exception fallbackEx) {
                    log.error("Fallback search failed: {}", fallbackEx.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.valueOf(ex.getStatusCode().value()), "AI agent error: " + ex.getResponseBodyAsString(), ex);
        } catch (ResourceAccessException ex) {
            log.error("Failed to connect to AI agent service at {}: {}", url, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI agent service is unavailable", ex);
        } catch (RestClientException ex) {
            log.error("Error communicating with AI agent service: {}", ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with AI agent service", ex);
        }
    }

    private AgentResponse fallbackDoctorSearch(String query) {
        List<DoctorSearchResponse> matches = doctorSearchService.searchDoctors(query, 3, "RELEVANCE", null, null, null, null, null);
        List<DoctorMatchDto> doctorMatches = matches.stream()
                .map(m -> new DoctorMatchDto(
                        m.getDoctorId(),
                        m.getName(),
                        m.getSpecialization(),
                        m.getQualification(),
                        m.getLanguage(),
                        m.getLocation(),
                        m.getConsultationFee()
                ))
                .toList();

        List<Long> ids = doctorMatches.stream().map(DoctorMatchDto::doctorId).toList();
        String summary = doctorMatches.isEmpty()
                ? "No matching doctors found for your query. Please consult a primary care doctor."
                : "Based on your symptoms, here are the most relevant doctors matched from our medical directory.";

        return new AgentResponse(summary, ids, doctorMatches);
    }
}


