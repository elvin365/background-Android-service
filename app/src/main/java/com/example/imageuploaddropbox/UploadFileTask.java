package com.example.imageuploaddropbox;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadFileTask extends AsyncTask<String , Void , FileMetadata> {
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;

    public interface Callback {
        void onUploadComplete(FileMetadata result);
        void onError(Exception e);
    }

    UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }
    @Override
    protected void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
       if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected FileMetadata doInBackground(String... strings) {
        String localUri = strings[0];
        File localFile = new File("/storage/emulated/0/MyFiles/Contacts.txt");

        Log.e("localFile",""+localFile);

        if (localFile != null) {

            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = localFile.getName();

            try  {


                InputStream inputStream = new FileInputStream(localFile);
                return mDbxClient.files().uploadBuilder(  "/" + remoteFileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (DbxException | IOException e) {
Log.e("Exception" , e.getLocalizedMessage());            }
        }

        return null;
    }

}
