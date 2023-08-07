package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.IncomeEvidenceRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IncomeEvidenceServiceTest {

    @InjectMocks
    private IncomeEvidenceService incomeEvidenceService;

    @Mock
    private IncomeEvidenceRepository incomeEvidenceRepository;

    @Test
    void testIncomeEvidenceService_whenGetIncomeEvidenceByIdInvoked_shouldSuccess() {
        incomeEvidenceService.getIncomeEvidenceById("NINO");
        verify(incomeEvidenceRepository, times(1)).findById(any());
    }

}