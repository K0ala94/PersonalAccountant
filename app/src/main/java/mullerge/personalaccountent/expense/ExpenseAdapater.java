package mullerge.personalaccountent.expense;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import mullerge.personalaccountent.MainActivity;
import mullerge.personalaccountent.R;
import mullerge.personalaccountent.month.Month;

public class ExpenseAdapater extends RecyclerView.Adapter<ExpenseAdapater.ExpenseViewHolder>  {

    private List<Expense> expenses;
    private Context context;
    private String selectedType = ExpenseType.ALL;
    private PopupWindow activeWindow;

    public ExpenseAdapater(){

        expenses = new ArrayList<>();
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.expense_listitem,parent, false);
        ExpenseViewHolder holder = new ExpenseViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {

        if(expenses != null && !expenses.isEmpty()) {
            Expense currentExpense = expenses.get(position);

            holder.setExpense(currentExpense);

            SimpleDateFormat sdf = new SimpleDateFormat("dd   kk:mm");
            String date = sdf.format(currentExpense.getDate());

            holder.dateTV.setText(date);
            holder.amountTV.setText(String.valueOf(currentExpense.getAmount()));
            holder.typeTV.setText(currentExpense.getType());
            holder.currencyTV.setText(currentExpense.getCurrency());
        }
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateList(){
        notifyDataSetChanged();
    }

    public void updateList(List<Expense> newExpenses){
        expenses = new ArrayList<>();
        if(newExpenses != null) {
            expenses.addAll(newExpenses);
        }

        notifyDataSetChanged();

    }

    public void addExpense(Expense newExpense){
        expenses.add(newExpense );
        updateList();
    }

    public void removeExpense(int pos){

        expenses.remove(pos);

        notifyDataSetChanged();
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private TextView dateTV;
        private TextView amountTV;
        private TextView typeTV;
        private  TextView currencyTV;
        private Expense expense;

        public void setExpense(Expense e){
            this.expense = e;
        }

        public ExpenseViewHolder(View itemView){
            super(itemView);

            dateTV = (TextView) itemView.findViewById(R.id.expense_date);
            amountTV = (TextView) itemView.findViewById(R.id.expense_amount);
            typeTV = (TextView) itemView.findViewById(R.id.expense_type);
            currencyTV = (TextView) itemView.findViewById(R.id.expense_currency);

            itemView.setOnLongClickListener(this);
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

            PopupWindow descWindwo = createDescPopup();
            descWindwo.showAsDropDown(v,200,0, Gravity.CENTER);


            return true;
        }

        private PopupWindow createDescPopup(){

            if(activeWindow != null){
                activeWindow.dismiss();
                activeWindow = null;
            }

            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View descView = inflater.inflate(R.layout.expense_desc_popup,null);

            TextView descTV = (TextView)descView.findViewById(R.id.expense_popup_desc);
            descTV.setText(expense.getDescription());

            final PopupWindow descWindow = new PopupWindow(descView,
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);

            descWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            descWindow.setElevation(5.0f);

            ImageButton closeBtn = (ImageButton) descView.findViewById(R.id.expense_desc_close_X);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    descWindow.dismiss();
                }
            });

            activeWindow = descWindow;
            return descWindow;
        }
    }

}
