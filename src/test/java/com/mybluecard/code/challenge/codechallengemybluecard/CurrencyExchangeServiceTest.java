package com.mybluecard.code.challenge.codechallengemybluecard;

import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyExchange;
import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyValues;
import com.mybluecard.code.challenge.codechallengemybluecard.service.CurrencyExchangeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CurrencyExchangeServiceTest {


    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Test
    public void testGetCurrencyValue() {
        // Create test data
        BigDecimal originAmount = BigDecimal.valueOf(200);
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setTargetValue(BigDecimal.valueOf(1000));

        // Invoke the method under test
        BigDecimal result = currencyExchangeService.getCurrencyValue(originAmount, currencyExchange);

        // Assert the result
        BigDecimal expected = BigDecimal.valueOf(5);
        assertEquals(expected, result);
    }

    @Test
    public void testGetCurrencyValue_targetValueIsLessThanOriginAmount() {
        // Create test data
        BigDecimal originAmount = BigDecimal.valueOf(200);
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setTargetValue(BigDecimal.valueOf(100));

        // Invoke the method under test
        BigDecimal result = currencyExchangeService.getCurrencyValue(originAmount, currencyExchange);

        // Assert the result
        BigDecimal expected = BigDecimal.valueOf(0.5);
        assertEquals(expected, result);
    }

    @Test
    public void testGetTargetResult() {
        // Create test data
        BigDecimal originAmount = BigDecimal.valueOf(100);
        CurrencyValues currencyValues = new CurrencyValues();
        currencyValues.setCurrencyValue(BigDecimal.valueOf(5));

        // Invoke the method under test
        BigDecimal result = currencyExchangeService.getTargetResult(originAmount, currencyValues);

        // Assert the result
        BigDecimal expected = BigDecimal.valueOf(500);
        assertEquals(expected, result);
    }

    @Test
    public void testGetTargetResult_currencyValueLessThanOne() {
        // Create test data
        BigDecimal originAmount = BigDecimal.valueOf(100);
        CurrencyValues currencyValues = new CurrencyValues();
        currencyValues.setCurrencyValue(BigDecimal.valueOf(0.5));

        // Invoke the method under test
        BigDecimal result = currencyExchangeService.getTargetResult(originAmount, currencyValues);

        // Assert the result
        BigDecimal expected = BigDecimal.valueOf(50.0);
        assertEquals(expected, result);
    }
}
