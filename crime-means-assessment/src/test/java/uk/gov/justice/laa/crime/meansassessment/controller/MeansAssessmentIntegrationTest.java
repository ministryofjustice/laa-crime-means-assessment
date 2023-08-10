package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.common.Constants;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(classes = {CrimeMeansAssessmentApplication.class, MeansAssessmentResponseBuilder.class}, webEnvironment = DEFINED_PORT)
@AutoConfigureWireMock(port = 9999)
class MeansAssessmentIntegrationTest {

    private static final boolean IS_VALID = true;
    private static final String ENDPOINT_URL = "/api/internal/v1/assessment/means";

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WireMockServer mockMaatCourtDataApi;

    @BeforeAll
    static void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        stubFor(post("/oauth2/token")
                .willReturn(WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(mapper.writeValueAsString(token))));
    }

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @AfterEach
    void clean() {
        mockMaatCourtDataApi.resetAll();
    }

    private MockHttpServletRequestBuilder buildRequest(HttpMethod method, String endpointUrl, boolean withAuth) {
        return buildRequest(method, null, endpointUrl, withAuth);
    }

    private MockHttpServletRequestBuilder buildRequest(HttpMethod method, String content, String endpointUrl) {
        return buildRequest(method, content, endpointUrl, true);
    }

    private MockHttpServletRequestBuilder buildRequest(HttpMethod method, String content,
                                                       String endpointUrl, boolean withAuth) {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.LAA_TRANSACTION_ID, UUID.randomUUID());
        if (content != null) {
            requestBuilder.content(content);
        }
        if (withAuth) {
            requestBuilder.header("Authorization", "Bearer " + "token");
        }
        return requestBuilder;
    }

    private void stubForRoleActionAndReservationUrl() {
        stubFor(get(urlEqualTo("/api/internal/v1/assessment/role-action-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody("true")));

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/reservation-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody("true")));
    }

    @Test
    void givenAInvalidContent_whenCreateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoOAuthToken_whenCreateAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.POST, "{}", ENDPOINT_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAnInvalidRepId_whenCreateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        initialMeansAssessmentRequest.setRepId(-1000);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        String expectedErrorMessage = "Rep Id is missing from request and is required";
        mvc.perform(buildRequest(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
    }

    @Test
    void givenAValidCreateMeansAssessmentRequest_whenDateCompletionCallFails_thenServerErrorResponseIsReturned() throws Exception {
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        stubForRoleActionAndReservationUrl();

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/outstanding-assessments-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(new OutstandingAssessmentResultDTO()))));

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/new-work-reason-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody("true")));

        stubFor(post(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")).willReturn(aResponse()
                .withStatus(504)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))));

        mvc.perform(buildRequest(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(exactly(1), postRequestedFor(urlEqualTo("/oauth2/token")));
        verify(exactly(4), getRequestedFor(urlPathMatching("/api/internal/v1/assessment/.*")));
        verify(exactly(1), postRequestedFor(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")));
    }

    @Test
    void givenAValidCreateMeansAssessmentRequest_WhenCreateAssessmentInvoked_ThenSuccessResponseIsReturned() throws Exception {
        var initialMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        stubForRoleActionAndReservationUrl();

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/outstanding-assessments-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(new OutstandingAssessmentResultDTO()))));

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/new-work-reason-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody("true")));

        stubFor(post(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")).atPriority(1).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(RepOrderDTO.builder().dateModified(LocalDateTime.now()).build()))));

        // MaatCourtDataService - Persist means assessment
        stubFor(post(urlEqualTo("/api/internal/v1/assessment/create-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getMaatApiInitAssessmentResponse()))));

        mvc.perform(buildRequest(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(exactly(4), getRequestedFor(urlPathMatching("/api/internal/v1/assessment/.*")));
        verify(exactly(1), postRequestedFor(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")));
        verify(exactly(1), postRequestedFor(urlEqualTo("/api/internal/v1/assessment/create-url")));
    }

    @Test
    void givenAInvalidContent_whenUpdateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoOAuthToken_whenUpdateAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.PUT, "{}", ENDPOINT_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAValidUpdateMeansAssessmentRequest_whenUpdateAssessmentInvoked_ThenSuccessResponseIsReturned() throws Exception {
        var updateAssessmentRequest = TestModelDataBuilder.getUpdateMeansAssessmentRequest(IS_VALID);
        updateAssessmentRequest.setAssessmentType(AssessmentType.FULL);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);

        stubForRoleActionAndReservationUrl();

        stubFor(get(urlMatching("/api/internal/v1/assessment/rep-orders/.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))));

        // DateCompletionService - retrieve rep order
        stubFor(post(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(RepOrderDTO.builder().dateModified(LocalDateTime.now()).build()))));

        // MaatCourtDataService - Persist means assessment
        stubFor(put(urlEqualTo("/api/internal/v1/assessment/update-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getMaatApiFullAssessmentResponse()))));

        mvc.perform(buildRequest(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(exactly(3), getRequestedFor(urlPathMatching("/api/internal/v1/assessment/.*")));
        verify(exactly(1), postRequestedFor(urlEqualTo("/api/internal/v1/assessment/rep-orders/update-date-completed")));
        verify(exactly(1), putRequestedFor(urlEqualTo("/api/internal/v1/assessment/update-url")));
    }

    @Test
    void givenAInvalidContent_whenGetOldAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL, ENDPOINT_URL, Boolean.TRUE))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void givenNoOAuthToken_whenGetOldAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/"
                        + TestModelDataBuilder.MEANS_ASSESSMENT_ID + "/" +
                        TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Disabled("Flaky test")
    void givenAValidFinancialAssessmentId_whenGetOldAssessmentInvoked_thenAssessmentIsReturned() throws Exception {
        FinancialAssessmentDTO financialAssessmentDTO =
                TestModelDataBuilder.getFinancialAssessmentDTO(
                        CurrentStatus.IN_PROGRESS.getStatus(),
                        NewWorkReason.HR.getCode(), ReviewType.NAFI.getCode()
                );

        financialAssessmentDTO.setAssessmentDetails(TestModelDataBuilder.getAssessmentDetails());
        financialAssessmentDTO.setChildWeightings(TestModelDataBuilder.getChildWeightings());
        List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList = new ArrayList<>();
        finAssIncomeEvidenceDTOList.add(
                TestModelDataBuilder.getFinAssIncomeEvidenceDTO("Y", "SIGNATURE")
        );
        financialAssessmentDTO.setFinAssIncomeEvidences(finAssIncomeEvidenceDTOList);
        financialAssessmentDTO.setUpdated(TestModelDataBuilder.TEST_DATE_CREATED);

        stubFor(get(urlEqualTo("/api/internal/v1/assessment/search-url")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                .withBody(objectMapper.writeValueAsString(financialAssessmentDTO))));

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + TestModelDataBuilder.MEANS_ASSESSMENT_ID, Boolean.TRUE))
                .andExpect(status().isOk());

        verify(exactly(1), getRequestedFor(urlPathMatching("/api/internal/v1/assessment/search-url")));
    }

}