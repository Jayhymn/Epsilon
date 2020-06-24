package com.jayhymn.eckankarbackpass;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

public class DateText implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
private int eventDay, eventMonth, eventYear;
private Context context;
private EditText editText;

    public DateText (Context context, EditText view){
        Activity act = (Activity)context;
        this.editText = view;
        this.editText.setOnClickListener(this);
        this.context = context;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        eventYear = year;
        eventMonth = monthOfYear;
        eventDay = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    private void updateDisplay() {
        editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(eventDay).append("/").append(eventMonth + 1).append("/").append(eventYear).append(" "));
    }
}
