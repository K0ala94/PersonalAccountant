package mullerge.personalaccountent.month;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mullerge.personalaccountent.R;

public class MonthFragment extends Fragment {

    private RecyclerView recyclerView;
    private MonthAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View monthsView = inflater.inflate(R.layout.fragment_month,container,false);

        return monthsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initRecycleView();

        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle(null);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addMOnthFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Month newMonth = new Month();
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                newMonth.setMonth(now.get(Calendar.MONTH));
                newMonth.setYear(now.get(Calendar.YEAR));
                newMonth.setIncome(450000);

                Month.save(newMonth);
                adapter.addMonth(newMonth);
            }
        });
    }

    public void initRecycleView(){
        recyclerView = (RecyclerView) getView().findViewById(R.id.month_recyclerView);
        adapter = new MonthAdapter();
        loadMonthsInBackGround();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadMonthsInBackGround(){
        new AsyncTask<Void, Void, List<Month>>(){

            @Override
            protected List<Month> doInBackground(Void... params) {
                return Month.listAll(Month.class);
            }

            @Override
            protected void onPostExecute(List<Month> months) {
                super.onPostExecute(months);
                adapter.updateList(months);
            }
        }.execute();
    }
}
