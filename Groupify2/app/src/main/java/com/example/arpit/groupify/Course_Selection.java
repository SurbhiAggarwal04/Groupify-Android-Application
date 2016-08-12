package com.example.arpit.groupify;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mamtora on 24-04-2016.
 */
public class Course_Selection extends AppCompatActivity {

    SQLiteDatabase db_s;
    public static final String  DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final  String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection);

        Button Done = (Button)findViewById(R.id.bcourse_selected);
        db_s = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox MC =(CheckBox)findViewById(R.id.bMobileComputing);
                CheckBox SD =(CheckBox)findViewById(R.id.bSoftwareDesign);
                //new DownloadFileFromURL().execute(file_url);
                boolean m = MC.isChecked();
                boolean s = SD.isChecked();
                int m_x=0;
                int s_x=0;
                if (m)
                    m_x=1;
                if (s)
                    s_x=1;


                    try {
                        db_s.beginTransaction();
                        //perform your database operations here ...
                        db_s.execSQL("insert into COURSES(Course1, Course2) values ('" + m_x + "', '" + s_x + "');");
                        db_s.setTransactionSuccessful(); //commit your changes
                    } catch (SQLiteException e) {
                        //report problem
                    } finally {
                        db_s.endTransaction();
                        Toast.makeText(getApplicationContext(), "Courses Selected. Please log in", Toast.LENGTH_SHORT).show();
                        new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        Intent intent = new Intent(Course_Selection.this, MainActivity.class);
                        startActivity(intent);

                    }
            }
        });


    }
}
