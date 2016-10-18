package com.example.himan.videotest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.io.File;
import java.io.FileFilter;

/**
 * Created by himan on 16/10/16.
 */
public class AppSettings extends Activity {

    private Spinner videoSpinner, audioSpinner, splitSpinner;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        sharedPreferences = this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), MODE_PRIVATE);
        init();
    }

    public void init() {
        videoSpinner = (Spinner) findViewById(R.id.videoSpinner);
        audioSpinner = (Spinner) findViewById(R.id.audioSpinner);
        splitSpinner = (Spinner) findViewById(R.id.splitSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.av_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> splitAdapter = ArrayAdapter.createFromResource(this,
                R.array.split_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        splitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        audioSpinner.setAdapter(adapter);
        videoSpinner.setAdapter(adapter);
        splitSpinner.setAdapter(splitAdapter);
        String audioQuality;
        if(( audioQuality=sharedPreferences.getString(getString(R.string.Audio_Quality),null))!=null){
            audioSpinner.setSelection(adapter.getPosition(audioQuality));
        }
        String videoQuality;
        if(( videoQuality=sharedPreferences.getString(getString(R.string.Video_Quality),null))!=null){
            videoSpinner.setSelection(adapter.getPosition(videoQuality));
        }

        String splitFrame;
        if(( splitFrame=sharedPreferences.getString(getString(R.string.Split_Frame),null))!=null){
            splitSpinner.setSelection(splitAdapter.getPosition(splitFrame));
        }



        audioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.Audio_Quality), (String) adapter.getItem(position));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(getString(R.string.Video_Quality), (String) adapter.getItem(position));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.Split_Frame), (String) splitAdapter.getItem(position));
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
