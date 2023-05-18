package pl.gry.lotto.infrastructure.numbergenerator.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.gry.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import pl.gry.lotto.domain.numbergenerator.dto.WinningNumbersDto;

@Log4j2
@Component
@RequiredArgsConstructor
public class WinningNumbersScheduler {

    private final WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Scheduled(cron = "${lotto.number-generator.lotteryRunOccurrence}")
    public WinningNumbersDto generateWinningNumbers() {
        log.info("winning numbers scheduler started");
        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.generateWinningNumbers();
        log.info(winningNumbersDto.getWinningNumbers());
        log.info(winningNumbersDto.getDate());
        return winningNumbersDto;
    }

}
