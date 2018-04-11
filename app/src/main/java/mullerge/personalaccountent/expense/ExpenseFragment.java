package mullerge.personalaccountent.expense;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.dalFireBase.ExpenseRepo;
import mullerge.personalaccountent.dalFireBase.MonthRepo;
import mullerge.personalaccountent.month.Month;
import mullerge.personalaccountent.month.MonthDataCalculator;
import mullerge.personalaccountent.util.CurrencyLoader;

public class ExpenseFragment extends Fragment implements NewExpenseDialoge.NewExpenseListener {

    private RecyclerView recyclerView;
    private ExpenseAdapater adapter;
    private Month selectedMonth;
    private Spinner typeSpinner;
    private ExpenseRepo expenseRepo = new ExpenseRepo();
    private MonthRepo monthRepo = new MonthRepo();
    private View thisView;
    private ChildEventListener expenseDBListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_expense,container,false);
        return thisView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Itt inicializaloma spinnert hogy az elejen ne villogjon
        typeSpinner = (Spinner) getView().findViewById(R.id.sum_type_spinner);
        typeSpinner.setAdapter(new ArrayAdapter<>(getContext(),R.layout.sum_spinner_item, ExpenseType.SUM_TYPES.toArray()));

        initRecycleView();

        ((TextView)getView().findViewById(R.id.expense_toolbar_text))
                .setText(selectedMonth.getMonthAsString());

        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addExpenseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewExpenseDialoge().show(getActivity().getSupportFragmentManager(),null);
            }
        });

        regsiterExpensesChangeListener();
        setUpSwipe();
    }



    public void initRecycleView(){
        selectedMonth = (Month) getArguments().getSerializable("selected_month");

        recyclerView = (RecyclerView) getView().findViewById(R.id.expense_recyclerView);

        adapter = new ExpenseAdapater();
        selectedMonth.setExpenses(new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setSelectedMonthAndLoadExpenses(selectedMonth);
    }

    public void calculateAndSetSum(){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                List<Expense> expensesToFilter = new ArrayList<>();
                expensesToFilter.addAll(adapter.getExpenses());

                MonthDataCalculator monthDataCalculator = new MonthDataCalculator();
                double sum =  monthDataCalculator.calculateSum(expensesToFilter,CurrencyLoader.getIntance(getContext()));

                selectedMonth.setExpenseSum(new Double(sum).intValue());

                monthRepo.updateMonthSum(selectedMonth);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                TextView sumTV = (TextView)thisView.findViewById(R.id.expense_month_sum);
                sumTV.setText(selectedMonth.getExpenseSum() + " Ft");
            }
        }.execute();

    }

    @Override
    public void onNewExpenseListener(Expense newExpense) {
        expenseRepo.saveExpense(newExpense,selectedMonth);
    }


    private void setUpSwipe() {
        ItemTouchHelper.SimpleCallback deleteCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Expense deletedExpense = adapter.getExpenses().get(position);
                adapter.removeExpense(position);
                expenseRepo.deleteExpense(selectedMonth, deletedExpense);

            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(deleteCallBack);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void setSumTypeSpinner(){
        typeSpinner = (Spinner) getView().findViewById(R.id.sum_type_spinner);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Expense> filteredExpenses = new ArrayList<>();
                String expType = ExpenseType.SUM_TYPES.get(position);
                adapter.setSelectedType(expType);

                if(expType.equals(ExpenseType.ALL)){
                    selectedMonth.getExpenses().sort((e1,e2) ->  new Long(e2.getDate() - e1.getDate()).intValue() );
                    adapter.updateList(selectedMonth.getExpenses());
                }
                else {
                    for(Expense e : selectedMonth.getExpenses()){
                        if(e.getType().equals(expType)){
                            filteredExpenses.add(e);
                        }
                    }

                    filteredExpenses.sort((e1,e2) ->  new Long(e2.getDate() - e1.getDate()).intValue() );
                    adapter.updateList(filteredExpenses);
                }

                calculateAndSetSum();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void placeExpenses(){
        for(Expense expense : selectedMonth.getExpenses()){
            placeExpense(expense);
        }
    }

    private void placeExpense(Expense expense){
        if (adapter.getSelectedType().equals(ExpenseType.ALL)
                || expense.getType().equals(adapter.getSelectedType())) {

            adapter.getExpenses().add(expense);
            adapter.updateList();
        }
        selectedMonth.getExpenses().add(expense);

    }

    private void setSelectedMonthAndLoadExpenses(final Month month){
        DatabaseReference selectedMonthRefrence = expenseRepo.getDBReference().child(month.getKey());
        selectedMonthRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> valuesMap = (HashMap<String, Object>) dataSnapshot.getValue();
                selectedMonth = Month.buildMonthFromMap(valuesMap);

                //calculateAndSetSum();
                setSumTypeSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void regsiterExpensesChangeListener() {
         expenseDBListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                placeExpense(dataSnapshot.getValue(Expense.class));
                calculateAndSetSum();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                calculateAndSetSum();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        expenseRepo.getDBReference().child(selectedMonth.getKey()).child("expenses").addChildEventListener(expenseDBListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        expenseRepo.getDBReference().child(selectedMonth.getKey()).child("expenses").removeEventListener(expenseDBListener);
    }
}


