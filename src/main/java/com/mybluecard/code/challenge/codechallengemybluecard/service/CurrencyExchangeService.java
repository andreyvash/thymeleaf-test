package com.mybluecard.code.challenge.codechallengemybluecard.service;

import com.mybluecard.code.challenge.codechallengemybluecard.domain.ConverterResponse;
import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyExchange;
import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyValues;
import com.mybluecard.code.challenge.codechallengemybluecard.repository.CurrencyExchangeRepository;
import com.mybluecard.code.challenge.codechallengemybluecard.repository.CurrencyValuesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyExchangeService {

    @Autowired
    private CurrencyExchangeRepository currencyExchangeRepository;

    @Autowired
    private CurrencyValuesRepository currencyValuesRepository;

    public CurrencyExchange saveConversion(CurrencyExchange currencyExchange) {
        return currencyExchangeRepository.save(currencyExchange);
    }

    public List<CurrencyExchange> getAllConversions() {
        List<CurrencyExchange> result = new ArrayList<>();

        for (CurrencyExchange currencyExchange : currencyExchangeRepository.findAll()) {
            result.add(currencyExchange);
        }

        return result;
    }

    public CurrencyExchange getConversion(String originCurrency,BigDecimal originAmount, String targetCurrency) {
        try {
            CurrencyValues currencyValues = currencyValuesRepository.findByOriginCurrencyAndTargetCurrency(originCurrency, targetCurrency).orElse(null);

            if(currencyValues != null ) {
                BigDecimal targetResult = originAmount.multiply(currencyValues.getCurrencyValue());

                CurrencyExchange currencyExchange = new CurrencyExchange();
                currencyExchange.setOriginCurrency(originCurrency);
                currencyExchange.setTargetCurrency(targetCurrency);
                currencyExchange.setTargetValue(targetResult);
                currencyExchange.setOriginValue(originAmount);
                currencyExchange.setConverterDate(LocalDateTime.now());
                return saveConversion(currencyExchange);
            }
        } catch (Exception e) {
            System.out.println("ERROR trying to get conversion from database : " + e.getMessage());
            throw new RuntimeException(e);
        }

        return getResultFromApi(originCurrency, originAmount, targetCurrency);
    }

    public CurrencyExchange getResultFromApi(String originCurrency,BigDecimal originAmount, String targetCurrency) {

        ConverterResponse response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-RapidAPI-Key", "5e8751059cmshb63e0440d0d16ecp1c42b9jsn74d9f4485f44"); // Replace with your RapidAPI key
            headers.set("X-RapidAPI-Host", "community-neutrino-currency-conversion.p.rapidapi.com");
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://community-neutrino-currency-conversion.p.rapidapi.com/convert" + "?from-value=" + originAmount + "&from-type=" + originCurrency + "&to-type=" + targetCurrency;
            ResponseEntity<ConverterResponse> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    ConverterResponse.class
            );

            response = responseEntity.getBody();
            CurrencyExchange currencyExchange = new CurrencyExchange();
            if(response != null ){
                currencyExchange = saveConversion(response.toCurrencyExchange());

                BigDecimal currencyValue = currencyExchange.getTargetValue().divide(originAmount);

                CurrencyValues currencyValues = new CurrencyValues();
                currencyValues.setCurrencyValue(currencyValue);
                currencyValues.setOriginCurrency(currencyExchange.getOriginCurrency());
                currencyValues.setTargetCurrency(currencyExchange.getTargetCurrency());
                currencyValuesRepository.save(currencyValues);
            }
             return currencyExchange;

        } catch (RestClientException e) {
            System.out.println("ERROR trying to get conversion from API : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
