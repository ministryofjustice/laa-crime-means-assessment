package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
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
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;
import uk.gov.justice.laa.crime.meansassessment.util.MockWebServerStubs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DirtiesContext
@RunWith(SpringRunner.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(
        classes = {
                CrimeMeansAssessmentApplication.class, MeansAssessmentResponseBuilder.class
        }, webEnvironment = DEFINED_PORT)
public class MeansAssessmentIntegrationTest {

    private static final boolean IS_VALID = true;
    private static final String ENDPOINT_URL = "/api/internal/v1/assessment/means";

    private MockMvc mvc;

    private static MockWebServer mockMaatCourtDataApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @BeforeClass
    public static void initialiseMockWebServer() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start(9999);
        mockMaatCourtDataApi.setDispatcher(MockWebServerStubs.getDispatcher());
    }

    @AfterClass
    public static void shutdownMockWebServer() throws IOException {
        mockMaatCourtDataApi.shutdown();
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

    @Test
    public void givenAInvalidContent_whenCreateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoOAuthToken_whenCreateAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.POST, "{}", ENDPOINT_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAnInvalidRepId_whenCreateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
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
    public void givenAValidCreateMeansAssessmentRequest_whenDateCompletionCallFails_thenServerErrorResponseIsReturned()
            throws Exception {

        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        enqueueAuthorizationResponse(true);
        enqueueAuthorizationResponse(true);
        enqueueAuthorizationResponse(false);
        enqueueAuthorizationResponse(true);

        // DateCompletionService - retrieve rep order
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.GATEWAY_TIMEOUT.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        mvc.perform(buildRequest(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenAValidCreateMeansAssessmentRequest_WhenCreateAssessmentInvoked_ThenSuccessResponseIsReturned()
            throws Exception {

        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getCreateMeansAssessmentRequest(IS_VALID);
        var initialMeansAssessmentRequestJson = objectMapper.writeValueAsString(initialMeansAssessmentRequest);

        enqueueAuthorizationResponse(true);
        enqueueAuthorizationResponse(true);
        enqueueAuthorizationResponse(false);
        enqueueAuthorizationResponse(true);

        // DateCompletionService - retrieve rep order
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(RepOrderDTO.builder().dateModified(LocalDateTime.now()).build()))
        );
        // MaatCourtDataService - Persist means assessment
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getMaatApiInitAssessmentResponse()))
        );

        mvc.perform(buildRequest(HttpMethod.POST, initialMeansAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenAInvalidContent_whenUpdateAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoOAuthToken_whenUpdateAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.PUT, "{}", ENDPOINT_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenAValidUpdateMeansAssessmentRequest_whenUpdateAssessmentInvoked_ThenSuccessResponseIsReturned() throws Exception {
        var updateAssessmentRequest =
                TestModelDataBuilder.getUpdateMeansAssessmentRequest(IS_VALID);
        updateAssessmentRequest.setAssessmentType(AssessmentType.FULL);
        var updateAssessmentRequestJson = objectMapper.writeValueAsString(updateAssessmentRequest);

        enqueueAuthorizationResponse(true);
        enqueueAuthorizationResponse(true);

        // MeansAssessmentValidationService - isAssessmentModifiedByAnotherUser
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))
        );

        // DateCompletionService - retrieve rep order
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(RepOrderDTO.builder().dateModified(LocalDateTime.now()).build()))
        );
        // MaatCourtDataService - Persist means assessment
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getMaatApiFullAssessmentResponse()))
        );

        mvc.perform(buildRequest(HttpMethod.PUT, updateAssessmentRequestJson, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void givenAInvalidContent_whenGetOldAssessmentInvoked_thenBadRequestErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL, ENDPOINT_URL, Boolean.TRUE))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void givenNoOAuthToken_whenGetOldAssessmentInvoked_thenUnauthorizedErrorResponseIsReturned() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/"
                        + TestModelDataBuilder.MEANS_ASSESSMENT_ID + "/" +
                        TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Ignore
    public void givenAValidFinancialAssessmentId_whenGetOldAssessmentInvoked_thenAssessmentIsReturned() throws Exception {
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

        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(financialAssessmentDTO))
        );

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/"
                        + TestModelDataBuilder.MEANS_ASSESSMENT_ID, Boolean.TRUE))
                .andExpect(status().isOk());
    }

    private void enqueueAuthorizationResponse(boolean result) throws JsonProcessingException {
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(AuthorizationResponseDTO.builder().result(result).build()))
        );
    }
}