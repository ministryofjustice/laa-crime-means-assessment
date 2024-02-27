package uk.gov.justice.laa.crime.meansassessment.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.StatelessApiResponse;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.DependantChild;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.FrequencyAmount;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.Income;
import uk.gov.justice.laa.crime.meansassessment.service.stateless.Outgoing;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
@ScenarioScope
public class CucumberRequestData {
    private final StatelessApiRequest request;
    private StatelessApiResponse response;

    public CucumberRequestData() {
        request = new StatelessApiRequest()
                .withAssessment(new Assessment()
                        .withHasPartner(false)
                        .withEligibilityCheckRequired(false)
                        .withAssessmentDate(LocalDateTime.now())
                        .withDependantChildren(Collections.emptyList()))
                .withIncome(Collections.emptyList())
                .withOutgoings(Collections.emptyList());
    }

    public StatelessApiResponse getResponse() {
        return response;
    }

    public void setResponse(StatelessApiResponse response) {
        this.response = response;
    }

    public void setRequestType(StatelessRequestType requestType) {
        request.getAssessment().setAssessmentType(requestType);
    }

    public void setCaseType(CaseType caseType) {
        request.getAssessment().setCaseType(caseType);
    }

    public void setMagCourtOutcome(MagCourtOutcome magCourtOutcome) {
        request.getAssessment().setMagistrateCourtOutcome(magCourtOutcome);
    }

    public void setPartner() {
        request.getAssessment().setHasPartner(true);
    }

    public StatelessApiRequest getRequest() {
        return request;
    }

    public void addIncome(FrequencyAmount value) {
        var income = request.getIncome();
        var newIncome = List.of((new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.ZERO), value)));
        request.withIncome(Stream.concat(income.stream(), newIncome.stream()).toList());
    }

    public void addOutgoings(FrequencyAmount frequencyAmount) {
        var outgoings = request.getOutgoings();
        var newIncome = List.of((new Outgoing(OutgoingType.RENT_OR_MORTGAGE,
                new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.ZERO), frequencyAmount)));
        request.withOutgoings(Stream.concat(outgoings.stream(), newIncome.stream()).toList());
    }

    public void setChildren(List<DependantChild> dependantChildren) {
        request.getAssessment().setDependantChildren(dependantChildren);
    }
}
