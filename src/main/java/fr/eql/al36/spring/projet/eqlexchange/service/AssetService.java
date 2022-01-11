package fr.eql.al36.spring.projet.eqlexchange.service;

import fr.eql.al36.spring.projet.eqlexchange.domain.Asset;
import fr.eql.al36.spring.projet.eqlexchange.domain.Currency;
import fr.eql.al36.spring.projet.eqlexchange.domain.User;
import fr.eql.al36.spring.projet.eqlexchange.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    private final CurrencyService currencyService;


    public AssetService(AssetRepository assetRepository,
                        CurrencyService currencyService) {
        this.assetRepository = assetRepository;
        this.currencyService = currencyService;
    }



    public void creditFromSourceAsset(Asset targetAsset, Asset sourceAsset, double amount) {
        if (targetAsset.getCurrency().getId() == sourceAsset.getCurrency().getId() && sourceAsset.getBalance() >= amount) {
            sourceAsset.setBalance(sourceAsset.getBalance() - amount);
            targetAsset.setBalance(targetAsset.getBalance() + amount);
            assetRepository.save(sourceAsset);
            assetRepository.save(targetAsset);
        }
        else {
            System.out.println("creditFromSourceAsset: could not process transfer");
        }
    }

    public List<Asset> getUserWallet(User user) {
        return assetRepository.getAssetsByUserOrderByBalanceDesc(user);
    }

    public Asset getByUserAndCurrency(User user, Currency currency) {
        return assetRepository.getAssetByUserAndCurrency(user, currency);
    }

    public Asset getEqlAssetByUser(User user) {
        List<Asset> assets = assetRepository.getAllByUser(user);
        for (Asset asset : assets) {
            if (asset.getCurrency().getId() == 5) {
                return asset;
            }
        }
        return null;
    }

    public List<Asset> getAllByUser(User user) {
        return assetRepository.getAllByUser(user);
    }

    public List<Asset> getAllByCurrency(Currency currency) {
        return assetRepository.getAllByCurrency(currency);
    }

    public Asset getById(Integer id) {
        return assetRepository.findById(id).get();
    }

    public Double calculMarketCap(Currency currency) {
        Double result;
        Double currencyPrice = currency.getCurrencyPrices().get(currency.getCurrencyPrices().size() - 1).getPrice();
        Double currencySupply = Double.parseDouble(currency.getCirculatingSupply());
        result =  currencyPrice * currencySupply;
        return result;
    }

    public Asset findAssetByUserAndCurrency(User user, Currency currency){
        if(assetRepository.findByUserAndCurrency(user, currency).isPresent()){
            return assetRepository.findByUserAndCurrency(user, currency).get();
        }
        return null;
    }
}
