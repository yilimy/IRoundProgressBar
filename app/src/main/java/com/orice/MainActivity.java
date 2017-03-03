package com.orice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orice.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {
    private RoundProgressBar progressBar;
    private int rate = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
    }

    public void decreaseAction(View view){
        progressBar.setProgress(progressBar.getProgress() - rate);
    }

    public void addAction(View view){
        progressBar.setProgress(progressBar.getProgress() + rate);
    }

    public void pauseAction(View view){
        progressBar.pause();
    }
}
