package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <code>MaatApiOAuth2Client.java</code>
 */
@Configuration
@Slf4j
public class MaatApiOAuth2Client {

    private final MaatApiConfiguration config;
    private static final String REGISTERED_ID = "maatapi";

    public MaatApiOAuth2Client(MaatApiConfiguration config) {
        this.config = config;
    }

    /**
     * @param tokenUri
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Bean
    ClientRegistrationRepository getRegistration(
            @Value("${spring.security.oauth2.client.provider.maatapi.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.maatapi.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.maatapi.client-secret}") String clientSecret
    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId(REGISTERED_ID)
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryClientRegistrationRepository(registration);
    }


    /**
     * @param clientRegistrationRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository) {

        // grant_type = client_credentials flow.
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        // Machine to machine service.
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository));
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }


    /**
     * @param authorizedClientManager
     * @return
     */
    @Bean(name = "maatAPIOAuth2WebClient")
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(REGISTERED_ID);

        WebClient.Builder client = WebClient.builder()
                .filter(loggingRequest())
                .filter(loggingResponse())
                .baseUrl(config.getBaseUrl());

        if (config.isOAuthEnabled()) {
            client.filter(oauth2Client);
        }
        return client.build();
    }

    /**
     * @return
     */
    private ExchangeFilterFunction loggingRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }

    /**
     * @return
     */
    private ExchangeFilterFunction loggingResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}",clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

}
