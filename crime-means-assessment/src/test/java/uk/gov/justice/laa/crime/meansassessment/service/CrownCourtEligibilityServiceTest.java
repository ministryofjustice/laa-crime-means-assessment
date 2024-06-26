package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class CrownCourtEligibilityServiceTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private CrownCourtEligibilityService crownCourtEligibilityService;


    private MeansAssessmentRequestDTO requestDTO;

    private FinancialAssessmentDTO financialAssessment = TestModelDataBuilder.getFinancialAssessmentDTO();

    private RepOrderDTO repOrderDTO;

    @BeforeEach
    void setup() {
        requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        repOrderDTO = TestModelDataBuilder.getRepOrderDTOWithAssessments(new ArrayList<>(List.of(financialAssessment)));

        when(maatCourtDataService.getRepOrder(anyInt()))
                .thenReturn(repOrderDTO);
    }

    @AfterEach
    void tearDown() {
        financialAssessment = TestModelDataBuilder.getFinancialAssessmentDTO();
        repOrderDTO = TestModelDataBuilder.getRepOrderDTOWithAssessments(new ArrayList<>(List.of(financialAssessment)));
    }

    @Test
    void givenIndictableSentForTrialAndNoPreviousAssessments_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        requestDTO.setCaseType(CaseType.INDICTABLE);
        requestDTO.setMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        repOrderDTO.getFinancialAssessments().clear();
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenCCAlreadySentForTrialAndNoPreviousAssessments_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        requestDTO.setCaseType(CaseType.CC_ALREADY);
        requestDTO.setMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        repOrderDTO.getFinancialAssessments().clear();
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenEWCommittedAndFirstAssessment_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        financialAssessment.setNewWorkReason(NewWorkReason.FMA.getCode());
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenRepOrderWithNoAssessment_whenIsEligibilityCheckRequiredIsInvoked_thenExceptionIsThrown() {
        requestDTO.setFinancialAssessmentId(8234);
        assertThatThrownBy(
                () -> crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)
        ).isInstanceOf(RuntimeException.class).hasMessage("Cannot find initial assessment with id: 8234");
    }

    @Test
    void givenEWCommittedReassessmentInitFailedAndDateCreatedOnMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenEWCommittedReassessmentInitFailedAndDateCreatedAfterMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE.plusDays(1));
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenEWCommittedReassessmentInitFailedAndDateCreatedBeforeMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE.minusDays(1));
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenNoPreviousAssessmentResults_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenPreviousAssessmentIsPassedInitMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().initResult(InitAssessmentResult.PASS.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenPreviousAssessmentIsFailedInitMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnTrue() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().initResult(InitAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenPreviousAssessmentIsFailedFullMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().fullResult(FullAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenPreviousAssessmentIsPassedFullMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().fullResult(FullAssessmentResult.PASS.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenPreviousAssessmentIsFailedPassport_whenIsEligibilityCheckRequiredIsInvoked_thenReturnTrue() {
        PassportAssessmentDTO previous =
                PassportAssessmentDTO.builder().result(PassportAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getPassportAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    void givenPreviousAssessmentIsPassedPassport_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        PassportAssessmentDTO previous =
                PassportAssessmentDTO.builder().result(PassportAssessmentResult.PASS.getResult()).build();
        repOrderDTO.getPassportAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenIncorrectCaseType_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        requestDTO.setCaseType(CaseType.APPEAL_CC);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        requestDTO.setCaseType(CaseType.SUMMARY_ONLY);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        requestDTO.setCaseType(CaseType.COMMITAL);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        requestDTO.setCaseType(CaseType.INDICTABLE);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        requestDTO.setCaseType(CaseType.CC_ALREADY);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        requestDTO.setCaseType(CaseType.EITHER_WAY);
        requestDTO.setMagCourtOutcome(MagCourtOutcome.COMMITTED);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    void givenMagsOutcomeDateSetIsNull_whenIsEligibilityCheckRequiredIsInvoked_thenReturnTrue() {
        PassportAssessmentDTO previous =
                PassportAssessmentDTO.builder().result(PassportAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getPassportAssessments().add(previous);
        repOrderDTO.setMagsOutcomeDateSet(null);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }
}
