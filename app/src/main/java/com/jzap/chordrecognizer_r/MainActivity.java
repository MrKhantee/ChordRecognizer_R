package com.jzap.chordrecognizer_r;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    TextView mTv_chord, mTv_mostIntenseNote, mTv_secMostIntenseNote, mTv_thirdMostIntenseNote;
    RadioButton mRb_recording;
    Switch mSwitch_autoDetect;

// Activity Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMembers();

        new Thread(new MainWorkerRunnable(this)).start();
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
// End Activity Overrides

    private void initializeMembers() {
        mTv_chord = (TextView) findViewById(R.id.tv_chord);
        mTv_mostIntenseNote = (TextView) findViewById(R.id.tv_mostIntenseNote);
        mTv_secMostIntenseNote = (TextView) findViewById(R.id.tv_secMostIntenseNote);
        mTv_thirdMostIntenseNote = (TextView) findViewById(R.id.tv_thirdMostIntenseNote);
        mRb_recording = (RadioButton) findViewById(R.id.rb_recording);
        mSwitch_autoDetect = (Switch) findViewById(R.id.switch_autoDetect);
    }

// Accessors/Mutators
    public TextView getmTv_chord() {
        return mTv_chord;
    }

    public TextView getmTv_mostIntenseNote() {
        return mTv_mostIntenseNote;
    }

    public TextView getmTv_secMostIntenseNote() {
        return mTv_secMostIntenseNote;
    }

    public TextView getmTv_thirdMostIntenseNote() {
        return mTv_thirdMostIntenseNote;
    }

    public RadioButton getmRb_recording() {
        return mRb_recording;
    }

    public Switch getmSwitch_autoDetect() {
        return mSwitch_autoDetect;
    }
// End Accessors/Mutators



}
