package uk.gov.justice.laa.crime.meansassessment.service;

import io.sentry.Sentry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.gov.justice.laa.crime.meansassessment.config.RetryConfiguration;
import uk.gov.justice.laa.crime.meansassessment.exception.APIClientException;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
public abstract class RetryableWebClientService {

    @Autowired
    private RetryConfiguration retryConfiguration;

    public <T> T callWithRetry(Class<T> responseClass, WebClient.ResponseSpec baseResponseSpec, String baseErrorMessage) {
        return baseResponseSpec
                .onStatus(
                        HttpStatus::is5xxServerError,
                            error -> Mono.error(
                                    new HttpServerErrorException(
                                            error.statusCode(),
                                            baseErrorMessage + " Server Error."
                                    )
                            )
                )
                .bodyToMono(responseClass)
                .retryWhen(
                        Retry.backoff(
                                retryConfiguration.getMaxRetries(),
                                        Duration.ofSeconds(
                                                retryConfiguration.getMinBackOffPeriod()
                                        )
                                )
                                .jitter(retryConfiguration.getJitterValue())
                                .filter(
                                        throwable -> throwable instanceof HttpServerErrorException
                                )
                )
                .onErrorMap(throwable -> new APIClientException(baseErrorMessage, throwable))
                .doOnError(Sentry::captureException)
                .block();
    }
}
