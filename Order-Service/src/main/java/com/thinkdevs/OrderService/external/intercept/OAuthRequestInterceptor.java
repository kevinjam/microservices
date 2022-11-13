package com.thinkdevs.OrderService.external.intercept;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
@RequiredArgsConstructor
public class OAuthRequestInterceptor implements RequestInterceptor {

    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer "
        + auth2AuthorizedClientManager
                .authorize(OAuth2AuthorizeRequest.
                        withClientRegistrationId("internal-client")
                        .principal("internal")
                        .build())
                .getAccessToken()
                .getTokenValue());

    }
}
