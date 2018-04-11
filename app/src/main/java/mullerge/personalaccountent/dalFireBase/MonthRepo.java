package mullerge.personalaccountent.dalFireBase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mullerge.personalaccountent.event.Event;
import mullerge.personalaccountent.expense.Expense;
import mullerge.personalaccountent.month.Month;
import mullerge.personalaccountent.month.MonthAdapter;

public class MonthRepo {

    private DatabaseReference monthDB;

    public MonthRepo(){
        init();
    }

    private void init(){
        monthDB = FirebaseDatabase.getInstance().getReference().child("months");
    }

    public void saveMonth(Month newMonth){
        String key = newMonth.getKey();

        List<Expense> expenses = newMonth.getExpenses();

        monthDB.child(key).setValue(newMonth);
    }

    public void deleteMonth(Month month){
        Map<String, Object> deletePaths = new HashMap<>();
        deletePaths.put("/months/" + month.getKey(), null);
        deletePaths.put("/monthsWithExpenses/" + month.getKey(), null);

        FirebaseDatabase.getInstance().getReference().updateChildren(deletePaths);
    }

    public void updateMonthSum(Month month){
        Map<String, Object> updatePath = new HashMap<>();
        updatePath.put("/months/" + month.getKey() + "/expenseSum", month.getExpenseSum());
        updatePath.put("/monthsWithExpenses/" + month.getKey() +"/expenseSum", month.getExpenseSum());

        FirebaseDatabase.getInstance().getReference().updateChildren(updatePath);
    }

    public void loadMonths(final MonthAdapter adapter){
        monthDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Month month = dataSnapshot.getValue(Month.class);
                adapter.addMonth(month);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Month month = dataSnapshot.getValue(Month.class);
                adapter.removeMonth(month);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
