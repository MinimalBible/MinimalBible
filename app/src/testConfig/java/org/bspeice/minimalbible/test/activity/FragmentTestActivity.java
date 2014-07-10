package org.bspeice.minimalbible.test.activity;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import org.bspeice.minimalbible.R;

import java.util.concurrent.CountDownLatch;

public class FragmentTestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startFragment(Fragment f) {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.container, f)
                .commit();

        // Do the ugly work of waiting for the transaction to complete...
        final CountDownLatch signal = new CountDownLatch(1);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                fragmentManager.executePendingTransactions();
                signal.countDown();
            }
        });
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
