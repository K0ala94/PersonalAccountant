package mullerge.personalaccountent.expense;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mullerge.personalaccountent.month.Month;

@IgnoreExtraProperties
public class Expense implements Serializable{

    private double amount;
    private long date;
    private String type;
    private String description;
    private String currency;
    private String key;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long dateMilis) {
        this.date =dateMilis;
    }

    @Exclude
    public Date getDateAsDate(){
        return new Date(date);
    }

    @Exclude
    public static Expense buildExpenseFromMap(Map<String,Object> map){
        Expense result = new Expense();
        result.setKey((String)map.get("key"));
        result.setAmount(new Long((long)map.get("amount")).doubleValue());
        result.setCurrency((String)map.get("currency"));
        result.setDate((Long)map.get("date"));
        result.setDescription((String)map.get("description"));
        result.setType((String)map.get("type"));

        return  result;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
