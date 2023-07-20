package uk.gov.justice.laa.crime.meansassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.meansassessment.CrimeMeansAssessmentApplication;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentRequest;
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
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

@RunWith(SpringRunner.class)
@Import(CrimeMeansAssessmentTestConfiguration.class)
@SpringBootTest(
        classes = {
                CrimeMeansAssessmentApplication.class, MeansAssessmentResponseBuilder.class
        }, webEnvironment = DEFINED_PORT)
public class StatelessMeansAssessmentIntegrationTest {
    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v2/assessment/means";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    private static final FrequencyAmount incomeAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(2700));
    private static final FrequencyAmount taxAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(1700));
    private static final FrequencyAmount niAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(1700));
    private static final FrequencyAmount outAmount = new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.valueOf(100));

    private static final ApiMeansAssessmentRequest testRequest = TestModelDataBuilder.getApiCreateMeansAssessmentRequest(true);
    private static final DependantChild childOne = new DependantChild().withAgeRange(AgeRange.ZERO_TO_ONE).withCount(2);
    private static final DependantChild childTwo = new DependantChild().withAgeRange(AgeRange.FIVE_TO_SEVEN).withCount(1);

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void validJsonProducesSuccessResult() throws Exception {
        var assessment = buildAssessment(StatelessRequestType.BOTH);

        var request = new StatelessApiRequest()
                .withAssessment(assessment)
                .withIncome(buildIncomes())
                .withOutgoings(buildOutgoings());

        String json = objectMapper.writeValueAsString(request);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void simplePassProducesSuccessfulResult() throws Exception {
        var income = new Income(IncomeType.EMPLOYMENT_INCOME, incomeAmount, incomeAmount);
        var tax = new Outgoing(OutgoingType.TAX, taxAmount, taxAmount);
        var ni = new Outgoing(OutgoingType.NATIONAL_INSURANCE, niAmount, niAmount);

        var assessment = buildAssessment(StatelessRequestType.BOTH);

        var request = new StatelessApiRequest()
                .withAssessment(assessment)
                .withIncome(Arrays.asList(income))
                .withOutgoings(Arrays.asList(tax, ni));

        String json = objectMapper.writeValueAsString(request);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private static Assessment buildAssessment(StatelessRequestType type) {
        return new Assessment()
                .withAssessmentDate(LocalDateTime.now())
                .withAssessmentType(type)
                .withCaseType(testRequest.getCaseType())
                .withMagistrateCourtOutcome(testRequest.getMagCourtOutcome())
                .withHasPartner(false)
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