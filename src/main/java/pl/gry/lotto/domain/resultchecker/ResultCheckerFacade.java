package pl.gry.lotto.domain.resultchecker;

import lombok.RequiredArgsConstructor;
import pl.gry.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import pl.gry.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import pl.gry.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.gry.lotto.domain.numberreceiver.dto.TicketDto;
import pl.gry.lotto.domain.resultchecker.dto.PlayersDto;
import pl.gry.lotto.domain.resultchecker.dto.ResultDto;

import java.util.List;
import java.util.Set;

import static pl.gry.lotto.domain.resultchecker.ResultCheckerMapper.mapPlayersToResults;

@RequiredArgsConstructor
public class ResultCheckerFacade {

    private final WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    private final NumberReceiverFacade numberReceiverFacade;
    private final PlayerRepository playerRepository;
    private final WinnersRetriever winnerGenerator;

    public PlayersDto generateResults() {
        List<TicketDto> allTicketsByDate = numberReceiverFacade.retrieveAllTicketsByNextDrawDate();
        List<Ticket> tickets = ResultCheckerMapper.mapFromTicketDto(allTicketsByDate);
        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.generateWinningNumbers();
        Set<Integer> winningNumbers = winningNumbersDto.getWinningNumbers();
        if (winningNumbers == null || winningNumbers.isEmpty()) {
            return PlayersDto.builder()
                    .message("Winners failed to retrieve")
                    .build();
        }
        List<Player> players = winnerGenerator.retrieveWinners(tickets, winningNumbers);
        playerRepository.saveAll(players);
        return PlayersDto.builder()
                .results(mapPlayersToResults(players))
                .message("Winners succeeded to retrieve")
                .build();
    }

    public ResultDto findByTicketId(String ticketId) {
        Player player = playerRepository.findById(ticketId)
                .orElseThrow(() -> new PlayerResultNotFoundException("Not found for id: " + ticketId));
        return ResultDto.builder()
                .hash(ticketId)
                .numbers(player.numbers())
                .hitNumbers(player.hitNumbers())
                .drawDate(player.drawDate())
                .wonNumbers(player.wonNumbers())
                .isWinner(player.isWinner())
                .build();
    }

}
