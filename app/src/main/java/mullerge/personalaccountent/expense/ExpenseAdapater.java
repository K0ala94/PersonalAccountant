package mullerge.personalaccountent.expense;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.month.Month;

public class ExpenseAdapater extends RecyclerView.Adapter<ExpenseAdapater.ExpenseViewHolder>  {

    private List<Expense> expenses;
    private Context context;

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

        Expense currentExpense = expenses.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd   hh:mm");
        String date = sdf.format(currentExpense.getDate());

        holder.dateTV.setText(date);
        holder.amountTV.setText(String.valueOf(currentExpense.getAmount()));
        holder.typeTV.setText(currentExpense.getType());
        holder.currencyTV.setText(currentExpense.getCurrency());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateList(){
        notifyDataSetChanged();
    }

    public void updateList(List<Expense> newExpenses){
        expenses = newExpenses;
        notifyDataSetChanged();
    }

    public void addExpense(Expense newExpense){
        expenses.add(newExpense );
        updateList();
    }

    public void removeExpense(int position){
        Expense deletedExpense = expenses.remove(position);
        Expense.delete(deletedExpense);
        notifyItemRemoved(position);
        if(position < expenses.size()){
            notifyItemRangeChanged(position, expenses.size()-position);
        }
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }



    public class ExpenseViewHolder extends RecyclerView.ViewHolder{

        private TextView dateTV;
        private TextView amountTV;
        private TextView typeTV;
        private  TextView currencyTV;

        public ExpenseViewHolder(View itemView){
            super(itemView);

            dateTV = (TextView) itemView.findViewById(R.id.expense_date);
            amountTV = (TextView) itemView.findViewById(R.id.expense_amount);
            typeTV = (TextView) itemView.findViewById(R.id.expense_type);
            currencyTV = (TextView) itemView.findViewById(R.id.expense_currency);
        }
    }

}
