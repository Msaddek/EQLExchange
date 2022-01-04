package fr.eql.al36.spring.projet.eqlexchange.controller;

import fr.eql.al36.spring.projet.eqlexchange.domain.Currency;
import fr.eql.al36.spring.projet.eqlexchange.domain.User;
import fr.eql.al36.spring.projet.eqlexchange.service.CurrencyPriceService;
import fr.eql.al36.spring.projet.eqlexchange.service.CurrencyService;
import fr.eql.al36.spring.projet.eqlexchange.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class CurrencyController {

    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final CurrencyPriceService currencyPriceService;


    public CurrencyController(TransactionService transactionService, CurrencyService currencyService,
                              CurrencyPriceService currencyPriceService) {
        this.transactionService = transactionService;
        this.currencyService = currencyService;
        this.currencyPriceService = currencyPriceService;
    }

    @GetMapping("/hello")
    public String welcome() {
        return "HelloWorld !!";
    }

    @GetMapping("currency/{id}/details")
    public String displayCurrencyDetails(Model model, HttpSession session, @PathVariable String id) {
        User connectedUser = (User) session.getAttribute("sessionUser");
        Currency currency = currencyService.findCurrencyById(Integer.parseInt(id));

        if(currency == null){
            return "currency/non-existant";
        }
        model.addAttribute("currency", currency);
        model.addAttribute("connectedUser", connectedUser);
        model.addAttribute("transactions", transactionService.getTransactionsDoneOnCurrency(currency));
        model.addAttribute("currencyPricesJSON", currencyPriceService.getCurrencyPricesJSON(currency));
        //model.addAttribute("transactions", transactionService.getTransactionsDoneOnCurrency(currency));
        return "currency/details";
    }

}
