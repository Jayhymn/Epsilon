package com.jayhymn.eckankarbackpass;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeText implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private  Context context;
    private int hour, minutes;
    private EditText editText;
    private Calendar mCalendar;
    private SimpleDateFormat mFormat;

    public TimeText(Context context, EditText editText){
        Activity act = (Activity)context;
        this.editText = editText;
        this.editText.setOnClickListener(this);
        this.context = context;
    }
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
        updateTime();
    }

    @Override
    public void onClick(View view) {
        mCalendar = Calendar.getInstance(TimeZone.getDefault());
        if (mCalendar == null)
            mCalendar = Calendar.getInstance();

        hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        minutes = mCalendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(context, this,
                hour, minutes, true);
        dialog.show();
    }

    private void updateTime() {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minutes);

        if (mFormat == null)
            mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        this.editText.setText(mFormat.format(mCalendar.getTime()));
    }
}
