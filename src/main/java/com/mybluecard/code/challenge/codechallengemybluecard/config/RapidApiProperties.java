package com.mybluecard.code.challenge.codechallengemybluecard.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Configuration
public class RapidApiProperties  {


    @Value("${rapid.key}")
    private String apiKey;

    @Value("${rapidapi.url}")
    private String apiUrl;

    @Value("${rapidapi.host}")
    private String apiHost;

}
