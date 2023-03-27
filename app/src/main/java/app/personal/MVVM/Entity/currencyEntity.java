package app.personal.MVVM.Entity;

public class currencyEntity {

    private String Currency;

    public currencyEntity(String currency) {
        Currency = currency;
    }

    public currencyEntity() {
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }
}
