package mullerge.personalaccountent.expense;


import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;
import java.util.Date;

import mullerge.personalaccountent.month.Month;

public class Expense extends SugarRecord implements Serializable{

    private double amount;
    private Date date;
    private String type;
    private String description;
    @Column(name = "month")
    private Month monthOfExpense;
    private String currency;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Month getMonthOfExpense() {
        return monthOfExpense;
    }

    public void setMonthOfExpense(Month monthOfExpense) {
        this.monthOfExpense = monthOfExpense;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
