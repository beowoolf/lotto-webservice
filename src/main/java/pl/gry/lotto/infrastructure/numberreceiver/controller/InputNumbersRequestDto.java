package pl.gry.lotto.infrastructure.numberreceiver.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InputNumbersRequestDto(
        @NotNull(message = "{inputNumbers.not.null}")
        @NotEmpty(message = "{inputNumbers.not.empty}")
        List<Integer> inputNumbers) {
}
