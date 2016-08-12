package com.example.arpit.groupify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class View_all_groups extends AppCompatActivity {
    Button G1,G2,G3,G4,G5,G6,G7,G8,foundButton;
    int groupNo;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public static final String DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    //boolean runnable = false;
    //int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        new DownloadFileFromURL().execute(file_url);//
        Toast.makeText(getApplicationContext(), "File Download Complete.", Toast.LENGTH_SHORT).show();
        // Cursor db_data = getData("USER_INFO");
       SQLiteDatabase db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        settings = getApplicationContext().getSharedPreferences("my", 0);
        editor = settings.edit();
        int userSerialNo = Integer.parseInt(settings.getString("userSerialNo", ""));
        Cursor db_data=db.rawQuery("SELECT * from GROUP_INFO where Member_ID1="+userSerialNo+" OR Member_ID2="+userSerialNo+" OR Member_ID3="+userSerialNo+";",null);
        db_data.moveToFirst();
        G1 = (Button) findViewById(R.id.g1);
        G2 = (Button) findViewById(R.id.g2);
        G3 = (Button) findViewById(R.id.g3);
        G4 = (Button) findViewById(R.id.g4);
        G5 = (Button) findViewById(R.id.g5);
        G6 = (Button) findViewById(R.id.g6);
        G7 = (Button) findViewById(R.id.g7);
        G8 = (Button) findViewById(R.id.g8);
        if(db_data.getCount()>0)
        {
            groupNo=Integer.parseInt(db_data.getString(0));
        }
        if(groupNo==1)
        {
            G1.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==2)
        {
            G2.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==3)
        {
            G3.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==4)
        {
            G4.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==5)
        {
            G5.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==6)
        {
            G6.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==7)
        {
            G7.setBackgroundColor(Color.BLUE);
        }
        if(groupNo==8)
        {
            G8.setBackgroundColor(Color.BLUE);
        }
       // runnable = true;
        //LoginThread.start();

        G1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","1");
                startActivity(intent);
            }
        });

        G2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","2");
                startActivity(intent);
            }
        });

        G3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","3");
                startActivity(intent);
            }
        });
        G4 = (Button) findViewById(R.id.g4);
        G4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","4");
                startActivity(intent);
            }
        });
        G5 = (Button) findViewById(R.id.g5);
        G5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","5");
                startActivity(intent);
            }
        });
        G6 = (Button) findViewById(R.id.g6);
        G6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","6");
                startActivity(intent);
            }
        });
        G7 = (Button) findViewById(R.id.g7);
        G7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","7");
                startActivity(intent);
            }
        });
        G8 = (Button) findViewById(R.id.g8);
        G8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(View_all_groups.this, Group_details.class);

                intent.putExtra("Group_No","8");
                startActivity(intent);
            }
        });
    }

}
