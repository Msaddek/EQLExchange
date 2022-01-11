package fr.eql.al36.spring.projet.eqlexchange.controller;

import fr.eql.al36.spring.projet.eqlexchange.domain.Currency;
import fr.eql.al36.spring.projet.eqlexchange.domain.TradeOrder;
import fr.eql.al36.spring.projet.eqlexchange.domain.Transaction;
import fr.eql.al36.spring.projet.eqlexchange.domain.User;
import fr.eql.al36.spring.projet.eqlexchange.repository.AssetRepository;
import fr.eql.al36.spring.projet.eqlexchange.repository.CurrencyRepository;
import fr.eql.al36.spring.projet.eqlexchange.repository.TradeOrderRepository;
import fr.eql.al36.spring.projet.eqlexchange.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@SessionAttributes("sessionUser")
public class TradeOrderController {

    private final TradeOrderRepository tradeOrderRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyService currencyService;
    private final TradeOrderService tradeOrderService;
    private final TransactionService transactionService;
    private final CurrencyPriceService currencyPriceService;
    private final AssetRepository assetRepository;
    private final AssetService assetService;

    public TradeOrderController(TradeOrderRepository tradeOrderRepository,
                                CurrencyRepository currencyRepository,
                                CurrencyService currencyService, TradeOrderService tradeOrderService, TransactionService transactionService, CurrencyPriceService currencyPriceService, AssetRepository assetRepository, AssetService assetService) {

        this.tradeOrderRepository = tradeOrderRepository;
        this.currencyRepository = currencyRepository;
        this.currencyService = currencyService;
        this.tradeOrderService = tradeOrderService;
        this.transactionService = transactionService;
        this.currencyPriceService = currencyPriceService;
        this.assetRepository = assetRepository;
        this.assetService = assetService;
    }

    @GetMapping("trade/buy/{id1}")
    public String trade(Model model, @PathVariable String id1, HttpSession session) {
        User connectedUser = (User) session.getAttribute("sessionUser");
        Currency currencyToBuy = currencyRepository.findById(Integer.parseInt(id1)).get();
        model.addAttribute("currencyPricesJSON", currencyPriceService.getCurrencyPricesJSON(currencyToBuy));
        model.addAttribute("currencyToSell",currencyService.findCurrencyById(3));
        model.addAttribute("currenciesToSell",currencyService.getAllExceptOneWithId(currencyToBuy.getId()));

        TradeOrder newTradeOrder = new TradeOrder();
        newTradeOrder.setCurrencyToBuy(currencyToBuy);

        model.addAttribute("tradeOrder", newTradeOrder);
        return "transaction/trade";
    }

    @PostMapping("trade/place")
    public String placeTradeOrder(@ModelAttribute TradeOrder tradeOrder, @RequestParam("idCurrencyToSell") Integer idCurrencyToSell, @RequestParam("idCurrencyToBuy") Integer idCurrencyToBuy, Model model, HttpSession session) {
        User connectedUser = (User) session.getAttribute("sessionUser");

        Currency currencyToBuy = currencyService.findCurrencyById(idCurrencyToBuy);
        tradeOrder.setCurrencyToBuy(currencyToBuy);
        Currency currencyToSell = tradeOrder.getCurrencyToSell();

        tradeOrder.setUser(connectedUser);
        tradeOrder.setAmountToBuy(currencyService.getCurrencyAmountIn(currencyToBuy,currencyToSell, tradeOrder.getAmountToSell()));
        tradeOrder.setCreationDate(LocalDateTime.now());
        tradeOrder = tradeOrderService.place(tradeOrder);
        List<TradeOrder> matchingTradeOrders = tradeOrderService.match(tradeOrder);

        if (matchingTradeOrders.size() > 0) {

            TradeOrder selectedTradeOrder = tradeOrderService.selectBestAmong(tradeOrder, matchingTradeOrders);
            transactionService.executeFromTradeOrders(tradeOrder, selectedTradeOrder);
        }

        return trade(model, currencyToBuy.getId().toString(), session);
    }
}
