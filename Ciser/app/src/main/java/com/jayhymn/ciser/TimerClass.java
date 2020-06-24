package com.jayhymn.ciser;

import android.widget.Toast;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TimerClass {
    private final int interval = 3000; // 1 Second
    private String msg;

    public TimerClass(String msg){
        this.msg = msg;
    }
    private Handler handler = new Handler() {
        @Override
        public void publish(LogRecord logRecord) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };
}
