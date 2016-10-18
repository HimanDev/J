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
public class SosActivity extends Activity {

    private RecyclerView mRecyclerView;
    PersonDatabaseRepo personDatabaseHandler;
    private ImageView imageViewAddPerson, imageViewDeletePerson, imageViewRecord, settingsImageView;
    private MyAdapter adapter;
    private SharedPreferences sharedPreferences;
    private TextView timerTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreferences = this.getSharedPreferences(this.getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), Context.MODE_PRIVATE);

//        new GoogleDriveOperator(this,FolderStructure.getInstance().getGoogleApiClient(), FolderStructure.getInstance().getGoogleAccountCredential()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FolderStructure.getInstance().getQueue());
        personDatabaseHandler = new PersonDatabaseRepo();
        setContentView(R.layout.sos);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SosActivity.this));
//        adapter=new MyAdapter(personDatabaseHandler.getAllContacts(),this);
//        mRecyclerView.setAdapter(adapter);

        imageViewAddPerson = (ImageView) findViewById(R.id.imageViewAddPerson);
        imageViewDeletePerson = (ImageView) findViewById(R.id.imageViewDeletePerson);
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        settingsImageView = (ImageView) findViewById(R.id.settingsImageView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        updateUi();
        imageViewAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this, AddPerson.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left, R.anim.push_right);
            }
        });
        imageViewDeletePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this, DeletePersonActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left, R.anim.push_right);
            }
        });
        imageViewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getInt(getString(R.string.sos_minutes), 0) != 0 || sharedPreferences.getString(getString(R.string.sos_message), null) != null) {
                    new AlertDialog.Builder(SosActivity.this)
                            .setTitle("Delete Confirmation")
                            .setMessage("Yes-")
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(SosActivity.this, HelpActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left, R.anim.push_right);
                                }
                            })
                            .create()
                            .show();

                } else {

                    Intent intent = new Intent(SosActivity.this, AudioRecordService.class);
                    startService(intent);
                }


            }
        });
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SosActivity.this, HelpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left, R.anim.push_right);
            }
        });
        new RemoteDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    public void updateUi() {
        if (AudioRecordService.isRunning) {
            imageViewRecord.setVisibility(View.GONE);
            timerTextView.setVisibility(View.VISIBLE);
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
        new RemoteDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        timeUpdateBroadcastReceiver = new TimeUpdateBroadcastReceiver();
        uiBroadcastReceiver=new UiBroadcastReceiver();
        registerReceiver(timeUpdateBroadcastReceiver, new IntentFilter(AudioRecordService.UPDATE_TIME_EVENT));
        registerReceiver(uiBroadcastReceiver, new IntentFilter(AudioRecordService.UI_UPDATE));



    }

    private class RemoteDataTask extends AsyncTask<Void, Void, List<PersonDto>> {
        @Override
        protected List<PersonDto> doInBackground(Void... params) {


            return personDatabaseHandler.getAllContacts();
        }

        @Override
        protected void onPostExecute(List<PersonDto> dataArrayList) {
            adapter = new MyAdapter(dataArrayList, SosActivity.this);
            mRecyclerView.setAdapter(adapter);


        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<PersonDto> itemsData;
        Context context;
        private List<Integer> positionsItemData = new ArrayList<>();

        public MyAdapter(List<PersonDto> itemsData, Context context) {

            this.context = context;
            this.itemsData = itemsData;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sos, parent, false);

            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {


            viewHolder.textViewName.setText(itemsData.get(position).getName());
            viewHolder.imageViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(SosActivity.this, VideoPlayBack.class);
//                    startActivity(intent);
                }
            });
            viewHolder.containerLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.checkImageView.getVisibility() != View.VISIBLE) {
                        viewHolder.checkImageView.setVisibility(View.VISIBLE);
                        positionsItemData.add(position);
                    } else {
                        viewHolder.checkImageView.setVisibility(View.INVISIBLE);
                        if (positionsItemData.contains(new Integer(position)))
                            positionsItemData.remove(new Integer(position));
                    }

                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewName;
            public ImageView imageViewCall, checkImageView;
            LinearLayout containerLL;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textViewName = (TextView) itemLayoutView.findViewById(R.id.textViewName);
                imageViewCall = (ImageView) itemLayoutView.findViewById(R.id.imageViewCall);
                checkImageView = (ImageView) itemLayoutView.findViewById(R.id.checkImageView);
                containerLL = (LinearLayout) itemLayoutView.findViewById(R.id.containerLL);

            }


        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }


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
