package mullerge.personalaccountent.month;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.List;

public class NewMonthReciever extends BroadcastReceiver{

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());

        if(currentDate.get(Calendar.DAY_OF_MONTH) == 1){
            addNewMonth();
        }
    }


    private void addNewMonth(){
        final SharedPreferences sp = context.getSharedPreferences("PERSONAL_ACCOUNTENT", Context.MODE_PRIVATE);

        List<Month> lastMonth =  Month.find(Month.class, null, null, null, "id DESC", "1");

        Month newMonth = new Month();

        if(lastMonth.size() == 0){
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());

            newMonth.setMonth(now.get(Calendar.MONTH));
            newMonth.setYear(now.get(Calendar.YEAR));
        }
        else {
            Month currentMonth = lastMonth.get(0);
            if (currentMonth.getMonth() == 11) {
                newMonth.setMonth(0);
                newMonth.setYear(currentMonth.getYear() + 1);
            } else {
                newMonth.setMonth(currentMonth.getMonth() + 1);
                newMonth.setYear(currentMonth.getYear());
            }
        }

        newMonth.setIncome(450000);
        Month.save(newMonth);
    }
}
