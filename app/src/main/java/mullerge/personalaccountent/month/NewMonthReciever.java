package mullerge.personalaccountent.month;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mullerge.personalaccountent.dalFireBase.ExpenseRepo;
import mullerge.personalaccountent.dalFireBase.MonthRepo;

public class NewMonthReciever extends BroadcastReceiver{

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            scheduleNewMonthBroadcast();
            System.out.println("LOG: BOOOOOOOT" + new Date().toString());
        }
        else {

            Calendar currentDate = Calendar.getInstance();
            currentDate.setTimeInMillis(System.currentTimeMillis());

            if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                addNewMonth();
            }
        }
    }


    private void addNewMonth(){
        MonthRepo monthRepo = new MonthRepo();
        ExpenseRepo expenseRepo = new ExpenseRepo();

        Month newMonth = new Month();

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        newMonth.setMonth(now.get(Calendar.MONTH));
        newMonth.setYear(now.get(Calendar.YEAR));

        newMonth.setIncome(450000);

        monthRepo.saveMonth(newMonth);
        expenseRepo.saveMonth(newMonth);
    }

    public void scheduleNewMonthBroadcast(){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context,NewMonthReciever.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context,0,intent,0);

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.MINUTE) + 1);

        alarmManager.setInexactRepeating(AlarmManager.RTC,startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pIntent);

        //alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis(), 5000,pIntent);

    }
}
