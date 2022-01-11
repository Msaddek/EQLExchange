package fr.eql.al36.spring.projet.eqlexchange.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String ticker;

    private String contractAddress;

    @Column(nullable = false)
    private long maximumSupply;

    private String circulatingSupply;


    @ManyToOne
    @JoinColumn(name = "currency_type_id")
    private CurrencyType currencyType;

    @JsonIgnore
    @OneToMany(mappedBy = "currencyToBuy")
    private Set<TradeOrder> buyTradeOrders;

    @JsonIgnore
    @OneToMany(mappedBy = "currencyToSell")
    private Set<TradeOrder> sellTradeOrders;

    @JsonIgnore
    @OneToMany(mappedBy = "currency")
    private Set<Asset> assets;

    @JsonIgnore
    @OneToMany(mappedBy = "currency")
    private List<CurrencyPrice> currencyPrices;


    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return maximumSupply == currency.maximumSupply && Objects.equals(id, currency.id) && Objects.equals(name,
                                                                                                            currency.name) &&
               Objects.equals(ticker, currency.ticker) && Objects.equals(contractAddress, currency.contractAddress) &&
               Objects.equals(circulatingSupply, currency.circulatingSupply);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, ticker, contractAddress, maximumSupply, circulatingSupply);
    }

}
