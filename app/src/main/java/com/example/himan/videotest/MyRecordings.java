package com.example.himan.videotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MyRecordings extends Activity {
    private RecyclerView mRecyclerView;
    private ImageView imageViewClose;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.my_recordings);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imageViewClose=(ImageView)findViewById(R.id.imageViewClose);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyRecordings.this));
        new RemoteDataTask().execute();
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyRecordings.this.finish();
//                overridePendingTransition(R.anim.push_left, R.anim.push_right);


            }
        });
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, ArrayList<Data>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Data> doInBackground(Void... params) {
            ArrayList<Data> dataArrayList = new ArrayList<>();
            File f = FolderStructure.getInstance().getVideoFolder();

            File[] files = f.listFiles();
            for (File inFile : files) {
                if (!inFile.isDirectory()) {
                    dataArrayList.add(new Data(inFile.getName(), inFile.getAbsolutePath()));
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
            viewHolder.imageViewPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyRecordings.this, VideoPlayBack.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left, R.anim.push_right);

                }
            });
            new BitmapWorkerTask(viewHolder.imageViewThumbnails, itemsData.get(position).fileUrl).execute();
//            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(itemsData.get(position).fileUrl, MediaStore.Video.Thumbnails.MICRO_KIND);
//            BitmapDrawable background = new BitmapDrawable(getResources(),bMap);
//            viewHolder.linearLayoutThumbnail.setBackgroundDrawable(background);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtViewTitle;
            public LinearLayout linearLayoutThumbnail;
            public ImageView imageViewThumbnails,imageViewPlay;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.fname);
                linearLayoutThumbnail = (LinearLayout) itemLayoutView.findViewById(R.id.linearLayoutThumbnail);
                imageViewThumbnails=(ImageView)itemLayoutView.findViewById(R.id.imageViewThumbnails);
                imageViewPlay=(ImageView)itemLayoutView.findViewById(R.id.imageViewPlay);


            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }


        class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;
            private int data = 0;
            private String fileUrl;

            public BitmapWorkerTask(ImageView linearLayout, String fileurl) {
                // Use a WeakReference to ensure the ImageView can be garbage collected
                imageViewReference = new WeakReference<ImageView>(linearLayout);
                this.fileUrl = fileurl;
            }

            // Decode image in background.
            @Override
            protected Bitmap doInBackground(Integer... params) {
             //   data = params[0];
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(fileUrl, MediaStore.Video.Thumbnails.MICRO_KIND);

                return bMap;
            }

            // Once complete, see if ImageView is still around and set bitmap.
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
//                        imageView.getScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            }
        }


    }

    private class Data {
        private String name;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        private String fileUrl;

        public Data() {
        }

        public Data(String name, String fileUrl) {
            this.name = name;
            this.fileUrl = fileUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = getWindow().getDecorView();
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

}