package mullerge.personalaccountent.month;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.expense.ExpenseAdapater;
import mullerge.personalaccountent.expense.ExpenseFragment;


public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private List<Month> months;
    private Context context;
    private PopupWindow activePopup;

    public MonthAdapter(){
        months = new ArrayList<>();
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

        holder.itemView.setLongClickable(true);
        holder.setMonth(currentMonth);
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

    public void addMonth ( Month newMonth){
        months.add(newMonth);
        updateList();
    }

    public void removeMonth(Month deletedMonth){
        months.remove(deletedMonth);
        notifyDataSetChanged();
    }

    public PopupWindow getActivePopup() {
        return activePopup;
    }

    public void setActivePopup(PopupWindow activePopup) {
        this.activePopup = activePopup;
    }

    public List<Month> getMonths() {
        return months;
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView yearTV;
        private TextView monthTV;

        private Month month;

        public MonthViewHolder(View itemView) {
            super(itemView);

            yearTV = (TextView) itemView.findViewById(R.id.year);
            monthTV = (TextView) itemView.findViewById(R.id.month);

            itemView.setOnLongClickListener(this);
        }

        public void setMonth(Month month) {
            this.month = month;
        }

        @TargetApi(26)
        @Override
        public boolean onLongClick(View v) {


            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(50);
            }

            month.setSaving(new MonthDataCalculator().calculateSavings(month));

            PopupWindow detailsWindow = createPopupWindow(v);
            //detailsWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            detailsWindow.showAsDropDown(v,100,0,Gravity.CENTER);

            return true;
        }

        private PopupWindow createPopupWindow(View v) {
            if(activePopup != null){
                activePopup.dismiss();
                activePopup = null;
            }

            LayoutInflater infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View detailsView = infalter.inflate(R.layout.month_detail, null);

            ((TextView) detailsView.findViewById(R.id.month_detail_sum)).setText(String.valueOf(month.getExpenseSum()) + "Ft");
            ((TextView) detailsView.findViewById(R.id.month_detail_title)).setText(
                                                    String.valueOf(month.getYear()) + "   " + month.getMonthAsString());
            ((TextView) detailsView.findViewById(R.id.month_savings)).setText(String.valueOf(month.getSaving()) + "Ft");
            ((TextView) detailsView.findViewById(R.id.month_income)).setText(String.valueOf(month.getIncome())  + "Ft");

            final PopupWindow detailsWindow = new PopupWindow(detailsView,
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);

            detailsWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            detailsWindow.setElevation(5.0f);
            detailsWindow.setAnimationStyle(R.style.popup_window_animation);

            ImageButton closeBtn = (ImageButton) detailsView.findViewById(R.id.month_detail_close_X);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsWindow.dismiss();
                }
            });

            activePopup = detailsWindow;
            return detailsWindow;
        }
    }
}
