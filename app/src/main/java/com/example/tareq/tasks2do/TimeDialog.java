package com.example.tareq.tasks2do;
import java.sql.Time;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    EditText txtTime;
    public TimeDialog(View view){
        txtTime=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c =  Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour,minute,true);


    }

    public void onTimeSet(TimePicker view,int hour,int minute) {
        //show to the selected date in the text box
        String time = hour+ ":" + minute;
        txtTime.setText(time);
    }



}

