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
import android.widget.TextView;

import java.util.List;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.month.Month;
import mullerge.personalaccountent.month.MonthDataCalculator;
import mullerge.personalaccountent.util.CurrencyLoader;

public class ExpenseFragment extends Fragment implements NewExpenseDialoge.NewExpenseListener {

    private RecyclerView recyclerView;
    private ExpenseAdapater adapter;
    private Month selectedMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

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

        calculateAndSetSum();
        setUpSwipe();
    }

    public void initRecycleView(){
        recyclerView = (RecyclerView) getView().findViewById(R.id.expense_recyclerView);

        adapter = new ExpenseAdapater();
       selectedMonth = (Month) getArguments().getSerializable("selected_month");

        loadExpensesInBackGround();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadExpensesInBackGround(){
        new AsyncTask<Void, Void, List<Expense>>(){

            @Override
            protected List<Expense> doInBackground(Void... params) {
                return Expense.find(Expense.class, "month = ?",String.valueOf(selectedMonth.getId()));
            }

            @Override
            protected void onPostExecute(List<Expense> expenses) {
                super.onPostExecute(expenses);
                adapter.updateList(expenses);
            }
        }.execute();
    }

    @Override
    public void onNewExpenseListener(Expense newExpense) {
        newExpense.setMonthOfExpense(selectedMonth);
        Expense.save(newExpense);
        adapter.getExpenses().add(newExpense);
        adapter.updateList();
        calculateAndSetSum();
    }

    private void calculateAndSetSum(){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                selectedMonth = Month.findById(Month.class, selectedMonth.getId());
                double sum =  MonthDataCalculator.calculateSum(selectedMonth,CurrencyLoader.getIntance(getContext()));
                selectedMonth.setExpenseSum(new Double(sum).intValue());
                Month.save(selectedMonth);


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                TextView sumTV = (TextView)getView().findViewById(R.id.expense_month_sum);
                sumTV.setText(selectedMonth.getExpenseSum() + " Ft");
            }
        }.execute();

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

                adapter.removeExpense(position);
                calculateAndSetSum();
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(deleteCallBack);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}


