package com.jzallas.lifecycleaware;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenericLogger {

    private static final int MAX_LOG_SIZE = 1000; // characters

    private TextView textView;

    public void attachTextView(TextView textView) {
        this.textView = textView;
    }

    public void log() {
        log("Generic event. This event has no context on when it was run.");
    }

    protected void log(String message) {
        final String logTag = this.getClass().getSimpleName();
        Log.i(logTag, message);

        appendtoTextView(logTag, message);
    }

    /**
     * Appends the new message (along with a timestamp) to the top of the TextView if it was supplied.
     *
     * @param tag
     * @param message
     */
    private void appendtoTextView(String tag, String message) {
        if (textView == null) {
            return;
        }
        String existingText = textView.getText().toString();

        StringBuilder builder = new StringBuilder(existingText);
        String time = SimpleDateFormat.getTimeInstance().format(new Date());

        builder.insert(0, String.format("[%s](%s)%s\n", time, tag, message));

        // limit this builder to 1000 characters
        String truncatedLog = builder.length() >= MAX_LOG_SIZE ? builder.substring(0, MAX_LOG_SIZE) : builder.toString();

        textView.setText(truncatedLog);
    }
}
