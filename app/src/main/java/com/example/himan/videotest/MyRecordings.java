package com.example.himan.videotest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class MyRecordings extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.my_recordings);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyRecordings.this));
        new RemoteDataTask().execute();
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, ArrayList<Data>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Data> doInBackground(Void... params) {
            ArrayList<Data> dataArrayList=new ArrayList<>();
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyCameraApp");

            File[] files = f.listFiles();
            for (File inFile : files) {
                if (!inFile.isDirectory()) {
                    dataArrayList.add(new Data(inFile.getName()));
                }
            }

            return dataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> dataArrayList) {
            MyAdapter adapter = new MyAdapter(dataArrayList, MyRecordings.this);
            mRecyclerView.setAdapter(adapter);


        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Data> itemsData;
        Context context;

        public MyAdapter(ArrayList<Data> itemsData, Context context) {

            this.context = context;
            this.itemsData = itemsData;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_recordings, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {


            viewHolder.txtViewTitle.setText(itemsData.get(position).getName());

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtViewTitle;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.fname);


            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }


    }

    private class Data {
        private String name;
        public Data() {
        }

        public Data(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}