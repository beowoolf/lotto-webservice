package pl.gry.lotto.domain.resultchecker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.gry.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import pl.gry.lotto.domain.numberreceiver.NumberReceiverFacade;

@Configuration
public class ResultCheckerConfiguration {

    @Bean
    ResultCheckerFacade resultCheckerFacade(WinningNumbersGeneratorFacade generatorFacade, NumberReceiverFacade receiverFacade, PlayerRepository playerRepository) {
        WinnersRetriever winnerGenerator = new WinnersRetriever();
        return new ResultCheckerFacade(generatorFacade, receiverFacade, playerRepository, winnerGenerator);
    }

}
