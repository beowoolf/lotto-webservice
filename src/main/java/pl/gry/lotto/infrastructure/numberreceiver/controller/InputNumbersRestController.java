package pl.gry.lotto.infrastructure.numberreceiver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.gry.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.gry.lotto.domain.numberreceiver.dto.NumberReceiverResponseDto;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@RestController
@RequiredArgsConstructor
public class InputNumbersRestController {

    private final NumberReceiverFacade numberReceiverFacade;

    @PostMapping("/inputNumbers")
    public ResponseEntity<NumberReceiverResponseDto> inputNumbers(@RequestBody @Valid InputNumbersRequestDto requestDto) {
        log.info("Numbers from player: " + requestDto.inputNumbers());
        Set<Integer> distinctNumbers = new HashSet<>(requestDto.inputNumbers());
        log.info("Distincted numbers from player: " + distinctNumbers);
        NumberReceiverResponseDto numberReceiverResponseDto = numberReceiverFacade.inputNumbers(distinctNumbers);
        return ResponseEntity.ok(numberReceiverResponseDto);
    }

}
