package com.mybluecard.code.challenge.codechallengemybluecard.controller;

import com.mybluecard.code.challenge.codechallengemybluecard.domain.CurrencyExchange;
import com.mybluecard.code.challenge.codechallengemybluecard.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CurrencyExchangeController {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @GetMapping({"/", "/index"})
    public String listConversions(Model model) {

        List<CurrencyExchange> list = currencyExchangeService.getAllConversions();
        model.addAttribute("title", "Currency Exchange");
        model.addAttribute("conversions", list);
        return "index";
    }

    @PostMapping("/convert")
    public String convertCurrency(@RequestParam("originCurrency") String originCurrency,
                                  @RequestParam("originAmount") BigDecimal originAmount,
                                  @RequestParam("targetCurrency") String targetCurrency,
                                  Model model) {
        CurrencyExchange currencyExchange = currencyExchangeService.getConversion(originCurrency, originAmount, targetCurrency);
        model.addAttribute("targetValue", currencyExchange.getTargetValue());
        model.addAttribute("originValue", currencyExchange.getOriginValue());
        model.addAttribute("originCurrency", currencyExchange.getOriginCurrency());
        model.addAttribute("targetCurrency", currencyExchange.getTargetCurrency());

        return "result";
    }

}
