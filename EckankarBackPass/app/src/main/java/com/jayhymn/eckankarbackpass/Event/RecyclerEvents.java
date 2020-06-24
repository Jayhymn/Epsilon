package com.jayhymn.eckankarbackpass.Event;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jayhymn.eckankarbackpass.R;

import java.util.ArrayList;

public class RecyclerEvents extends RecyclerView.Adapter<RecyclerEvents.ViewHolder> {
    private ArrayList<EventObject> mEvent;
    public static ArrayList<Integer> selectedPos = new ArrayList<>();

    //private Context context;
    public RecyclerEvents (ArrayList<EventObject> mEvent){
        this.mEvent = mEvent;
        //this.context = context;
    }
    /*public RecyclerEvents (ArrayList<EventObject> mEvent, Context context){
        this.mEvent = mEvent;
        this.context = context;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.single_event,null,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(mEvent.get(position).getTitle());
        holder.duration.setText(mEvent.get(position).getDuration());

        //Item item = items.get(position);
        for (int e:selectedPos){
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(e == position ?
                    Color.rgb(173,216,230): Color.TRANSPARENT);
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = holder.getAdapterPosition();
                if (!selectedPos.isEmpty()){
                    if (!(selectedPos.contains(a))){
                        selectedPos.add(a);
                        notifyItemChanged(a);
                    }
                    else {
                        selectedPos.remove(selectedPos.indexOf(a));
                        notifyItemChanged(a);
                    }
                }
                else {
                    //Intent intent = new Intent();
                }
                //selectedPos = RecyclerView.NO_POSITION;
            }
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int e = holder.getAdapterPosition();
                if (selectedPos.contains(e)){

                    return false;
                }
                else {
                    // Updating old as well as new positions
                    selectedPos.add(e);
                    notifyItemChanged(e);

                    return true;
                }

            }
        });
    }
    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, duration;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.event_templete);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(params);
            title = itemView.findViewById(R.id.soloEvent);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
