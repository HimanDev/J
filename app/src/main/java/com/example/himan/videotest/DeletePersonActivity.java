package com.example.himan.videotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by himan on 31/7/16.
 */

public class DeletePersonActivity extends Activity {

    private RecyclerView mRecyclerView;
    PersonDatabaseHandler personDatabaseHandler;
    private ImageView imageViewAddPerson;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personDatabaseHandler=new PersonDatabaseHandler(this);
        setContentView(R.layout.delete_user);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter=new MyAdapter(personDatabaseHandler.getAllContacts(),getApplication());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DeletePersonActivity.this));

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
            MyAdapter adapter = new MyAdapter(dataArrayList, DeletePersonActivity.this);
            adapter.notifyDataSetChanged();



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
                    .inflate(R.layout.item_delete_person, parent,false);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {


            viewHolder.textViewName.setText(itemsData.get(position).getName());
            viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personDatabaseHandler.deleteContact(itemsData.get(position));
                    itemsData.remove(position);
                    notifyDataSetChanged();

                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewName;
            public ImageView imageViewDelete;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textViewName = (TextView) itemLayoutView.findViewById(R.id.textViewName);
                imageViewDelete=(ImageView)itemLayoutView.findViewById(R.id.imageViewDelete);

            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return (itemsData==null) ? 0:itemsData.size();
        }


    }

}
