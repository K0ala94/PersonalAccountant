package mullerge.personalaccountent.month;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.expense.ExpenseAdapater;
import mullerge.personalaccountent.expense.ExpenseFragment;


public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private List<Month> months;
    private Context context;

    public MonthAdapter(){
        months = new ArrayList<>();

       /* Month newMonth = new Month();
        newMonth.setYear(2018);
        newMonth.setMonth(2);

        Month newMonth2 = new Month();
        newMonth2.setYear(2018);
        newMonth2.setMonth(3);

        months.add(newMonth);
        months.add(newMonth2);*/
    }

    @Override
    public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View monthsView = LayoutInflater.from(context).inflate(R.layout.month_lisitem,parent,false);
        MonthViewHolder viewHolder = new MonthViewHolder(monthsView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MonthViewHolder holder, int position) {
        final Month currentMonth = months.get(position);

        holder.yearTV.setText(String.valueOf(currentMonth.getYear()));
        holder.monthTV.setText(currentMonth.getMonthAsString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                ExpenseFragment expenseFragment = new ExpenseFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_month",currentMonth);
                expenseFragment.setArguments(bundle);

                transaction.replace(R.id.main_activity, expenseFragment, "expenses_fragment");
                transaction.addToBackStack("monthFragment");
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    public void updateList(){
        notifyDataSetChanged();
    }

    public void updateList(List<Month> newMonths){
        months = newMonths;
        notifyDataSetChanged();
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder{

        private TextView yearTV;
        private TextView monthTV;

        public MonthViewHolder(View itemView){
            super(itemView);

            yearTV = (TextView) itemView.findViewById(R.id.year);
            monthTV = (TextView) itemView.findViewById(R.id.month);
        }
    }

    public void addMonth ( Month newMonth){
        months.add(newMonth);
        updateList();
    }
}
