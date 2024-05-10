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
import uk.gov.justice.laa.crime.common.model.meansassessment.stateless.StatelessApiResponse;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.enums.meansassessment.AgeRange;
import uk.gov.justice.laa.crime.enums.meansassessment.StatelessRequestType;
import uk.gov.justice.laa.crime.meansassessment.DependantChild;
import uk.gov.justice.laa.crime.meansassessment.FrequencyAmount;
import uk.gov.justice.laa.crime.meansassessment.StatelessFullResult;
import uk.gov.justice.laa.crime.meansassessment.StatelessInitialResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.meansassessment.util.RequestBuilderUtils.buildRequestGivenContent;

public class StatelessSteps extends CucumberSpringConfiguration {
    private static final String MEANS_ASSESSMENT_ENDPOINT_URL = "/api/internal/v2/assessment/means";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberRequestData requestData;

    @When("^I call the stateless CMA endpoint$")
    public void iCallStatelessCma() throws Exception {
        String json = objectMapper.writeValueAsString(requestData.getRequest());

        var thing = mvc.perform(buildRequestGivenContent(HttpMethod.POST, json, MEANS_ASSESSMENT_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = thing.getContentAsString();
        var response = objectMapper.readValue(content, StatelessApiResponse.class);
        requestData.setResponse(response);
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

    static Map<String, Consumer<Pair<String, StatelessInitialResult>>> initialCheckers = Map.of(
            "imaResult", result_pair -> {
                assertThat(result_pair.getRight().getResult()).isEqualTo(InitAssessmentResult.valueOf(result_pair.getLeft()));
            },
            "imaReason", result_pair -> {
                assertThat(result_pair.getRight().getResultReason()).isEqualTo(result_pair.getLeft());
            },
            "fullAssessmentAvailable", result_pair -> {
                assertThat(result_pair.getRight().isFullAssessmentPossible()).isEqualTo(Boolean.parseBoolean(result_pair.getLeft()));
            },
            "totalAggregatedIncome", result_pair -> {
                assertThat(result_pair.getRight().getTotalAggregatedIncome()).isEqualTo(new BigDecimal(result_pair.getLeft()));
            },
            "adjustedIncome", result_pair -> {
                assertThat(result_pair.getRight().getAdjustedIncomeValue()).isEqualTo(new BigDecimal(result_pair.getLeft()));
            }
    );

    static Map<String, Consumer<Pair<String, StatelessFullResult>>> fullCheckers = Map.of(
            "fmaResult", result_pair -> {
                assertThat(result_pair.getRight().getResult()).isEqualTo(FullAssessmentResult.valueOf(result_pair.getLeft()));
            },
            "fmaReason", result_pair -> {
                assertThat(result_pair.getRight().getResultReason()).isEqualTo(result_pair.getLeft());
            },
            "adjustedLivingAllowance", result_pair -> {
                assertThat(result_pair.getRight().getAdjustedLivingAllowance().setScale(2)).isEqualTo(new BigDecimal(result_pair.getLeft()));
            },
            "totalAggregatedExpense", result_pair -> {
                assertThat(result_pair.getRight().getTotalAnnualAggregatedExpenditure()).isEqualTo(new BigDecimal(result_pair.getLeft()));
            },
            "totalAnnualDisposableIncome", result_pair -> {
                assertThat(result_pair.getRight().getDisposableIncome()).isEqualTo(new BigDecimal(result_pair.getLeft()));
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
