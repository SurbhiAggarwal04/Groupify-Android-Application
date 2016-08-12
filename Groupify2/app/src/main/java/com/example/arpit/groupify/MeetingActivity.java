package com.example.arpit.groupify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Mamtora on 23-04-2016.
 */
public class MeetingActivity extends AppCompatActivity {
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    EditText date;
    EditText time;
    EditText venue;
    Button submit;
    String dateText;
    String timeText;
    String venueText;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_afterlogin);
        setContentView(R.layout.activity_createmeeting);
        date=(EditText) findViewById(R.id.date);
        time=(EditText) findViewById(R.id.time);
        venue=(EditText) findViewById(R.id.venue);
        submit = (Button) findViewById(R.id.submitMeetingbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = getApplicationContext().getSharedPreferences("my", 0);
                editor = settings.edit();

                int userSerialNo = Integer.parseInt(settings.getString("userSerialNo", ""));
                Toast.makeText(getApplicationContext(),"group id:"+ settings.getString("userGroupId",""), Toast.LENGTH_LONG).show();
               int userGroupId = Integer.parseInt(settings.getString("userGroupId", ""));
                dateText=date.getText().toString();
                timeText=time.getText().toString();
                venueText=venue.getText().toString();
                db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
                db.beginTransaction();
                db.execSQL("INSERT INTO GROUP_MEETING_INFO (GROUPID,Venue,Date,Time,status) values (" + userGroupId + ",'" + venueText + "','" + dateText + "','" + timeText + "','OPEN');");
                db.setTransactionSuccessful();
                db.endTransaction();
                new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                Intent intent=new Intent(MeetingActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });




    }
}
