package com.mybluecard.code.challenge.codechallengemybluecard.repository;

import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyValues;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CurrencyValuesRepository extends CrudRepository<CurrencyValues, Long> {

    Optional<CurrencyValues> findByOriginCurrencyAndTargetCurrency(String originCurrency, String targetCurrency);
}
