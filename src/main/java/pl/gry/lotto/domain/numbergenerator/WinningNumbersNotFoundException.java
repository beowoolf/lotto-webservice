package pl.gry.lotto.domain.numbergenerator;

public class WinningNumbersNotFoundException extends RuntimeException {

    WinningNumbersNotFoundException(String message) {
        super(message);
    }

}
