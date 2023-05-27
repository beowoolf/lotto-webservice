package pl.gry.lotto.domain.resultannouncer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResponseRepository extends MongoRepository<ResultResponse, String> {

}
