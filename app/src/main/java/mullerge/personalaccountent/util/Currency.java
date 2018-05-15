package mullerge.personalaccountent.util;


import java.io.Serializable;

public class Currency implements Serializable {

    public static final String HUF = "HUF";
    public static final String EURO = "EUR";
    public static final String USD = "USD";
    public static final String GBP = "GBP";
    public static final String KUNA = "HRK";

    public static final String[] CURRENCIES = {HUF, EURO, USD, GBP, KUNA};

    private double rateToHuf;
    private String currenyName;

    public Currency(double rate, String name){
        rateToHuf = rate;
        currenyName = name;
    }

    public String getCurrenyName() {
        return currenyName;
    }

    public void setCurrenyName(String currenyName) {
        this.currenyName = currenyName;
    }

    public double getRateToHuf() {
        return rateToHuf;
    }

    public void setRateToHuf(double rateToHuf) {
        this.rateToHuf = rateToHuf;
    }
}
