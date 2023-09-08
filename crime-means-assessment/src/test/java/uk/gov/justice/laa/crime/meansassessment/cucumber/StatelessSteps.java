package uk.gov.justice.laa.crime.meansassessment.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.DependantChild;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiResponse;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.FrequencyAmount;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessFullResult;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessInitialResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

// This doesn't seem to help either
//@NoArgsConstructor
//@RequiredArgsConstructor
//@AllArgsConstructor
public class StatelessSteps {
//public class StatelessSteps implements En {
    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v2/assessment/means";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberRequestData requestData;

//    This doesn't help, because we just get this.mvc is null error
//    @Before
//    public void setRequestData() {
//        requestData = new CucumberRequestData();
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//    }

    @Given("^An initial assessment$")
    public void an_initial_assessment() {
        requestData.setRequestType(StatelessRequestType.INITIAL);
    }

    @Given("A full assessment")
    public void aFullAssessment() {
        requestData.setRequestType(StatelessRequestType.BOTH);
    }

    @Given("^A case type of \"([^\"]*)\"$")
    public void case_type(String caseType) {
        requestData.setCaseType(CaseType.valueOf(caseType));
    }

    @Given("^A mag court outcome of \"([^\"]*)\"$")
    public void mag_court(String magCourtOutcome) {
        requestData.setMagCourtOutcome(MagCourtOutcome.valueOf(magCourtOutcome));
    }

    @Given("^The applicant has a partner$")
    public void partner() {
        requestData.setPartner();
    }

    @And("The partner has income of {double} and frequency {string}")
    public void thePartnerHasIncomeOfAndFrequency(double value, String frequency) {
        var frequencyAmount = new FrequencyAmount(Frequency.valueOf(frequency), BigDecimal.valueOf(value));

        requestData.addIncome(frequencyAmount);
    }

    @And("The partner has outgoings of {double} with frequency {string}")
    public void thePartnerHasOutgoingsOfWithFrequency(double value, String frequency) {
        var frequencyAmount = new FrequencyAmount(Frequency.valueOf(frequency), BigDecimal.valueOf(value));

        requestData.addOutgoings(frequencyAmount);
    }

    @When("^I call the stateless CMA endpoint$")
    public void call_stateless_cma() throws Exception {
        String json = objectMapper.writeValueAsString(requestData.getRequest());

        var thing = mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = thing.getContentAsString();
        var response = objectMapper.readValue(content, StatelessApiResponse.class);
        requestData.setResponse(response);
    }

    @Then("I expect the initial outcome to be {string}")
    public void iExpectTheInitialOutcomeToBe(String outcome) {
        var response = requestData.getResponse();
        assertEquals(InitAssessmentResult.valueOf(outcome), response.getInitialMeansAssessment().getResult());
    }

    @Then("I expect the full outcome to be {string}")
    public void iExpectTheFullOutcomeToBe(String outcome) {
        var response = requestData.getResponse();
        assertEquals(FullAssessmentResult.valueOf(outcome), response.getFullMeansAssessment().getResult());
    }

    @Given("The applicant details")
    public void theApplicantDetails(DataTable dataTable) {
        List<Map<String, String>> listOfData = dataTable.asMaps(String.class, String.class);

        var data = listOfData.get(0);
        requestData.setCaseType(CaseType.valueOf(data.get("caseType")));
        var magCourtOutcome = data.get("magCourtOutcome");
        if (magCourtOutcome != null ) {
            requestData.setMagCourtOutcome(MagCourtOutcome.valueOf(magCourtOutcome));
        }
        if (Boolean.parseBoolean(data.get("hasAPartner"))) {
            requestData.setPartner();
        }
        String freq_string = data.get("frequency");
        if (freq_string != null) {
            var frequency = Frequency.valueOf(freq_string);
            var income = data.get("income");
            if (income != null) {
                requestData.addIncome(new FrequencyAmount(frequency, new BigDecimal(income)));
            }
            var outgoings = data.get("outgoings");
            if (outgoings != null) {
                requestData.addOutgoings(new FrequencyAmount(frequency, new BigDecimal(outgoings)));
            }
            var partnerIncome = data.get("partnerIncome");
            if (partnerIncome != null) {
                requestData.addIncome(new FrequencyAmount(frequency, new BigDecimal(partnerIncome)));
            }
            var partnerOut = data.get("partnerOutgoings");
            if (partnerOut != null) {
                requestData.addOutgoings(new FrequencyAmount(frequency, new BigDecimal(partnerOut)));
            }
        }

        requestData.setRequestType(StatelessRequestType.BOTH);
    }

    @And("children with ages")
    public void childrenWithAges(DataTable dataTable) {
        List<Map<String, Integer>> listOfData = dataTable.asMaps(String.class, Integer.class);

        var dependantChildren = listOfData.get(0).entrySet().stream()
                .filter(child -> child.getValue() > 0).map(child -> {
            var age = Arrays.stream(AgeRange.values())
                    .filter(a -> child.getKey().equals(a.getValue()))
                    .findFirst().get();

            return new DependantChild(age, child.getValue());
        }
        ).toList();
        requestData.setChildren(dependantChildren);
    }

    // lambdas to check initial result
    static Map<String, Consumer<Pair<String, StatelessInitialResult>>> initialCheckers = Map.of(
            "imaResult", result_pair -> {
                assertEquals(InitAssessmentResult.valueOf(result_pair.getLeft()), result_pair.getRight().getResult(), "imaResult");
            },
            "imaReason", result_pair -> {
                assertEquals(result_pair.getLeft(), result_pair.getRight().getResultReason(), "imaReason");
            },
            "fullAssessmentAvailable", result_pair -> {
                assertEquals(Boolean.parseBoolean(result_pair.getLeft()), result_pair.getRight().isFullAssessmentPossible(), "fullAssessmentAvailable");
            },
            "adjustedIncome", result_pair -> {
                assertEquals(new BigDecimal(result_pair.getLeft()), result_pair.getRight().getAdjustedIncomeValue(), "adjustedIncome");
            }
    );

    // lambdas to check full result
    static Map<String, Consumer<Pair<String, StatelessFullResult>>> fullCheckers = Map.of(
            "fmaResult", result_pair -> {
                assertEquals(FullAssessmentResult.valueOf(result_pair.getLeft()), result_pair.getRight().getResult(), "fmaResult");
            },
            "fmaReason", result_pair -> {
                assertEquals(result_pair.getLeft(), result_pair.getRight().getResultReason(), "fmaReason");
            },
            "totalAggregatedIncome", result_pair -> {
                assertEquals(new BigDecimal(result_pair.getLeft()), result_pair.getRight().getTotalAggregatedIncome(), "totalAggregatedIncome");
            },
            "adjustedLivingAllowance", result_pair -> {
                assertEquals(new BigDecimal(result_pair.getLeft()), result_pair.getRight().getAdjustedLivingAllowance().setScale(2), "adjustedLivingAllowance");
            },
            "totalAggregatedExpense", result_pair -> {
                assertEquals(new BigDecimal(result_pair.getLeft()), result_pair.getRight().getTotalAnnualAggregatedExpenditure(), "totalAggregatedExpense");
            },
            "totalAnnualDisposableIncome", result_pair -> {
                assertEquals(new BigDecimal(result_pair.getLeft()), result_pair.getRight().getDisposableIncome(), "totalAnnualDisposableIncome");
            }
    );


    @Then("I expect the result to be")
    public void iExpectTheResultToBe(DataTable dataTable) {
        List<Map<String, String>> listOfData = dataTable.asMaps(String.class, String.class);
        var expectedData = listOfData.get(0);

        var response = requestData.getResponse();
        var initialResult = response.getInitialMeansAssessment();
        var fullResult = response.getFullMeansAssessment();

        // run checks based on expected data presence
        for (var expectedItem: expectedData.entrySet()) {
            var initChecker = initialCheckers.get(expectedItem.getKey());
            if (initChecker != null) {
                initChecker.accept(new ImmutablePair<>(expectedItem.getValue(), initialResult));
            } else {
                if (fullResult != null) {
                    var fullChecker = fullCheckers.get(expectedItem.getKey());
                    if (fullChecker != null) {
                        fullChecker.accept(new ImmutablePair<>(expectedItem.getValue(), fullResult));
                    }
                }
            }
        }

    }
}
