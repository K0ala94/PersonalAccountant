package mullerge.personalaccountent.util;


public class CurrencyCalculator {

    public static int getAmountInHUF(String currencyName, double amount, CurrencyLoader loader){
        int valueInHUF;

        if(currencyName.equals(Currency.HUF )) {
            valueInHUF = new Double(amount).intValue();
        }
        else {

            try {
                Currency currency = loader.getCurrencyWithName(currencyName);
                valueInHUF =new Double(Math.round(amount * currency.getRateToHuf())).intValue();
            }catch (Exception e){
                valueInHUF = 0;
            }
        }

        return valueInHUF;
    }

}
