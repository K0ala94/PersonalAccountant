package mullerge.personalaccountent.month;


import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.NotNull;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mullerge.personalaccountent.event.Event;
import mullerge.personalaccountent.expense.Expense;

@IgnoreExtraProperties
public class Month implements Serializable {

    protected int year;
    protected int month;
    protected int expenseSum;
    protected int income;
    protected int saving;
    private String label;
    private List<Expense> expenses = new ArrayList<>();

    @Exclude
    public String getMonthAsString(){

        if(label != null) {
            return label;
        }
        return new DateFormatSymbols().getMonths()[month];
    }

    @Exclude
    public String getKey(){
        return year + getMonthAsString();
    }

    @Exclude
    public static Month buildMonthFromMap(Map<String, Object> map){
        Month result = new Month();
        result.setYear(new Long((long)map.get("year")).intValue());
        result.setIncome(new Long((long)map.get("income")).intValue());
        result.setMonth(new Long((long)map.get("month")).intValue());
        result.setExpenseSum(new Long((long)map.get("expenseSum")).intValue());
        result.setSaving(new Long((long)map.get("saving")).intValue());
        result.setLabel((String)map.get("label"));

        List expenses = new ArrayList<>();
        if((Map<String, Object>)map.get("expenses") != null) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) map.get("expenses")).entrySet()) {
                expenses.add(Expense.buildExpenseFromMap((Map<String, Object>) entry.getValue()));
            }
        }

        result.setExpenses(expenses);

        return result;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getExpenseSum() {
        return expenseSum;
    }

    public void setExpenseSum(int expenseSum) {
        this.expenseSum = expenseSum;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getSaving() {
        return saving;
    }

    public void setSaving(int saving) {
        this.saving = saving;
    }

    public List<Expense> getExpenses() {
        if(expenses == null)
            expenses = new ArrayList<>();
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
