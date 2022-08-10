package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullAssessmentValidator {

    public boolean validate(MeansAssessmentRequestDTO requestDTO) {
        return requestDTO.getFullAssessmentDate() != null;
    }
}
