package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.DependantChild;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiRequest;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.FrequencyAmount;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.Income;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.Outgoing;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StatelessMeansAssessmentController.class)
public class StatelessMeansAssessmentControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v2/assessment/means";

    @Autowired
    private ObjectMapper objectMapper;

    private static final FrequencyAmount incomeAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(2700));
    private static final FrequencyAmount taxAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(1700));
    private static final FrequencyAmount niAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(1700));

    @Test
    public void validJsonProducesSuccessResult() throws Exception {
        var initialMeansAssessmentRequest =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(true);

        var income = new Income(IncomeType.EMPLOYMENT_INCOME, incomeAmount, incomeAmount);
        var tax = new Outgoing(OutgoingType.TAX, taxAmount, taxAmount);
        var ni = new Outgoing(OutgoingType.NATIONAL_INSURANCE, niAmount, niAmount);

        var childOne = new DependantChild().withAgeRange(AgeRange.ZERO_TO_ONE).withCount(2);
        var childTwo = new DependantChild().withAgeRange(AgeRange.FIVE_TO_SEVEN).withCount(1);
        var assessment = new Assessment()
                .withAssessmentDate(LocalDateTime.now())
                .withAssessmentType(StatelessRequestType.INITIAL)
                .withCaseType(initialMeansAssessmentRequest.getCaseType())
                .withMagistrateCourtOutcome(initialMeansAssessmentRequest.getMagCourtOutcome())
                .withHasPartner(false)
                .withDependantChildren(Arrays.asList(childOne, childTwo));

        var request = new StatelessApiRequest()
                .withAssessment(assessment)
                .withIncome(Arrays.asList(income))
                .withOutgoings(Arrays.asList(tax, ni));

        String json = objectMapper.writeValueAsString(request);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void invalidJsonProducesErrorResult() throws Exception {
        var assessment = new Assessment()
                .withAssessmentDate(LocalDateTime.now());
        var request = new StatelessApiRequest()
                .withAssessment(assessment);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(request), MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}