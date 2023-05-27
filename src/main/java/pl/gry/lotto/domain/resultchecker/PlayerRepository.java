package pl.gry.lotto.domain.resultchecker;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlayerRepository extends MongoRepository<Player, String> {

    Optional<Player> findById(String hash);

}
