package com.jayhymn.eckankarbackpass.BetaClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jayhymn.eckankarbackpass.DateText;
import com.jayhymn.eckankarbackpass.R;
import com.jayhymn.eckankarbackpass.TimeText;

public class Cycler_Form extends RecyclerView.Adapter<Cycler_Form.ViewHolder> {
    private Context context;
    private int count;

public Cycler_Form(Context context, int number){
    this.context = context;
    this.count = number;
}
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.form_inflater,null,false);
        ViewHolder holder = new ViewHolder(view);

        //int c = holder.getAdapterPosition() + 1;
        //holder.parentForm.setId(c);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    View v = holder.itemView;
        View vDate = holder.date;

        //holder.getItemId();

        View vstartfrom = holder.startFrom;
        View vendTo = holder.endTo;
        View mDay = holder.day;

        int dayCount = position + 1;
        ((TextView) mDay).setText("Day" + dayCount);


    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    //params.

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(150,
                60);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(280,
                60);

        vDate.setLayoutParams(param1);
        vstartfrom.setLayoutParams(param);
        vendTo.setLayoutParams(param);
    v.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        EditText date, startFrom, endTo;
        TextView day;
        LinearLayout parentForm;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateEvent);
            //date.setId(Integer.parseInt(String.valueOf(countViews) + 1));

            //enable the editText to be able to pick a date
            new DateText(context, date);

            startFrom = itemView.findViewById(R.id.fromTime);

            endTo = itemView.findViewById(R.id.toTime);

            //enable timepicker on the editText views
            new TimeText(context, startFrom);
            new TimeText(context, endTo);

            day = itemView.findViewById(R.id.day);
            parentForm = itemView.findViewById(R.id.parentForm);
        }
    }
}
