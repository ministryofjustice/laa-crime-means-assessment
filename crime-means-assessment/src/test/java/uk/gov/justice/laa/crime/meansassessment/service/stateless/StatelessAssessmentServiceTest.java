package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaChildWeightingService;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaChildWeightingRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailFrequencyRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.CaseTypeAssessmentCriteriaDetailValueRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class StatelessAssessmentServiceTest {

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;
    @Autowired
    private AssessmentCriteriaDetailFrequencyRepository assessmentCriteriaDetailFrequencyRepository;
    @Autowired
    private CaseTypeAssessmentCriteriaDetailValueRepository caseTypeAssessmentCriteriaDetailValueRepository;
    @Autowired
    private AssessmentCriteriaChildWeightingRepository assessmentCriteriaChildWeightingRepository;

    private AssessmentCriteriaService assessmentCriteriaService;

    private AssessmentCriteriaChildWeightingService childWeightingService;

    private StatelessAssessmentService statelessAssessmentService;

    //  These age ranges give a child weighting of 1.1 - so 2.1 overall
    private static final Map<AgeRange, Integer> childFactor11 = Map.of(
            AgeRange.FIVE_TO_SEVEN, 1,
            AgeRange.EIGHT_TO_TEN, 2
    );

    private static final Map<AgeRange, Integer> childFactors =  Map.of(
            AgeRange.ZERO_TO_ONE, 1,
            AgeRange.EIGHT_TO_TEN, 4);

    @Before
    public void setUp() {
        assessmentCriteriaService = new AssessmentCriteriaService(assessmentCriteriaRepository,
                assessmentCriteriaDetailFrequencyRepository, caseTypeAssessmentCriteriaDetailValueRepository,
                assessmentCriteriaChildWeightingRepository);

        childWeightingService = new AssessmentCriteriaChildWeightingService();

        statelessAssessmentService = new StatelessAssessmentService(assessmentCriteriaService, childWeightingService);
    }

    @Test
    public void initialOnlyProducesEligibleWhenAtLowerThreshold() {
        assertThat(initialOutcomeForIncome(BigDecimal.valueOf(12475))).isEqualTo(InitAssessmentResult.PASS);
    }

    @Test
    public void initialOnlyProducesFullWhenAboveLowerThreshold() {
        assertThat(initialOutcomeForIncome(BigDecimal.valueOf(12476))).isEqualTo(InitAssessmentResult.FULL);
    }

    @Test
    public void initialOnlyProducesUnknownWhenBelowUpperThreshold() {
        assertThat(initialOutcomeForIncome(BigDecimal.valueOf(22324))).isEqualTo(InitAssessmentResult.FULL);
    }

    @Test
    public void initialOnlyProducesInEligibleWhenAtUpperThreshold() {
        assertThat(initialOutcomeForIncome(BigDecimal.valueOf(22325))).isEqualTo(InitAssessmentResult.FAIL);
    }

    @Test
    public void initalCountsPartnerIncome() {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)));
        var result = statelessAssessmentService
                .execute(new Assessment().withAssessmentType(StatelessRequestType.INITIAL)
                                .withAssessmentDate(LocalDateTime.now())
                                .withEligibilityCheckRequired(true)
                                .withHasPartner(true).withCaseType(CaseType.APPEAL_CC).
                        withMagistrateCourtOutcome(MagCourtOutcome.APPEAL_TO_CC),
                        childFactors,
                        List.of(employmentIncome), Collections.emptyList()
                );
        assertThat(result.getInitialResult().getLowerThreshold().intValue()).isEqualTo(12475);
        assertThat(result.getInitialResult().getUpperThreshold().intValue()).isEqualTo(22325);
        assertThat(result.getInitialResult().getResult()).isEqualTo(InitAssessmentResult.FULL);
    }

    @Test
    public void initWithFullCanBeCalledSuccessfully() {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)));
        var outgoing = new Outgoing(OutgoingType.TAX,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(1200)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(1200)));
        var result = statelessAssessmentService
                .execute(new Assessment().withAssessmentType(StatelessRequestType.BOTH)
                                .withAssessmentDate(LocalDateTime.now())
                                .withEligibilityCheckRequired(true)
                                .withHasPartner(false).withCaseType(CaseType.APPEAL_CC).
                                withMagistrateCourtOutcome(MagCourtOutcome.APPEAL_TO_CC),
                        childFactors,
                        List.of(employmentIncome),
                        List.of((outgoing)));
        assertThat(result.getFullResult().getResult()).isEqualTo(FullAssessmentResult.FAIL);
    }

    @Test
    public void initWithFullConsidersPartner() {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)));
        var outgoing = new Outgoing(OutgoingType.TAX,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(1200)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(1200)));
        var result = statelessAssessmentService
                .execute(new Assessment().withAssessmentType(StatelessRequestType.BOTH)
                                .withAssessmentDate(LocalDateTime.now())
                                .withEligibilityCheckRequired(true)
                                .withHasPartner(true).withCaseType(CaseType.APPEAL_CC).
                                withMagistrateCourtOutcome(MagCourtOutcome.APPEAL_TO_CC),
                        childFactors,
                        List.of(employmentIncome),
                        List.of((outgoing)));
        assertThat(result.getFullResult().getResult()).isEqualTo(FullAssessmentResult.PASS);
    }

    @Test
    public void overInelThresholdMagistratesCourtIsAFail() {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(7800)),
                new FrequencyAmount(Frequency.MONTHLY, BigDecimal.valueOf(2000)));
        var result = statelessAssessmentService
                .execute(new Assessment().withAssessmentType(StatelessRequestType.BOTH)
                                .withAssessmentDate(LocalDateTime.now())
                                .withEligibilityCheckRequired(true)
                                .withHasPartner(true).withCaseType(CaseType.INDICTABLE).
                                withMagistrateCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL),
                        childFactors,
                        Arrays.asList(employmentIncome),
                        Collections.emptyList());
        assertThat(result.getFullResult().getResult()).isEqualTo(FullAssessmentResult.FAIL);
    }

    private InitAssessmentResult initialOutcomeForIncome(BigDecimal income) {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.ANNUALLY, income.multiply(BigDecimal.valueOf(2.1))),
                null);
        var result = statelessAssessmentService
                .execute(new Assessment().withAssessmentType(StatelessRequestType.INITIAL)
                                .withAssessmentDate(LocalDateTime.now())
                                .withHasPartner(false)
                        .withEligibilityCheckRequired(true)
                                        .withCaseType(CaseType.APPEAL_CC).
                                withMagistrateCourtOutcome(MagCourtOutcome.APPEAL_TO_CC),
                        childFactor11,
                        List.of(employmentIncome), Collections.emptyList()
                );
        return result.getInitialResult().getResult();
    }
}