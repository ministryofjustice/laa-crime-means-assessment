package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.util.Optional;

import static uk.gov.justice.laa.crime.meansassessment.common.Constants.ACTION_CREATE_ASSESSMENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeansAssessmentValidationProcessor {

    private final InitAssessmentValidator initAssessmentValidator;
    private final FullAssessmentValidator fullAssessmentValidator;
    private final MeansAssessmentValidationService meansAssessmentValidationService;

    public static final String MSG_REP_ID_REQUIRED = "Rep Id is missing from request and is required";
    public static final String MSG_ROLE_ACTION_IS_NOT_VALID = "Role action is not valid";
    public static final String MSG_NEW_WORK_REASON_IS_NOT_VALID = "New work reason is not valid";
    public static final String MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER = "This record is not reserved by current user";
    public static final String MSG_INCOMPLETE_ASSESSMENT_FOUND = "An incomplete assessment is associated with the current application";
    public static final String MSG_INCORRECT_REVIEW_TYPE = "Review Type - As the current Crown Court Rep Order Decision is Refused - " +
            "Ineligible (applicants disposable income exceeds eligibility threshold) you must select the appropriate review type - " +
            "Eligibility Review, Miscalculation Review or New Application Following Ineligibility.";
    public static final String MSG_FULL_ASSESSMENT_DATE_REQUIRED = "Full assessment date is required";

    public static final String ASSESSMENT_MODIFIED_BY_ANOTHER_USER = "Data has been modified by another user";

    public Optional<Void> validate(MeansAssessmentRequestDTO requestDTO, AssessmentRequestType requestType) {
        log.info("Validating means assessment request : {}", requestDTO);
        if (!isRepIdValid(requestDTO)) {
            throw new ValidationException(MSG_REP_ID_REQUIRED);
        } else if (!meansAssessmentValidationService.isRoleActionValid(requestDTO, ACTION_CREATE_ASSESSMENT)) {
            throw new ValidationException(MSG_ROLE_ACTION_IS_NOT_VALID);
        } else if (!meansAssessmentValidationService.isRepOrderReserved(requestDTO)) {
            throw new ValidationException(MSG_RECORD_NOT_RESERVED_BY_CURRENT_USER);
        }

        if (AssessmentRequestType.CREATE.equals(requestType) &&
                meansAssessmentValidationService.isOutstandingAssessment(requestDTO)) {
            throw new ValidationException(MSG_INCOMPLETE_ASSESSMENT_FOUND);
        } else {
            if (meansAssessmentValidationService.isAssessmentModifiedByAnotherUser(requestDTO)) {
                throw new ValidationException(ASSESSMENT_MODIFIED_BY_ANOTHER_USER);
            }
        }

        if (AssessmentType.INIT.equals(requestDTO.getAssessmentType())) {
            if (!meansAssessmentValidationService.isNewWorkReasonValid(requestDTO)) {
                throw new ValidationException(MSG_NEW_WORK_REASON_IS_NOT_VALID);
            } else if (!initAssessmentValidator.validate(requestDTO)) {
                throw new ValidationException(MSG_INCORRECT_REVIEW_TYPE);
            }
        } else {
            if (!fullAssessmentValidator.validate(requestDTO)) {
                throw new ValidationException(MSG_FULL_ASSESSMENT_DATE_REQUIRED);
            }
        }
        return Optional.empty();
    }

    boolean isRepIdValid(final MeansAssessmentRequestDTO meansAssessmentRequest) {
        return (meansAssessmentRequest.getRepId() != null && Integer.signum(meansAssessmentRequest.getRepId()) >= 0);
    }
}
