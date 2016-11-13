package com.example.himan.videotest;



        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.preference.MultiSelectListPreference;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.himan.videotest.domains.PersonDto;
        import com.example.himan.videotest.repository.PersonDatabaseRepo;

        import java.net.MulticastSocket;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by himan on 31/7/16.
 */
public class SosMainActivity extends Activity {

    private ImageView  imageViewRecord;
    private SharedPreferences sharedPreferences;
    private TextView timerTextView,sosMassageTextview;
    private   SharedPreferences sharedPreferencesSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreferences = this.getSharedPreferences(this.getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), Context.MODE_PRIVATE);
         sharedPreferencesSettings = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.sos_main);

        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        sosMassageTextview=(TextView)findViewById(R.id.sosMassageTextview);
                sosMassageTextview.setText(sharedPreferencesSettings.getString(getString(R.string.sos_message),getString(R.string.sos_default)));

        updateUi();
      
      
        imageViewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getInt(getString(R.string.sos_minutes), 0) != 0 || sharedPreferences.getString(getString(R.string.sos_message), null) != null) {
                    new AlertDialog.Builder(SosMainActivity.this)
                            .setTitle("Delete Confirmation")
                            .setMessage("Yes-")
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(SosMainActivity.this, HelpActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left, R.anim.push_right);
                                }
                            })
                            .create()
                            .show();

                } else {

                    Intent intent = new Intent(SosMainActivity.this, AudioRecordService.class);
                    startService(intent);
                }


            }
        });

    }

    public void updateUi() {
        if (AudioRecordService.isRunning) {
            imageViewRecord.setVisibility(View.GONE);
            timerTextView.setVisibility(View.VISIBLE);
            timerTextView.setText("Sent");
        } else {
            imageViewRecord.setVisibility(View.VISIBLE);
            timerTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeUpdateBroadcastReceiver != null)
            unregisterReceiver(timeUpdateBroadcastReceiver);
        if (uiBroadcastReceiver != null)
            unregisterReceiver(uiBroadcastReceiver);
    }

    private TimeUpdateBroadcastReceiver timeUpdateBroadcastReceiver;
    private UiBroadcastReceiver uiBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        timeUpdateBroadcastReceiver = new TimeUpdateBroadcastReceiver();
        uiBroadcastReceiver=new UiBroadcastReceiver();
        registerReceiver(timeUpdateBroadcastReceiver, new IntentFilter(AudioRecordService.UPDATE_TIME_EVENT));
        registerReceiver(uiBroadcastReceiver, new IntentFilter(AudioRecordService.UI_UPDATE));



    }



    public class TimeUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            timerTextView.setText(intent.getStringExtra(AudioRecordService.TIME_REMAINING));
            updateUi();

        }
    }

    public class UiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateUi();
        }
    }

}
