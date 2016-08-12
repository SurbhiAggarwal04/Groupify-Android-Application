package com.example.arpit.groupify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    public static final String DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";

    EditText Email_ID;
    EditText Pass_Word;
    String Email_ID_Text;
    String Pass_Word_Text;
    boolean runnable = false;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //requestPermissions(permissions, WRITE_REQUEST_CODE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 300);
        }
        db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME, null);

        Button LoginButton = (Button) findViewById(R.id.bLogin);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email_ID = (EditText) findViewById(R.id.etUsername);
                Pass_Word = (EditText) findViewById(R.id.etPassword);
                Email_ID_Text = Email_ID.getText().toString();
                Pass_Word_Text = Pass_Word.getText().toString();
                new DownloadFileFromURL().execute(file_url);
                Toast.makeText(getApplicationContext(), "File Download Complete.", Toast.LENGTH_SHORT).show();
                // Cursor db_data = getData("USER_INFO");


                runnable = true;
                LoginThread.start();
                //Toast.makeText(MainActivity.this, Email_ID_Text + Pass_Word_Text, Toast.LENGTH_SHORT).show();
                //   Intent intent = new Intent(MainActivity.this, Register.class);
                // startActivity(intent);
            }
        });

        Button signUpButton = (Button) findViewById(R.id.bSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                //Intent intent = new Intent(MainActivity.this, View_all_groups.class);
                startActivity(intent);
            }
        });
    }

    void Verify_Login() {
        if(count==0) {
            count++;

            db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
            Cursor db_data = getData("USER_INFO");
            int db_count = db_data.getCount();
            boolean found = false;
            runnable = false;
            String userSerialNo=null;
            String IDMatch=null;
            String PWMatch;

            for (int i = 0; i <= db_count - 1; i++) {
                db_data.moveToNext();

                userSerialNo=db_data.getString(0);
                IDMatch = db_data.getString(2);
                PWMatch = db_data.getString(3);

                if (IDMatch.equals(Email_ID_Text) && PWMatch.equals(Pass_Word_Text)) {
                    found = true;
                    Context context=getApplicationContext();
                    SharedPreferences settings = context.getSharedPreferences("my",0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("userEmail", IDMatch);
                    editor.putString("userSerialNo", userSerialNo);
                    editor.commit();
                    db_data=getData("USER_PROFILE_INFO");
                    db_count=db_data.getCount();
                    int j=0;
                    for(j=0;j<db_count;j++)
                    {
                        db_data.moveToNext();

                        if(userSerialNo.equals(db_data.getString(0)))
                        {

                            UserProfile userProfile=new UserProfile();
                            userProfile.setSerialNo(Integer.parseInt(userSerialNo));
                            userProfile.setProjects(db_data.getString(5));
                            userProfile.setEmail(db_data.getString(1));
                            userProfile.setIndustry(db_data.getString(2));
                            userProfile.setSummary(db_data.getString(3));
                            userProfile.setSkills(db_data.getString(4));
                            userProfile.setUrl(db_data.getString(6));
                            Gson gson = new Gson();
                            String json = gson.toJson(userProfile); // myObject - instance of MyObject
                            editor.putString("userProfileObject", json);
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            break;


                        }
                    }
                    if(j>=db_count)
                    {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);

                    }
                    break;
                }
                //db.close();
                //db_data.moveToNext();

            }

        }

    }

    public Thread LoginThread = new Thread() {
        @Override
        public void run() {
            while (runnable) {

                try {

                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.sendEmptyMessage(1);
                }
            }
        }

    };
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //if(insert==0)
            Verify_Login();
            //else {
            //new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
            //Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
        }
    };

    public Cursor getData(String Table_Name){


        Cursor db_data = db.rawQuery("SELECT * from "+ Table_Name,null);
        return db_data;


    }
}
