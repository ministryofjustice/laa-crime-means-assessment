package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.FullAssessmentAvailabilityService;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StatelessAssessmentServiceTest {

    private static final AssessmentCriteriaEntity mockAssessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntity();
    @InjectMocks
    private StatelessAssessmentService statelessAssessmentService;
    @Mock
    private InitMeansAssessmentService initMeansAssessmentService;
    @Mock
    private FullMeansAssessmentService fullMeansAssessmentService;
    @Mock
    private MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    @Mock
    private AssessmentCriteriaService assessmentCriteriaService;
    @Mock
    private FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    private static final BigDecimal adjustedLivingAllowance = BigDecimal.valueOf(10000.67);

    private void setupStubs() {
        when(meansAssessmentServiceFactory.getService(AssessmentType.INIT))
                .thenReturn(initMeansAssessmentService);
        when(meansAssessmentServiceFactory.getService(AssessmentType.FULL))
                .thenReturn(fullMeansAssessmentService);
        when(assessmentCriteriaService.getAssessmentCriteria(any(LocalDateTime.class), anyBoolean(), anyBoolean()))
                .thenReturn(mockAssessmentCriteria);
    }

    @Test
    void givenInitRequestType_whenExecuteIsInvoked_thenInitAssessmentIsPerformed() {

        setupStubs();

        when(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                any(CaseType.class), any(MagCourtOutcome.class), any(NewWorkReason.class), any(InitAssessmentResult.class))
        ).thenReturn(false);

        when(initMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(MeansAssessmentDTO.builder().initAssessmentResult(InitAssessmentResult.PASS).build());

        try (MockedStatic<StatelessDataAdapter> adapter = Mockito.mockStatic(StatelessDataAdapter.class)) {

            adapter.when(() -> StatelessDataAdapter.mapIncomesToSectionSummaries(any(AssessmentCriteriaEntity.class), anyList()))
                    .thenReturn(TestModelDataBuilder.getApiAssessmentSummaries(true));

            adapter.when(() -> StatelessDataAdapter.mapChildGroupings(anyMap(), anySet()))
                    .thenReturn(TestModelDataBuilder.getAssessmentChildWeightings());

            var overallResult = getAssessmentOutcome(StatelessRequestType.INITIAL);
            validateInitialResult(overallResult.getInitialResult(), InitAssessmentResult.PASS);
            assertThat(overallResult.getInitialResult().isFullAssessmentPossible()).isFalse();
            assertThat(overallResult.getFullResult()).isNull();
        }
    }

    @Test
    void givenBothRequestType_whenExecuteIsInvoked_thenFullAssessmentIsPerformed() {

        setupStubs();

        when(initMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(MeansAssessmentDTO.builder().initAssessmentResult(InitAssessmentResult.FULL).build());

        when(fullMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(MeansAssessmentDTO.builder()
                .fullAssessmentResult(FullAssessmentResult.PASS)
                .adjustedLivingAllowance(adjustedLivingAllowance).build());

        when(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                any(CaseType.class), any(MagCourtOutcome.class), any(), any(InitAssessmentResult.class))
        ).thenReturn(true);

        try (MockedStatic<StatelessDataAdapter> adapter = Mockito.mockStatic(StatelessDataAdapter.class)) {

            adapter.when(() -> StatelessDataAdapter.mapIncomesToSectionSummaries(any(AssessmentCriteriaEntity.class), anyList()))
                    .thenReturn(TestModelDataBuilder.getApiAssessmentSummaries(true));

            adapter.when(() -> StatelessDataAdapter.mapOutgoingsToSectionSummaries(any(AssessmentCriteriaEntity.class), anyList()))
                    .thenReturn(TestModelDataBuilder.getApiAssessmentSummaries(true));

            adapter.when(() -> StatelessDataAdapter.mapChildGroupings(anyMap(), anySet()))
                    .thenReturn(TestModelDataBuilder.getAssessmentChildWeightings());

            var overallResult = getAssessmentOutcome(StatelessRequestType.BOTH);
            validateFullResult(overallResult.getFullResult(), FullAssessmentResult.PASS);
            validateInitialResult(overallResult.getInitialResult(), InitAssessmentResult.FULL);
        }
    }

    @Test
    void givenBothRequestTypeAndInitAssessmentFails_whenExecuteIsInvoked_thenOnlyInitAssessmentIsPerformed() {
        setupStubs();

        when(initMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(MeansAssessmentDTO.builder().initAssessmentResult(InitAssessmentResult.FAIL).build());

        when(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                any(CaseType.class), any(MagCourtOutcome.class), any(), any(InitAssessmentResult.class))
        ).thenReturn(false);

        try (MockedStatic<StatelessDataAdapter> adapter = Mockito.mockStatic(StatelessDataAdapter.class)) {

            adapter.when(() -> StatelessDataAdapter.mapIncomesToSectionSummaries(any(AssessmentCriteriaEntity.class), anyList()))
                    .thenReturn(TestModelDataBuilder.getApiAssessmentSummaries(true));

            adapter.when(() -> StatelessDataAdapter.mapChildGroupings(anyMap(), anySet()))
                    .thenReturn(TestModelDataBuilder.getAssessmentChildWeightings());

            var overallResult = getAssessmentOutcome(StatelessRequestType.BOTH);
            validateInitialResult(overallResult.getInitialResult(), InitAssessmentResult.FAIL);
            assertThat(overallResult.getInitialResult().isFullAssessmentPossible()).isFalse();
            assertThat(overallResult.getFullResult()).isNull();
        }
    }

    private void validateInitialResult(StatelessInitialResult initialResult, InitAssessmentResult expected) {
        assertThat(initialResult.getResult()).isEqualTo(expected);
        assertThat(initialResult.getLowerThreshold()).isEqualTo(mockAssessmentCriteria.getInitialLowerThreshold());
        assertThat(initialResult.getUpperThreshold()).isEqualTo(mockAssessmentCriteria.getInitialUpperThreshold());
    }

    private void validateFullResult(StatelessFullResult fullResult, FullAssessmentResult expected) {
        assertThat(fullResult.getResult()).isEqualTo(expected);
        assertThat(fullResult.getAdjustedLivingAllowance()).isEqualTo(adjustedLivingAllowance);
        assertThat(fullResult.getEligibilityThreshold()).isEqualTo(mockAssessmentCriteria.getEligibilityThreshold());
    }

    private StatelessResult getAssessmentOutcome(StatelessRequestType requestType) {
        var employmentIncome = new Income(IncomeType.EMPLOYMENT_INCOME,
                new FrequencyAmount(Frequency.ANNUALLY, BigDecimal.TEN), null);
        return statelessAssessmentService
                .execute(new Assessment().withAssessmentType(requestType)
                                .withAssessmentDate(LocalDateTime.now())
                                .withHasPartner(false)
                                .withEligibilityCheckRequired(true)
                                .withCaseType(CaseType.APPEAL_CC)
                                .withMagistrateCourtOutcome(MagCourtOutcome.APPEAL_TO_CC),
                        Map.of(AgeRange.ZERO_TO_ONE, 1, AgeRange.EIGHT_TO_TEN, 4),
                        List.of(employmentIncome), Collections.emptyList(), TestModelDataBuilder.getAssessmentCriteriaEntity()
                );
    }
}