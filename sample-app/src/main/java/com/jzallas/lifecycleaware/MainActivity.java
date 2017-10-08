package com.jzallas.lifecycleaware;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // logger1.onLifecycleEvent(...) during onStart(...)
    @LifecycleAware(Lifecycle.Event.ON_START)
    LifecycleLogger logger1 = new LifecycleLogger();

    // logger2.log() during onResume(...)
    @LifecycleAware(value = Lifecycle.Event.ON_RESUME, method = "log")
    GenericLogger logger2 = new GenericLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind the components to a lifecycle
        LifecycleBinder.bind(this);

        TextView textView = findViewById(R.id.textview1);
        logger1.attachTextView(textView);
        logger2.attachTextView(textView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab)
                .setOnClickListener(view -> startSecondActivity());
    }

    private void startSecondActivity() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
