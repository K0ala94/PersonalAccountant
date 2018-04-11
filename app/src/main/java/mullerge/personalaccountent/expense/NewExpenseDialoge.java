package mullerge.personalaccountent.expense;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import mullerge.personalaccountent.R;
import mullerge.personalaccountent.util.Currency;

public class NewExpenseDialoge extends AppCompatDialogFragment implements Dialog.OnClickListener {


    private Context context;
    private EditText amountET;
    private EditText descET;
    private Spinner typeSpinner;
    private Spinner currencySpinner;
    private DatePicker datePicker;

    private NewExpenseListener listener;

    public interface NewExpenseListener{
        public void onNewExpenseListener(Expense newExpense);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        listener.onNewExpenseListener(getNewExpense());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        listener = (NewExpenseListener) getFragmentManager().findFragmentByTag("expenses_fragment");

        return new AlertDialog.Builder(getContext()).setTitle("New Expense").setView(getContentView()).setPositiveButton("save", this).setNegativeButton("cancel", null).create();
    }

    private Expense getNewExpense(){
        Expense newExpense = new Expense();
        if(amountET.getText() != null && amountET.getText().length() > 0 ) {
            newExpense.setAmount(Double.parseDouble(amountET.getText().toString()));
        }
        else{
            newExpense.setAmount(0);
        }
        if(typeSpinner.getSelectedItem() != null) {
            newExpense.setType(typeSpinner.getSelectedItem().toString());
        }
        if(descET.getText() != null && descET.getText().length() > 0) {
            newExpense.setDescription(descET.getText().toString());
        }
        else{
            newExpense.setDescription(getString(R.string.nodetail));
        }
        if(currencySpinner != null){
            newExpense.setCurrency(currencySpinner.getSelectedItem().toString());
        }

        newExpense.setDate(new Date().getTime());

        return  newExpense;
    }


    private View getContentView(){
        View newExpenseView =  LayoutInflater.from(context).inflate(R.layout.new_expense_layout,null);

        amountET = (EditText) newExpenseView.findViewById(R.id.newexpense_amount);
        descET = (EditText) newExpenseView.findViewById(R.id.newexpense_desc);
        typeSpinner = (Spinner) newExpenseView.findViewById(R.id.newexpense_type);
        typeSpinner.setAdapter(new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, ExpenseType.TYPES.toArray()));

        currencySpinner = (Spinner) newExpenseView.findViewById(R.id.currency_spinner);
        currencySpinner.setAdapter(new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, Currency.CURRENCIES));

        amountET.setInputType(InputType.TYPE_CLASS_NUMBER);

        return newExpenseView;
    }

}
