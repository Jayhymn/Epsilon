
package com.jayhymn.eckankarbackpass.Event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class EventDB extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "EventApp";
    private static final String DATABASE_TABLE = "event";
    private static final String FIELD_INDEX  = "ID";
    private static final String FIELD_TITLE  = "title";
    private static final String FIELD_DATE  = "date";
    private static final String FIELD_SATRT_TIME  = "start";
    private static final String FIELD_END_TIME  = "endTime";

    public EventDB(Context context){
        super(context, DATABASE_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MSG_TABLE =
                "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE+
                        " ("+FIELD_INDEX+" INTEGER NOT NULL, "+FIELD_TITLE+" text NOT NULL, "
                        +FIELD_DATE+" text NOT NULL, "
                        +FIELD_SATRT_TIME+ " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        +FIELD_END_TIME+" timestamp NOT NULL, PRIMARY KEY ("+FIELD_INDEX+"))";
        sqLiteDatabase.execSQL(CREATE_MSG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
    /*public void storeEvent(EventObject obj){
        SQLiteDatabase db = this.getWritableDatabase();
        //create sql insert object to hold values to be stored
        ContentValues values = new ContentValues();

        //set appropriate values to the table columns
        values.put(FIELD_TITLE, obj.getTitle());
        values.put(FIELD_DATE, obj.getDate());
        values.put(FIELD_SATRT_TIME,obj.getStartTime() );
        values.put(FIELD_END_TIME, obj.getEndTime());

        // Inserting Row
        db.insert(DATABASE_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public ArrayList<EventObject> getMessages(String title){

        ArrayList<EventObject> qDataChats = new ArrayList<>();

        String selectQuery = "SELECT * FROM "+DATABASE_TABLE+" WHERE " +
                "("+ FIELD_TITLE + title +")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                qDataChats.add(new EventObject(cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        db.close(); // Closing database connection
        // return contact list
        return qDataChats;
    }
    */
}
