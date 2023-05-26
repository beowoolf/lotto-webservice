package pl.gry.lotto.infrastructure.resultannouncer.controller.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.gry.lotto.domain.resultchecker.PlayerResultNotFoundException;

@Log4j2
@ControllerAdvice
public class ResultAnnouncerControllerErrorHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlayerResultNotFoundException.class)
    public ResultAnnouncerErrorResponse handlePlayerResultNotFound(PlayerResultNotFoundException exception) {
        String message = exception.getMessage();
        log.error(message);
        return new ResultAnnouncerErrorResponse(message, HttpStatus.NOT_FOUND);
    }

}
