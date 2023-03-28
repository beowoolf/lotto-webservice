package pl.gry.lotto.domain.numbergenerator;

import lombok.Builder;

import java.util.Set;

@Builder
public record SixRandomNumbersDto(Set<Integer> numbers) {
}
