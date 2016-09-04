package com.example.himan.videotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by himan on 31/7/16.
 */
public class SosActivity extends Activity {

    private RecyclerView mRecyclerView;
    PersonDatabaseHandler personDatabaseHandler;
    private ImageView imageViewAddPerson,imageViewDeletePerson,imageViewRecord,settingsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personDatabaseHandler=new PersonDatabaseHandler(this);
        setContentView(R.layout.sos);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imageViewAddPerson=(ImageView) findViewById(R.id.imageViewAddPerson);
        imageViewDeletePerson=(ImageView) findViewById(R.id.imageViewDeletePerson);
        imageViewRecord=(ImageView)findViewById(R.id.imageViewRecord);
        settingsImageView=(ImageView)findViewById(R.id.settingsImageView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SosActivity.this));
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
                Intent intent = new Intent(SosActivity.this, AudioRecordService.class);
                startService(intent);

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
        new RemoteDataTask().execute();


    }

    @Override
    protected void onResume() {
        super.onResume();
        new RemoteDataTask().execute();
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, List<Person>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Person> doInBackground(Void... params) {


            return personDatabaseHandler.getAllContacts();
        }

        @Override
        protected void onPostExecute(List<Person> dataArrayList) {
            MyAdapter adapter = new MyAdapter(dataArrayList, SosActivity.this);
            mRecyclerView.setAdapter(adapter);


        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Person> itemsData;
        Context context;

        public MyAdapter(List<Person> itemsData, Context context) {

            this.context = context;
            this.itemsData = itemsData;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sos, parent,false);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {


            viewHolder.textViewName.setText(itemsData.get(position).getName());
            viewHolder.imageViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SosActivity.this,VideoPlayBack.class);
                    startActivity(intent);
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewName;
            public ImageView imageViewCall;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textViewName = (TextView) itemLayoutView.findViewById(R.id.textViewName);
                imageViewCall=(ImageView)itemLayoutView.findViewById(R.id.imageViewCall);

            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }


    }

}
