package org.example.Burse.Currency;

public enum CurrencyType {
    USD("USD"),
    EUR("EUR");

    private final String displayName;

    CurrencyType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
