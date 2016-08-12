package com.example.arpit.groupify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class Register extends AppCompatActivity {
    SQLiteDatabase db;
    public static final String  DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final  String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    int insert;
    EditText First_Name;
    EditText Last_Name;
    EditText Contact_No;
    EditText Email_ID;
    EditText Pass_Word;
    String Email_ID_Text;
    String Pass_Word_Text;
    String First_Name_Text;
    String Last_Name_Text;
    String Contact_No_Text;
    String User_Name_Text;
    boolean runnable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button RegisterButton = (Button)findViewById(R.id.bRegister);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileFromURL().execute(file_url);
                First_Name = (EditText) findViewById(R.id.etFirstName);
                Last_Name = (EditText) findViewById(R.id.etLastName);
                Contact_No = (EditText) findViewById(R.id.etContact);
                Email_ID = (EditText) findViewById(R.id.etEmail);
                Pass_Word = (EditText) findViewById(R.id.etPassword);
                First_Name_Text = First_Name.getText().toString();
                Last_Name_Text = Last_Name.getText().toString();
                Contact_No_Text= Contact_No.getText().toString();
                Email_ID_Text = Email_ID.getText().toString();
                Pass_Word_Text = Pass_Word.getText().toString();
                User_Name_Text = First_Name_Text + " " + Last_Name_Text;
                Toast.makeText(getApplicationContext(), "File Download Complete.", Toast.LENGTH_SHORT).show();
                insert =0;

                runnable = true;
                startThread.start();
                //Cursor db_data = getData("USER_INFO");
                //Toast.makeText(MainActivity.this, Email_ID_Text + Pass_Word_Text, Toast.LENGTH_SHORT).show();
                  //Intent intent = new Intent(Register.this, MainActivity.class);
                Intent intent = new Intent(Register.this, Course_Selection.class);
                  startActivity(intent);
            }
        });

    }

    void Insert_into_USER_Info() {
        db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        Cursor db_data = getData();
        int db_count = db_data.getCount();
        boolean found=false;
        runnable = false;
        String IDMatch;
        for(int i=0;i<db_count-1;i++){
            db_data.moveToNext();
            IDMatch = db_data.getString(2);
            if(IDMatch.equals(Email_ID_Text)){
                found = true;
                break;
            }
            //db_data.moveToNext();

        }
        if(found ==true)
            Toast.makeText(getApplicationContext(), "Already Registered. Please try logging in", Toast.LENGTH_SHORT).show();
            else {
            // db.execSQL("select * from USER_INFO where Email_ID = " + User_Name_Text);

            insert = 1;
            // db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME, null);
            try {
                db.beginTransaction();
                //perform your database operations here ...
                db.execSQL("insert into USER_INFO(User_Name, Email_ID, Password, Contact_No) values ('" + User_Name_Text + "', '" + Email_ID_Text + "', '" + Pass_Word_Text + "', '" + Contact_No_Text + "');");
                db.setTransactionSuccessful(); //commit your changes
            } catch (SQLiteException e) {
                //report problem
            } finally {
                db.endTransaction();
               // Toast.makeText(getApplicationContext(), "Registered. Please log in", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(Register.this, MainActivity.class);
                //startActivity(intent);
                runnable = false;
                //new UploadFiletoURL().execute(uploadFilePath,uploadFileName,upLoadServerUri);
            }
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            if(insert==0)
                Insert_into_USER_Info();
            else {
                new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
                db.close();
            }
        }
    };

    public Thread startThread = new Thread(){
        @Override
        public void run() {
            while(runnable){

                try{

                    Thread.sleep(5000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    handler.sendEmptyMessage(1);
                }
            }
        }

    };
    public Cursor getData(){
        Cursor db_data = db.rawQuery("SELECT * FROM USER_INFO" ,null);
        //Cursor db_data = db.rawQuery("SELECT * FROM USER_INFO where Email_ID = " + Email_ID_Text ,null);
        return db_data;


    }
}
