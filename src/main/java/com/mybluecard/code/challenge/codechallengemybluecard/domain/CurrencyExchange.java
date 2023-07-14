package com.mybluecard.code.challenge.codechallengemybluecard.domain;

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
@Table
@Entity
public class CurrencyExchange implements Serializable {

    private static final long serialVersionUID = 1815744647700361038L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String originCurrency;

    private String targetCurrency;

    private BigDecimal originValue;

    private BigDecimal targetValue;

    private LocalDateTime converterDate;

}
