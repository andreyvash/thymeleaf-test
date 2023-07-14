package com.mybluecard.code.challenge.codechallengemybluecard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConverterResponse implements Serializable {

    public String result;
    public boolean valid;
    @JsonProperty("from-value")
    public String fromValue;
    @JsonProperty("to-type")
    public String toType;
    @JsonProperty("from-type")
    public String fromType;


    public CurrencyExchange toCurrencyExchange(){
        CurrencyExchange currencyExchange = new CurrencyExchange();
        currencyExchange.setOriginCurrency(fromType);
        currencyExchange.setTargetCurrency(toType);
        currencyExchange.setTargetValue(BigDecimal.valueOf(Double.parseDouble(result)));
        currencyExchange.setOriginValue(BigDecimal.valueOf(Double.parseDouble(fromValue)));
        currencyExchange.setConverterDate(LocalDateTime.now());
        return currencyExchange;
    }
}
