package pl.gry.lotto.domain.numberreceiver;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    Collection<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate);

    Ticket findByHash(String hash);

}
