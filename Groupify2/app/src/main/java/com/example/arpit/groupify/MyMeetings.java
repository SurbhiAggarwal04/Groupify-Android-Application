
package com.example.arpit.groupify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;


public class MyMeetings extends AppCompatActivity {
    TextView date;
    TextView time;
    TextView venue;
    TextView status;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String GRP_id = getIntent().getExtras().getString("Group_No_1");
        setContentView(R.layout.activity_my_meetings);
        //date=(TextView)findViewById(R.id.date);
        time=(TextView)findViewById(R.id.time);
        status=(TextView)findViewById(R.id.venue);
        venue=(TextView)findViewById(R.id.status);
        db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        TextView my_meetings = (TextView)findViewById(R.id.date);
        String my_meetings_single="";
        Cursor db_data=db.rawQuery("SELECT * FROM GROUP_MEETING_INFO where GROUPID="+GRP_id+";",null);
        db_data.moveToFirst();
        if(db_data.getCount()>0)
        {
            //date=(TextView)findViewById(R.id.date);
            //time=(TextView)findViewById(R.id.time);
            //status=(TextView)findViewById(R.id.venue);
            //venue=(TextView)findViewById(R.id.status);


            for (int i = 0; i <= db_data.getCount() - 1; i++) {


                my_meetings_single += "Meeting Number: " + Integer.toString(i+1) + "\n";
                my_meetings_single += "Date: " + db_data.getString(3) + "\n";
                my_meetings_single += "Time: " + db_data.getString(4) + "\n";
                my_meetings_single += "Venue: " + db_data.getString(2) + "\n";
                my_meetings_single += "Status: " + db_data.getString(5) + "\n\n";
                db_data.moveToNext();
                // userSerialNo = db_data.getString(0);
                // IDMatch = db_data.getString(2);
                //PWMatch = db_data.getString(3);
            }
            // time.setText(db_data.getString(4));
            //date.setText(db_data.getString(3));
            //venue.setText(db_data.getString(2));
            //status.setText(db_data.getString(5));


        }
        my_meetings.setText(my_meetings_single);
    }

}