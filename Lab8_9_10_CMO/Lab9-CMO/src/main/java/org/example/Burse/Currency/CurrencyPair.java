package org.example.Burse.Currency;

import java.util.Objects;

public class CurrencyPair {

    private final CurrencyType baseCurrency;
    private final CurrencyType quoteCurrency;

    public CurrencyPair(CurrencyType baseCurrency, CurrencyType quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public CurrencyType getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyType getQuoteCurrency() {
        return quoteCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyPair pair = (CurrencyPair) o;
        return baseCurrency.equals(pair.baseCurrency) && quoteCurrency.equals(pair.quoteCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, quoteCurrency);
    }

    @Override
    public String toString() {
        return baseCurrency.toString() + "/" + quoteCurrency.toString();
    }

}
