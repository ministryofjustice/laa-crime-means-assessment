package uk.gov.justice.laa.crime.meansassessment.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.LoggingData;

@Slf4j
@Aspect
@Component
public class RequestLoggingHandler {

    @Around("execution(public * uk.gov.justice.laa.crime.meansassessment.controller.MeansAssessmentController.*(..))")
    public Object logAroundControllers(ProceedingJoinPoint pjp) throws Throwable {
        Object result;
        String methodName = pjp.getSignature().getName();
        ApiMeansAssessmentRequest meansAssessment = (ApiMeansAssessmentRequest) pjp.getArgs()[0];
        MDC.put(LoggingData.MAAT_ID.getValue(), Integer.toString(meansAssessment.getRepId()));
        log.info("{} request received for MAAT ID: {}", methodName, MDC.get(LoggingData.MAAT_ID.getValue()));
        result = pjp.proceed();
        log.info("{} request completed for MAAT ID: {}", methodName, MDC.get(LoggingData.MAAT_ID.getValue()));
        MDC.remove(LoggingData.MAAT_ID.getValue());
        return result;
    }
}