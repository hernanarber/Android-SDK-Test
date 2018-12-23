package com.forter.hernanarber.fortertest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.forter.hernanarber.fortersdk.ForterSDK;

public class MainActivity extends AppCompatActivity {

    private ForterSDK forter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing the SDK:
        forter = ForterSDK.get();
        forter.init(this, "myApiKey"); // Developer Note: Replace myApiKey with YOUR Api Key.

        // testing with: http://mockbin.org/bin/e6ecaed3-43b5-4230-ae0c-e963a4aa58c2
        forter.setExportServerUrl("http://mockbin.org/bin/e6ecaed3-43b5-4230-ae0c-e963a4aa58c2");

        // Adding an Initial Event:
        // Developer Note: Use this Method to Track your Custom Events with ForsterSDK:
        forter.track("Action", "Init");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forter.track("Action", "Custom-Action");
                Snackbar.make(view, "Custom Action Tracked by ForterSDK", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // BONUS: Tracking Previous WIFI Networks:
        forter.trackPreviousNetworks(this);

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
