package mullerge.personalaccountent.event;


import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.GregorianCalendar;

import mullerge.personalaccountent.month.Month;

public class Event extends Month{

    private String label;

    public Event(){}

    public Event(String label){
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        this.label = label;
        income = 450000;
    }

    @Override
    public String getMonthAsString(){
        return label;
    }

    public String getlabel() {
        return label;
    }

    public void setlabel(String title) {
        this.label = title;
    }
}
