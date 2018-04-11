package mullerge.personalaccountent.dalFireBase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import mullerge.personalaccountent.expense.Expense;
import mullerge.personalaccountent.expense.ExpenseAdapater;
import mullerge.personalaccountent.expense.ExpenseFragment;
import mullerge.personalaccountent.month.Month;

public class ExpenseRepo  {

    private DatabaseReference expenseDB;

    public ExpenseRepo(){
        init();
    }

    private void init(){
        expenseDB = FirebaseDatabase.getInstance().getReference().child("monthsWithExpenses");
    }

    public void saveExpense(Expense expense, Month month){
        String key = expenseDB.child(month.getKey()).child("expenses").push().getKey();
        expense.setKey(key);
        expenseDB.child(month.getKey()).child("expenses").child(key).setValue(expense);
    }

    public void saveMonth(Month month){
        expenseDB.child(month.getKey()).setValue(month);
    }

    public void deleteExpense(Month month, Expense expense){
        expenseDB.child(month.getKey()).child("expenses").child(expense.getKey()).removeValue();
    }


    public DatabaseReference getDBReference(){
        return expenseDB;
    }

    //adatok betoltese Expense fragmentben
}
