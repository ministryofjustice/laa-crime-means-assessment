package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrownCourtEligibilityServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private CrownCourtEligibilityService crownCourtEligibilityService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private MeansAssessmentRequestDTO requestDTO;

    private FinancialAssessmentDTO financialAssessment = TestModelDataBuilder.getFinancialAssessmentDTO();

    private RepOrderDTO repOrderDTO;

    @Before
    public void setup() {
        requestDTO = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        repOrderDTO = TestModelDataBuilder.getRepOrderDTOWithAssessments(new ArrayList<>(List.of(financialAssessment)));

        when(maatCourtDataService.getRepOrder(anyInt(), anyString()))
                .thenReturn(repOrderDTO);
    }

    @After
    public void tearDown() {
        financialAssessment = TestModelDataBuilder.getFinancialAssessmentDTO();
        repOrderDTO = TestModelDataBuilder.getRepOrderDTOWithAssessments(new ArrayList<>(List.of(financialAssessment)));
    }

    @Test
    public void givenIndictableSentForTrialAndNoPreviousAssessments_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        requestDTO.setCaseType(CaseType.INDICTABLE);
        requestDTO.setMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        repOrderDTO.getFinancialAssessments().clear();
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenCCAlreadySentForTrialAndNoPreviousAssessments_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        requestDTO.setCaseType(CaseType.CC_ALREADY);
        requestDTO.setMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL);
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();

        repOrderDTO.getFinancialAssessments().clear();
        softly.assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenEWCommittedAndFirstAssessment_whenIsEligibilityCheckRequiredIsInvoked_thenCorrectResultIsReturned() {
        financialAssessment.setNewWorkReason(NewWorkReason.FMA.getCode());
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    public void givenRepOrderWithNoAssessment_whenIsEligibilityCheckRequiredIsInvoked_thenExceptionIsThrown() {
        requestDTO.setFinancialAssessmentId(8234);
        assertThatThrownBy(
                () -> crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)
        ).isInstanceOf(RuntimeException.class).hasMessage("Cannot find initial assessment with id: 8234");
    }

    @Test
    public void givenEWCommittedReassessmentInitFailedAndDateCreatedOnMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenEWCommittedReassessmentInitFailedAndDateCreatedAfterMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE.plusDays(1));
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenEWCommittedReassessmentInitFailedAndDateCreatedBeforeMagsOutcomeDateSet_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        financialAssessment.setInitResult(InitAssessmentResult.FAIL.getResult());
        financialAssessment.setDateCreated(TestModelDataBuilder.TEST_MAGS_OUTCOME_DATE.minusDays(1));
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenNoPreviousAssessmentResults_whenIsEligibilityCheckRequiredIsInvoked_thenTrueIsReturned() {
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
        public void givenPreviousAssessmentIsPassedInitMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
            FinancialAssessmentDTO previous =
                    FinancialAssessmentDTO.builder().initResult(InitAssessmentResult.PASS.getResult()).build();
            repOrderDTO.getFinancialAssessments().add(previous);
            assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    public void givenPreviousAssessmentIsFailedInitMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnTrue() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().initResult(InitAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenPreviousAssessmentIsFailedFullMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().fullResult(FullAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    public void givenPreviousAssessmentIsPassedFullMeans_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        FinancialAssessmentDTO previous =
                FinancialAssessmentDTO.builder().fullResult(FullAssessmentResult.PASS.getResult()).build();
        repOrderDTO.getFinancialAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    public void givenPreviousAssessmentIsFailedPassport_whenIsEligibilityCheckRequiredIsInvoked_thenReturnTrue() {
        PassportAssessmentDTO previous =
                PassportAssessmentDTO.builder().result(PassportAssessmentResult.FAIL.getResult()).build();
        repOrderDTO.getPassportAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isTrue();
    }

    @Test
    public void givenPreviousAssessmentIsPassedPassport_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
        PassportAssessmentDTO previous =
                PassportAssessmentDTO.builder().result(PassportAssessmentResult.PASS.getResult()).build();
        repOrderDTO.getPassportAssessments().add(previous);
        assertThat(crownCourtEligibilityService.isEligibilityCheckRequired(requestDTO)).isFalse();
    }

    @Test
    public void givenIncorrectCaseType_whenIsEligibilityCheckRequiredIsInvoked_thenReturnFalse() {
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
}
