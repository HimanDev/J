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
import android.support.annotation.NonNull;
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

import com.example.himan.videotest.repository.DriveResourceDto;
import com.example.himan.videotest.repository.DriveResourceRepo;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.internal.DriveServiceResponse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.Permission;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import needle.Needle;


public class MyRecordings extends Activity {
    private RecyclerView mRecyclerView;
    private ImageView imageViewClose;
    private MyAdapter adapter;
    private static final String TAG = "MYREC";


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.my_recordings);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyRecordings.this));
        new RemoteDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
        protected ArrayList<Data> doInBackground(Void... params) {
            Log.i("Remote", getStatus().name());
            ArrayList<Data> dataArrayList = new ArrayList<>();
            File rootVideoFolder = FolderStructure.getInstance().getRootVideoFolder();
            File[] files = rootVideoFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory() && file.list().length > 0) {
                    for (File videoFiles : file.listFiles()) {
                        dataArrayList.add(new Data(file.getName(), videoFiles.getAbsolutePath(), file.getAbsolutePath(),new Date(file.lastModified()),FolderType.VIDEO));
                        break;
                    }
                }
            }

            File rootAudioFolder = FolderStructure.getInstance().getRootAudioFolder();
            File[] filesAudio = rootAudioFolder.listFiles();
            for (File file : filesAudio) {
                if (file.isDirectory() && file.list().length > 0) {
                        dataArrayList.add(new Data(file.getName(), null, file.getAbsolutePath(),new Date(file.lastModified()),FolderType.AUDIO));

                }
            }
            Collections.sort(dataArrayList,new DataComparatoor());
            Collections.reverse(dataArrayList);

            Log.i("Remote", getStatus().name());
            return dataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> dataArrayList) {
            adapter = new MyAdapter(dataArrayList, getApplicationContext());
            mRecyclerView.setAdapter(adapter);

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Data> itemsData;
        Context context;

        public MyAdapter(List<Data> itemsData, Context context) {

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
                    intent.putExtra("folderLocation", itemsData.get(position).getFolderLocation());
                    intent.putExtra("folderType",itemsData.get(position).getFolderType());
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left, R.anim.push_right);

                }
            });
            viewHolder.shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  final  DriveResourceDto driveResourceDto = new DriveResourceRepo().getDriveResource(itemsData.get(position).getName());
                    if (driveResourceDto != null)
                        if (driveResourceDto.getLink() == null || !driveResourceDto.getLink().equals("")) {
                        new GetResourceId(driveResourceDto).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                        shareTextUrl(driveResourceDto.getLink());
                        }


                }
            });
            if(itemsData.get(position).fileUrl!=null){
                new BitmapWorkerTask(viewHolder.imageViewThumbnails, itemsData.get(position).fileUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else {
                viewHolder.imageViewThumbnails.setImageResource(R.drawable.musical_note);
            }
//            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(itemsData.get(position).fileUrl, MediaStore.Video.Thumbnails.MICRO_KIND);
//            BitmapDrawable background = new BitmapDrawable(getResources(),bMap);
//            viewHolder.linearLayoutThumbnail.setBackgroundDrawable(background);

        }

        private class GetResourceId extends AsyncTask<Void, Void, String> {

            private String resourceId = null;
            private boolean changeListenerExecuted = false;
            private boolean syncExecuted = false;
            private final static String LINK_APPEND_RESOURCE_ID = "https://drive.google.com/open?id=";
            private  DriveResourceDto driveResourceDto;

            public GetResourceId(DriveResourceDto driveResourceDto) {
                this.driveResourceDto = driveResourceDto;
            }


            @Override
            protected String doInBackground(Void...params) {

                DriveId driveFolderId = DriveId.decodeFromString(driveResourceDto.getDriveId());
                if (driveFolderId == null) {
                    //show pop up file not found on the location in the google drive,
                    //might be deleted manually
                    return null;
                }
                if (driveFolderId.getResourceId() != null && !driveFolderId.equals("")) {
                    // rest api call give permission to the resource
                    allowSharePermission();
                    return LINK_APPEND_RESOURCE_ID + driveFolderId.getResourceId();
                }
                driveFolderId.asDriveFolder().addChangeListener(FolderStructure.getInstance().getGoogleApiClient(), new ChangeListener() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        Log.d(TAG, "event: " + event + " resId: " + event.getDriveId().getResourceId());
                        resourceId = event.getDriveId().getResourceId();
                    }
                });
                Drive.DriveApi.requestSync(FolderStructure.getInstance().getGoogleApiClient()).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                    @Override
                    public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                        if (status.isSuccess()) {
                            DriveId driveFolderId = DriveId.decodeFromString(driveResourceDto.getDriveId());
                            if (driveFolderId != null && driveFolderId.getResourceId() != null && !driveFolderId.equals("")) {
                                resourceId = driveFolderId.getResourceId();
                            }
                        }
                    }
                });
                do {
                    try {
                        Thread.sleep(2000);
                        if (resourceId != null) {
                            allowSharePermission();
                            return LINK_APPEND_RESOURCE_ID + resourceId;
                        }
                    } catch (InterruptedException e) {
                        Log.e(this.getClass().getName(), "Thread interrupted exception");
                    }

                } while(!changeListenerExecuted && !syncExecuted);
                return resourceId;
            }

            /**
             * It gives share permission to current folder uploaded or current driveId object set
             * in the driveId reference.
             */
            private void allowSharePermission() {
                if (!FolderStructure.getInstance().isNetworkAvailable(context)) {
                    return;
                }
                com.google.api.services.drive.Drive driveService = FolderStructure.getInstance().getRestApiDriveService();
                try {
                    Permission newPermission = new Permission();
                    newPermission.setType("anyone");
                    newPermission.setRole("reader");
                    Permission p = driveService.permissions().create(resourceId, newPermission).execute();
                } catch (UserRecoverableAuthIOException e) {
                    // context.startActivityForResult(e.getIntent(), "1001");
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                driveResourceDto.setLink(s);

                shareTextUrl(s);
                new DriveResourceRepo().updateDriveResource(driveResourceDto);
                Log.d(TAG,s);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            public TextView txtViewTitle;
            public LinearLayout linearLayoutThumbnail;
            public ImageView imageViewThumbnails, imageViewPlay, shareImageView;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.fname);
                linearLayoutThumbnail = (LinearLayout) itemLayoutView.findViewById(R.id.linearLayoutThumbnail);
                imageViewThumbnails = (ImageView) itemLayoutView.findViewById(R.id.imageViewThumbnails);
                imageViewPlay = (ImageView) itemLayoutView.findViewById(R.id.imageViewPlay);
                shareImageView = (ImageView) itemLayoutView.findViewById(R.id.shareImageView);


            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData == null ? 0 : itemsData.size();
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
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(fileUrl, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

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

        public FolderType getFolderType() {
            return folderType;
        }

        public void setFolderType(FolderType folderType) {
            this.folderType = folderType;
        }

        private FolderType folderType;

        public Date getLastModifyAt() {
            return lastModifyAt;
        }

        public void setLastModifyAt(Date lastModifyAt) {
            this.lastModifyAt = lastModifyAt;
        }

        private Date lastModifyAt;

        public String getFolderLocation() {
            return folderLocation;
        }

        public void setFolderLocation(String folderLocation) {
            this.folderLocation = folderLocation;
        }

        private String folderLocation;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        private String fileUrl;

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        private String folderName;

        public Data() {
        }

        public Data(String name, String fileUrl, String folderLocation,Date lastModifyAt,FolderType folderType) {
            this.name = name;
            this.fileUrl = fileUrl;
            this.folderLocation = folderLocation;
            this.lastModifyAt=lastModifyAt;
            this.folderType=folderType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    private class DataComparatoor implements Comparator<Data>{

        @Override
        public int compare(Data lhs, Data rhs) {
            if(lhs.getLastModifyAt().compareTo(rhs.getLastModifyAt())<1){

            }
            return  lhs.getLastModifyAt().compareTo(rhs.getLastModifyAt());

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

    private void shareTextUrl(String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Help me SOS");
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share link!"));
    }


}