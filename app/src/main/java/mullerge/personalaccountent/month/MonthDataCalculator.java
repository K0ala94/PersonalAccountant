package mullerge.personalaccountent.month;


import java.util.List;

import mullerge.personalaccountent.expense.Expense;
import mullerge.personalaccountent.util.CurrencyCalculator;
import mullerge.personalaccountent.util.CurrencyLoader;

public class MonthDataCalculator {

    public double calculateSum(List<Expense> allExpensesInMonth, CurrencyLoader loader){
            double sum = 0;

            for (Expense e : allExpensesInMonth) {
                sum += CurrencyCalculator.getAmountInHUF(e.getCurrency(), e.getAmount(), loader);
            }

            return sum;
    }

    public  int calculateSavings(Month month){

        return month.getIncome() - month.getExpenseSum();
    }
}
