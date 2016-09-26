package com.example.himan.videotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himan.videotest.repository.PersonDatabaseRepo;
import com.example.himan.videotest.repository.SettingsDto;

/**
 * Created by himan on 29/8/16.
 */
public class HelpActivity extends Activity {
    private TextView  saveTextView;
    private ImageView minusImageView, plusImageView;
    private EditText messageEditText,hoursTextView;
    private PersonDatabaseRepo settingsDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsDatabaseHandler=new PersonDatabaseRepo();
        setContentView(R.layout.help);
        hoursTextView=(EditText)findViewById(R.id.hoursTextView);
        saveTextView=(TextView)findViewById(R.id.saveTextView);
        minusImageView=(ImageView)findViewById(R.id.minusImageView);
        plusImageView=(ImageView)findViewById(R.id.plusImageView);
        messageEditText=(EditText)findViewById(R.id.messageEditText);

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDto settingsDto=new SettingsDto();
                settingsDto.setMessage(messageEditText.getText().toString());
                settingsDto.setMinutes(Integer.parseInt(hoursTextView.getText().toString()));
//                settingsDatabaseHandler.updateSettings(settingsDto);
//                Toast.makeText(HelpActivity.this,settingsDatabaseHandler.getSettings(1).toString(),Toast.LENGTH_LONG).show();


            }
        });

    }
}
