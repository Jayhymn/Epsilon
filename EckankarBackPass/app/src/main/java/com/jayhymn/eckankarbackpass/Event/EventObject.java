package com.jayhymn.eckankarbackpass.Event;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class EventObject implements Serializable {
    private String title;
    private String duration, events;
    private int count;

public EventObject(String title, String duration, String events, int count){
    this.title = title;
    this.duration = duration;
    this.events = events;
    this.count = count;
}

    public void setTitle(String title){ this.title = title; }
    public void setDuration(String duration){
        this.duration = duration;
    }
    public void setEvents (String events){this.events = events;}
    public void getCount (int count){this.count = count;}

    protected EventObject(Parcel in) {
        title = in.readString();
        duration = in.readString();
        events = in.readString();
        count = in.readInt();
    }

    public static final Parcelable.Creator<EventObject> CREATOR = new Parcelable.Creator<EventObject>() {
        @Override
        public EventObject createFromParcel(Parcel in) {
            return new EventObject(in);
        }

        @Override
        public EventObject[] newArray(int size) {
            return new EventObject[size];
        }
    };

    public String getTitle(){return title;}
    public String getDuration() { return duration; }
    public String getEvents(){return events;}
    public int getCount(){return count;}
}
