package pl.gry.lotto.integration.numbergenerator;

import org.springframework.web.client.RestTemplate;
import pl.gry.lotto.domain.numbergenerator.RandomNumberGenerable;
import pl.gry.lotto.infrastructure.numbergenerator.http.RandomGeneratorClientConfig;

public class RandomGeneratorRestTemplateTestConfig extends RandomGeneratorClientConfig {

    public RandomNumberGenerable remoteNumberGeneratorClient(int port, int connectionTimeout, int readTimeout) {
        RestTemplate restTemplate = restTemplate(connectionTimeout, readTimeout, restTemplateResponseErrorHandler());
        return remoteNumberGeneratorClient(restTemplate, "http://localhost", port);
    }

}
