package mullerge.personalaccountent.month;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.zip.Inflater;

import mullerge.personalaccountent.R;

public class CreateEventDialoge extends AppCompatDialogFragment implements Dialog.OnClickListener {

    private Context context;
    private NewEventListener listener;
    private EditText eventNameET;

    public interface NewEventListener{
        public void onEventNameRecieved(String eventName);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String eventName = eventNameET.getText().toString();
        if(eventName != null && !eventName.isEmpty()){
            listener.onEventNameRecieved(eventName);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        listener = (NewEventListener) getFragmentManager().findFragmentByTag("month_fragment");

        return new AlertDialog.Builder(getContext()).setTitle("New Event").setView(getContentView()).setPositiveButton("create", this).setNegativeButton("cancel", null).create();
    }


    private View getContentView(){
        View newEventLayout = LayoutInflater.from(context).inflate(R.layout.create_event_dialoge,null);
        eventNameET = (EditText) newEventLayout.findViewById(R.id.eventName);

        return newEventLayout;
    }
}
