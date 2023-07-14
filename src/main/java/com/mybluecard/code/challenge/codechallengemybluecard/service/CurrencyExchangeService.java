package com.mybluecard.code.challenge.codechallengemybluecard.service;

import com.mybluecard.code.challenge.codechallengemybluecard.config.RapidApiProperties;
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
    private RapidApiProperties rapidApiProperties;

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
                BigDecimal targetResult = getTargetResult(originAmount, currencyValues);

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
            headers.set("X-RapidAPI-Key", rapidApiProperties.getApiKey()); // Replace with your RapidAPI key
            headers.set("X-RapidAPI-Host", rapidApiProperties.getApiHost());
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = rapidApiProperties.getApiUrl() + "/convert" + "?from-value=" + originAmount + "&from-type=" + originCurrency + "&to-type=" + targetCurrency;
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

                BigDecimal currencyValue = getCurrencyValue(originAmount, currencyExchange);

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

    public BigDecimal getCurrencyValue(BigDecimal originAmount, CurrencyExchange currencyExchange) {
        return currencyExchange.getTargetValue().divide(originAmount);
    }

    public BigDecimal getTargetResult(BigDecimal originAmount, CurrencyValues currencyValues) {
        return originAmount.multiply(currencyValues.getCurrencyValue());
    }

}
