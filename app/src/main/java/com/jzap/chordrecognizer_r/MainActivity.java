package com.jzap.chordrecognizer_r;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView mTv_chord, mTv_mostIntenseNote, mTv_secMostIntenseNote, mTv_thirdMostIntenseNote;
    private ImageView mIv_button;
    private Drawable mDr_button, mDr_readyButton;
    private NotesGraphView mNgv_graph;

    private boolean mRecording;

    private MainWorkerRunnable mWorkerRunnable;

// Activity Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume() : resumed");
        super.onResume();
        initializeMembers();
        new Thread(mWorkerRunnable).start();

        makeGraph();

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

    @Override
    protected void onPause() {
        super.onStop();
        Log.i(TAG, "onPause() : Paused");
        resetButton();
        mWorkerRunnable.setmEndRunnable(true);
        mNgv_graph.setmEndRunnable(true);
    }
// End Activity Overrides

    private void initializeMembers() {
        mTv_chord = (TextView) findViewById(R.id.tv_chord);
        mTv_mostIntenseNote = (TextView) findViewById(R.id.tv_mostIntenseNote);
        mTv_secMostIntenseNote = (TextView) findViewById(R.id.tv_secMostIntenseNote);
        mTv_thirdMostIntenseNote = (TextView) findViewById(R.id.tv_thirdMostIntenseNote);
        mIv_button = (ImageView) findViewById(R.id.button);
        mDr_button = getResources().getDrawable(R.drawable.button);
        mDr_readyButton = getResources().getDrawable(R.drawable.readybutton);
        //mNgv_graph =  new NotesGraphView(this);

        mRecording = false;

        mWorkerRunnable = new MainWorkerRunnable(this);
    }

    private void resetButton() {
        mIv_button.setImageDrawable(mDr_button);
        mRecording = false;
    }


    private void makeGraph() {
        Log.i(TAG, "makeGraph()");
        mNgv_graph =  new NotesGraphView(this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mNgv_graph.setLayoutParams(lP);
        relativeLayout.addView(mNgv_graph);
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

    public ImageView getmIv_button() {
        return mIv_button;
    }

    public NotesGraphView getmNgv_graph() { return mNgv_graph; }

    public void setmRecording(View v) {
        mRecording = !mRecording;

        if (mRecording) {
            Log.i(TAG, "mRecording");
            mIv_button.setImageDrawable(mDr_readyButton);
        } else {
            Log.i(TAG, "!mRecording");
            mIv_button.setImageDrawable(mDr_button);
        }
    }

    public boolean getmRecording() {
        return mRecording;
    }
// End Accessors/Mutators



}
