package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.commons.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.FullAssessmentResult;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiRequest;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StatelessMeansAssessmentController.class)
class StatelessMeansAssessmentControllerTest {
    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v2/assessment/means";
    private static final ApiMeansAssessmentRequest testRequest = TestModelDataBuilder.getApiCreateMeansAssessmentRequest(true);
    private static final DependantChild childOne = new DependantChild(AgeRange.ZERO_TO_ONE, 2);
    private static final DependantChild childTwo = new DependantChild(AgeRange.FIVE_TO_SEVEN, 1);
    private static final FrequencyAmount incomeAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(2700));
    private static final FrequencyAmount outAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(100));

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatelessAssessmentService statelessAssessmentService;

    @MockBean
    private AssessmentCriteriaService assessmentCriteriaService;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void validRequest_success() throws Exception {
        var assessment = buildAssessment(StatelessRequestType.BOTH);

        var request = new StatelessApiRequest()
                .withAssessment(assessment)
                .withIncome(buildIncomes())
                .withOutgoings(buildOutgoings());

        String json = objectMapper.writeValueAsString(request);
        var initialResult = new StatelessResult(
                null, new StatelessInitialResult(InitAssessmentResult.PASS,
                BigDecimal.ZERO, BigDecimal.ONE, false, BigDecimal.TEN, BigDecimal.ZERO));

        when(statelessAssessmentService.execute(any(Assessment.class), anyMap(), anyList(), anyList()))
                .thenReturn(initialResult);
        when(assessmentCriteriaService.getAssessmentCriteria(any(LocalDateTime.class), anyBoolean(), anyBoolean()))
                .thenReturn(TestModelDataBuilder.getAssessmentCriteriaEntity());
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void fullRequest_success() throws Exception {
        var assessment = buildAssessment(StatelessRequestType.BOTH);

        var request = new StatelessApiRequest()
                .withAssessment(assessment)
                .withIncome(buildIncomes())
                .withOutgoings(buildOutgoings());

        String json = objectMapper.writeValueAsString(request);
        var fullResult = new StatelessResult(
                new StatelessFullResult(FullAssessmentResult.PASS, BigDecimal.ONE,
                        BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE),
                new StatelessInitialResult(InitAssessmentResult.PASS, BigDecimal.ZERO, BigDecimal.ONE, true, BigDecimal.ZERO, BigDecimal.ONE));

        when(statelessAssessmentService.execute(any(Assessment.class), anyMap(), anyList(), anyList()))
                .thenReturn(fullResult);
        when(assessmentCriteriaService.getAssessmentCriteria(any(LocalDateTime.class), anyBoolean(), anyBoolean()))
                .thenReturn(TestModelDataBuilder.getAssessmentCriteriaEntity());
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestFailsValidation() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "invalid JSON", MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void requestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "", MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    private static Assessment buildAssessment(StatelessRequestType type) {
        return new Assessment()
                .withAssessmentDate(LocalDateTime.now())
                .withAssessmentType(type)
                .withCaseType(testRequest.getCaseType())
                .withMagistrateCourtOutcome(testRequest.getMagCourtOutcome())
                .withHasPartner(false)
                .withEligibilityCheckRequired(true)
                .withDependantChildren(Arrays.asList(childOne, childTwo));
    }

    private static List<Income> buildIncomes() {
        return Arrays.stream(IncomeType.values())
                .map(incomeType -> new Income(incomeType, incomeAmount, incomeAmount)).toList();
    }

    private static List<Outgoing> buildOutgoings() {
        return Arrays.stream(OutgoingType.values())
                .map(incomeType -> new Outgoing(incomeType, outAmount, outAmount)).toList();
    }
}
