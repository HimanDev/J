package com.example.himan.videotest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by DPandey on 11-10-2016.
 */

public class GoogleSignInActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static final int REQUEST_AUTHORIZATION = 1001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    private GoogleApiClient mGoogleApiClient;
    GoogleAccountCredential mCredential = null;
    /*private TextView mStatusTextView;
    private TextView mDetailTextView;*/
    private boolean initialised = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences(getString(R.string.PREFERENCE_FILE_KEY_GOOGLE), Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
       // findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        initSignInAndGoogleDrive();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
/*                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(GoogleSignInActivity.this, MainApp.class);
                                startActivity(intent);
                            }
                    });*/

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSignInView();
                        }
                    });
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Initialise sign in and google drive
     *
     */
    private void initSignInAndGoogleDrive() {
        if(mAuth.getCurrentUser() == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestScopes(Plus.SCOPE_PLUS_PROFILE, Drive.SCOPE_FILE)
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(Drive.API).addApi(Plus.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        else{
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addApi(Plus.API)
                    .addScope(Drive.SCOPE_FILE).addScope(Plus.SCOPE_PLUS_PROFILE).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if(sharedPreferences==null){
            sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
            editor=sharedPreferences.edit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Inte`nt from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showSignInView();
                Toast.makeText(GoogleSignInActivity.this, "Please Sign In with google to proceed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(!mGoogleApiClient.isConnected()) {
                                mGoogleApiClient = new GoogleApiClient.Builder(GoogleSignInActivity.this)
                                        .addApi(Drive.API).addApi(Plus.API)
                                        .addScope(Drive.SCOPE_FILE).addScope(Plus.SCOPE_PLUS_PROFILE).addConnectionCallbacks(GoogleSignInActivity.this)
                                        .addOnConnectionFailedListener(GoogleSignInActivity.this)
                                        .build();
                                mGoogleApiClient.connect();
                            }
/*                            Intent intent = new Intent(GoogleSignInActivity.this, MainApp.class);
                            startActivity(intent);*/
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  showSignInView();
                                  Toast.makeText(GoogleSignInActivity.this, "You have successfully Sign Out",
                                          Toast.LENGTH_SHORT).show();
                              }
                        }
                    );
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if(mAuth.getCurrentUser() == null) {
            showSignInView();
        }
    }

    private void showSignInView() {
        setContentView(R.layout.activity_google);
/*        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);*/
        // Button listeners
        SignInButton signInButton= (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setOnClickListener(this);
        Toast.makeText(this, "Please Sign in again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } /*else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.disconnect_button) {
            revokeAccess();
        }*/
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading.....");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onConnected(@Nullable  Bundle bundle) {
        if(mAuth.getCurrentUser() == null){
            return;
        }

        Log.i("Connected","Connect");
        if(initialised){
            Intent intent = new Intent(GoogleSignInActivity.this, MainApp.class);
            startActivity(intent);
            this.finish();
            return;
        }
        Log.i(TAG, "API client connected.");

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(DriveScopes.DRIVE_METADATA, DriveScopes.DRIVE))
                .setBackOff(new ExponentialBackOff());
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        if (mCredential.getSelectedAccountName() == null) {
            mCredential.setSelectedAccountName(email);
        }
        createGDriveFolder();
        com.google.api.services.drive.Drive driveService = new com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), mCredential)
                .setApplicationName("SafeApp")
                .build();

        FolderStructure.getInstance().setGoogleApiClient(mGoogleApiClient);
        FolderStructure.getInstance().setGoogleAccountCredential(mCredential);
        FolderStructure.getInstance().setRestApiDriveService(driveService);
        new DriveRestApiPermission(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        initialised = true;
        Intent intent = new Intent(GoogleSignInActivity.this, MainApp.class);
        hideProgressDialog();
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void createGDriveFolder() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating initial Folders on google drive.");
        String driveIdKey = sharedPreferences.getString(getString(R.string.App_Folder_Drive_Id),null);
        try {
            if(driveIdKey != null) {
                DriveId driveId = DriveId.decodeFromString(driveIdKey);
                if (driveId != null) {
                    createGDriveSubFolder(driveId, getString(R.string.Videos_Folder_Name), getString(R.string.Video_Folder_Drive_Id), true);
                    createGDriveSubFolder(driveId, getString(R.string.Audios_Folder_Name), getString(R.string.Audio_Folder_Drive_Id), true);
                    return;
                }
            }
        }


        catch(IllegalArgumentException | NullPointerException e){
            Log.e(TAG, "Initial Folders on google drive does not exist/deleted.",e);
        }

        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle(getString(R.string.App_Folder_Name)).build();

        PendingResult result = Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient,
                metadataChangeSet);
        result.setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                DriveId driveId = driveFolderResult.getDriveFolder().getDriveId();
                editor.putString(getString(R.string.App_Folder_Drive_Id),driveId.encodeToString());
                editor.commit();
                createGDriveSubFolder(driveId, getString(R.string.Videos_Folder_Name), getString(R.string.Video_Folder_Drive_Id), false);
                createGDriveSubFolder(driveId, getString(R.string.Audios_Folder_Name), getString(R.string.Audio_Folder_Drive_Id), false);
            }
        });

    }

    private void createGDriveSubFolder(DriveId parentDriveId, String title, final String  rDriveIdKey, boolean canExist) {
        if(canExist){
            String driveIdKey = sharedPreferences.getString(rDriveIdKey,null);
            try {
                DriveId driveId = DriveId.decodeFromString(driveIdKey);
                if(driveId != null){
                    return;
                }
            }
            catch(IllegalArgumentException e){
                Log.e(TAG, "Initial"+title+" Folder on google drive does not exist/deleted.");
            }
            return;
        }
        Log.i(TAG, "Creating initial Sub Folders on google drive.");
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle(title).build();
        PendingResult result = parentDriveId.asDriveFolder().createFolder(mGoogleApiClient,
                metadataChangeSet);
        result.setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                DriveId driveId = driveFolderResult.getDriveFolder().getDriveId();
                editor.putString(rDriveIdKey, driveId.encodeToString());
                editor.commit();
            }
        });
    }

    /**
     * This class is used to just ask for google drive permission.
     *
     */
    private class DriveRestApiPermission extends AsyncTask<Void, Void, Void> {

        private Activity activity = null;

        DriveRestApiPermission(Activity activity){
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                com.google.api.services.drive.Drive driveService = FolderStructure.getInstance().getRestApiDriveService();
                com.google.api.services.drive.model.FileList fileList = driveService.files().list().execute();
                fileList.toString();
            }
            catch(UserRecoverableAuthIOException e){
                activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }
            catch(IOException e){
                Log.i(TAG,e.getMessage());
            }
            return null;
        }
    }

}