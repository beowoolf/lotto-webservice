package pl.gry.lotto.infrastructure.resultannouncer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.gry.lotto.domain.resultannouncer.ResultAnnouncerFacade;
import pl.gry.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;

@RestController
@RequiredArgsConstructor
public class ResultAnnouncerRestController {

    private final ResultAnnouncerFacade resultAnnouncerFacade;

    @GetMapping("/results/{id}")
    public ResponseEntity<ResultAnnouncerResponseDto> checkResultsById(@PathVariable String id) {
        ResultAnnouncerResponseDto resultAnnouncerResponseDto = resultAnnouncerFacade.checkResult(id);
        return ResponseEntity.ok(resultAnnouncerResponseDto);
    }

}
