package mullerge.personalaccountent.month;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.dalFireBase.ExpenseRepo;
import mullerge.personalaccountent.dalFireBase.MonthRepo;
import mullerge.personalaccountent.event.Event;
import mullerge.personalaccountent.util.Currency;
import mullerge.personalaccountent.util.CurrencyLoader;

public class MonthFragment extends Fragment implements CreateEventDialoge.NewEventListener {

    private RecyclerView recyclerView;
    private MonthAdapter adapter;
    private MonthRepo monthRepo = new MonthRepo();
    private ExpenseRepo expenseRepo = new ExpenseRepo();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View monthsView = inflater.inflate(R.layout.fragment_month,container,false);

        return monthsView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar_month);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
         //toolbar.setTitle(null);

        initRecycleView();

        setUpSwipe();

        //NEM MUKODIK
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getActivePopup() != null){
                    adapter.getActivePopup().dismiss();
                    adapter.setActivePopup(null);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.addMOnthFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new CreateEventDialoge().show(getActivity().getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.month_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == R.id.action_exchangerates){
            PopupWindow exchangeRatesWindow = createExchangeRatesPopUp();
            exchangeRatesWindow.showAtLocation(getView(), Gravity.CENTER,0,0);
        }
        else if ( itemId == R.id.action_calculator){
            launchCalculator();
        }
        return super.onOptionsItemSelected(item);

    }

    public void initRecycleView(){
        recyclerView = (RecyclerView) getView().findViewById(R.id.month_recyclerView);
        adapter = new MonthAdapter();
        loadMonthsInBackGround();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadMonthsInBackGround(){
        monthRepo.loadMonths(adapter);
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

                Month deletedMonth = adapter.getMonths().get(position);
                confirmDelete(deletedMonth).show();
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(deleteCallBack);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public AlertDialog confirmDelete(Month deletedMonth)
    {
        AlertDialog deleteDialodge =new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int whichButton) {
                        monthRepo.deleteMonth(deletedMonth);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return deleteDialodge;
    }


    private PopupWindow createExchangeRatesPopUp(){
        LayoutInflater infalter = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View exchangeRatesView = infalter.inflate(R.layout.currencies_popup, null);

        CurrencyLoader currencyLoader = CurrencyLoader.getIntance(getContext());
        Map<String, Currency> currencies = currencyLoader.getLoadedCurrencies();

        TextView eurTV = (TextView)exchangeRatesView.findViewById(R.id.er_eur);
        TextView usdTV = (TextView)exchangeRatesView.findViewById(R.id.er_usd);
        TextView gbpTV = (TextView)exchangeRatesView.findViewById(R.id.er_gbp);
        TextView kunaTV = (TextView)exchangeRatesView.findViewById(R.id.er_kuna);


        eurTV.setText(String.valueOf(currencies.get(Currency.EURO).getRateToHuf()).substring(0,6) + "Ft");
        usdTV.setText(String.valueOf(currencies.get(Currency.USD).getRateToHuf()).substring(0,6) + "Ft");
        gbpTV.setText(String.valueOf(currencies.get(Currency.GBP).getRateToHuf()).substring(0,6) + "Ft");
        if(String.valueOf(currencies.get(Currency.KUNA).getRateToHuf()).length() >= 6) {
            kunaTV.setText(String.valueOf(currencies.get(Currency.KUNA).getRateToHuf()).substring(0, 6) + "Ft");
        }
        else {
            kunaTV.setText("This exchange rate will be updated tomorrow");
        }

        final PopupWindow exchangeRatesWindow = new PopupWindow(exchangeRatesView,
                                                        RecyclerView.LayoutParams.WRAP_CONTENT,
                                                        RecyclerView.LayoutParams.WRAP_CONTENT);
        exchangeRatesWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        exchangeRatesWindow.setElevation(5.0f);
        exchangeRatesWindow.setAnimationStyle(R.style.popup_window_animation);

        ImageButton closeBtn = (ImageButton) exchangeRatesView.findViewById(R.id.er_close_X);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeRatesWindow.dismiss();
            }
        });

        return exchangeRatesWindow;
    }

    private void launchCalculator(){
        final PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        String calcPackageName = null;
        int i = 0;
        boolean calcFound = false;
        while(!calcFound &&  i < packs.size() ){
            if( packs.get(i).packageName.toString().toLowerCase().contains("calcul")){
                calcPackageName = packs.get(i).packageName;
                calcFound = true;
            }
            i++;
        }
        if(calcPackageName != null){
            Intent intent = pm.getLaunchIntentForPackage(calcPackageName);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(),"No calculator found on your device", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onEventNameRecieved(String eventName) {

        Month newMonth = new Month();
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        newMonth.setMonth(now.get(Calendar.MONTH)-1);
        newMonth.setYear(now.get(Calendar.YEAR));
        newMonth.setIncome(450000);
        newMonth.setLabel(eventName);

        monthRepo.saveMonth(newMonth);
        expenseRepo.saveMonth(newMonth);
    }
}
