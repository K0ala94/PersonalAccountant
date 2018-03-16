package mullerge.personalaccountent.month;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.text.DateFormatSymbols;

public class Month extends SugarRecord implements Serializable {

    private int year;
    private int month;
    private int expenseSum;
    private int income;
    private int saving;
    @Ignore
    private boolean modified = false;

    public String getMonthAsString(){
        return new DateFormatSymbols().getMonths()[month-1];
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

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
