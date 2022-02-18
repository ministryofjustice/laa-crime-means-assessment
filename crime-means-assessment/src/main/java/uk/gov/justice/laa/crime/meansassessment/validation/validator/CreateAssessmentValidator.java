package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.util.Optional;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;

@Component
@RequiredArgsConstructor
public class CreateAssessmentValidator {

    private final MeansAssessmentValidationService meansAssessmentValidationService;

    public static final String MSG_REP_ID_REQUIRED = "Rep Id is missing from request and is required";
    public static final String MSG_ROLE_ACTION_IS_NOT_VALID = "Role action is not valid";
    public static final String MSG_NEW_WORK_REASON_IS_NOT_VALID = "New work reason is not valid";
    public static final String MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER = "This record is not reserved by current user";
    public static final String MSG_INCOMPLETE_ASSESSMENT_FOUND = "An incomplete assessment is associated with the current application";

    public Optional<Void> validate(ApiCreateMeansAssessmentRequest meansAssessmentRequest) {

        if(!meansAssessmentValidationService.isRepIdPresentForCreateAssessment(meansAssessmentRequest)) {
            throw new ValidationException(MSG_REP_ID_REQUIRED);
        } else if(!meansAssessmentValidationService.validateRoleAction(meansAssessmentRequest, ACTION_CREATE_ASSESSMENT)){
            throw new ValidationException(MSG_ROLE_ACTION_IS_NOT_VALID);
        } else if(!meansAssessmentValidationService.validateNewWorkReason(meansAssessmentRequest)) {
            throw new ValidationException(MSG_NEW_WORK_REASON_IS_NOT_VALID);
        } else if(!meansAssessmentValidationService.validateRoleReservation(meansAssessmentRequest)) {
            throw new ValidationException(MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER);
        } else if(!meansAssessmentValidationService.validateOutstandingAssessments(meansAssessmentRequest)) {
            throw new ValidationException(MSG_INCOMPLETE_ASSESSMENT_FOUND);
        }
        return Optional.empty();
    }

}
