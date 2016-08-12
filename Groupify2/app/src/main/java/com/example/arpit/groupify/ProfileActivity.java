package com.example.arpit.groupify;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


/**
 * Created by Mamtora on 07-04-2016.
 */
public class ProfileActivity extends AppCompatActivity {
    GPSTracker gps;
    GetCoordinates gc;
    FindDistance fd;
    Cursor db_data;
    TextView date;
    TextView time;
    TextView venue;
    TextView status;
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    Button viewProfile;
    Button viewGroups;
    Button profile_linkedin_btn;
    Button createMeetingbtn;
    Button myMeetingbtn;
    SQLiteDatabase db;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    String groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);
        settings = getApplicationContext().getSharedPreferences("my", 0);
        editor = settings.edit();

        int userSerialNo = Integer.parseInt(settings.getString("userSerialNo", ""));

        db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);

        Cursor db_data2 = db.rawQuery("SELECT * FROM GROUP_INFO where Member_ID1="+userSerialNo+" OR Member_ID2="+userSerialNo+" OR Member_ID3="+userSerialNo+";",null);
        db_data2.moveToFirst();

        createMeetingbtn=(Button) findViewById(R.id.createMeetingbtn);
        myMeetingbtn=(Button) findViewById(R.id.myMeetingbtn);
        myMeetingbtn.setEnabled(false);
        createMeetingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, MeetingActivity.class);
                startActivity(intent);
            }
        });
        myMeetingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, MyMeetings.class);
                intent.putExtra("Group_No_1",groupId);
                startActivity(intent);
            }
        });
        if(db_data2.getCount()>0)
        {
            Toast.makeText(getApplicationContext(), db_data2.getString(0), Toast.LENGTH_LONG).show();
            groupId=db_data2.getString(0);
            if(groupId.contains("'"))
            {
                groupId=db_data2.getString(0).replace("'","");
            }
            editor.putString("userGroupId", groupId);
            editor.commit();
            createMeetingbtn.setEnabled(true);
            db_data=db.rawQuery("SELECT * FROM GROUP_MEETING_INFO where GROUPID="+groupId+" and status='OPEN';",null);
            db_data.moveToFirst();
            if(db_data.getCount()>0)
            {
                myMeetingbtn.setEnabled(true);
                if(db_data.getString(5).equals("OPEN")) {
                    String venue = db_data.getString(2);
                    String date = db_data.getString(3);
                    String time = db_data.getString(4);
                    Time today=new Time(Time.getCurrentTimezone());
                    today.setToNow();
                    int day=today.monthDay;
                    int month=today.month;
                    month++;
                    int year=today.year;
                    String todayDate=month+"/"+day+"/"+year;
                    if(String.valueOf(day).length()==1 || String.valueOf(month).length()==1 )
                    {
                        if(String.valueOf(day).length()==1 && String.valueOf(month).length()==1)
                        {
                            todayDate="0"+month+"/0"+day+"/"+year;
                        }
                        else if(String.valueOf(day).length()==1)
                        {
                            todayDate=month+"/0"+day+"/"+year;
                        }
                        else
                        {
                            todayDate="0"+month+"/0"+day+"/"+year;
                        }


                    }
                  //  Toast.makeText(getApplicationContext(),"today date:"+todayDate ,Toast.LENGTH_LONG).show();
                   // Toast.makeText(getApplicationContext(),"meeting date:"+date ,Toast.LENGTH_LONG).show();
                    //venue = "1355 South Alma School Road #105, Mesa, AZ 85210, USA";
                    if(date.equals(todayDate))
                    {
                      //  Toast.makeText(getApplicationContext(),"inside gps" ,Toast.LENGTH_LONG).show();

                        gps = new GPSTracker(getApplicationContext());
                        gc = new GetCoordinates();
                        fd = new FindDistance();
                        final List dest_add = gc.convertAddress(venue,getApplicationContext());

                        if(gps.canGetLocation()) {

                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();
                            double lati_dest = (double) dest_add.get(0);
                            double longi_dest = (double) dest_add.get(1);
                            long c = Calendar.getInstance().getTimeInMillis();
                            double distance = fd.findDistance(longitude, latitude, longi_dest, lati_dest);
                            Toast.makeText(getApplicationContext(),"You have a meeting at:"+time+" and the distance to reach the venue from current location is:"+distance ,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            gps.showSettingsAlert();
                        }
                    }

                    final Dialog dialog = new Dialog(ProfileActivity.this);
                    dialog.setContentView(R.layout.activity_custom_dialog);
                    dialog.setTitle("Meeting fixed on:");
                    TextView noificationDate = (TextView) dialog.findViewById(R.id.date);
                    TextView noificationTime = (TextView) dialog.findViewById(R.id.time);
                    TextView noificationVenue = (TextView) dialog.findViewById(R.id.venue);

                    noificationTime.setText("Time: " + time);
                    noificationDate.setText("Date: " + date);
                    noificationVenue.setText("Venue: " + venue);
                    Button btnSave = (Button) dialog.findViewById(R.id.accept);
                    Button btnCancel = (Button) dialog.findViewById(R.id.reject);
                    dialog.show();
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Rejected", Toast.LENGTH_LONG).show();
                            db.beginTransaction();
                            db.execSQL("update GROUP_MEETING_INFO set status='REJECT' where GROUPID=" + Integer.parseInt(groupId) + ";");
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                            dialog.dismiss();
                        }
                    });
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                        }
                    });

                }
            }
            else
            {
                myMeetingbtn.setEnabled(false);
            }

            /*if(db_data.getCount()>0)
            {
                myMeetingbtn.setEnabled(true);
                myMeetingbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //Intent intent=new Intent(ProfileActivity.this,MyMeetings.class);
                       // startActivity(intent);
                        setContentView(R.layout.activity_my_meetings);
                        date=(TextView)findViewById(R.id.date);
                        time=(TextView)findViewById(R.id.time);
                        status=(TextView)findViewById(R.id.venue);
                        venue=(TextView)findViewById(R.id.status);
                        time.setText(db_data.getString(4));
                        date.setText(db_data.getString(3));
                        venue.setText(db_data.getString(2));
                        status.setText(db_data.getString(5));
                    }
                });
            }*/
        }
        else
        {
            createMeetingbtn.setEnabled(false);
        }


        viewProfile = (Button) findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, ProfileDetails.class);
                startActivity(intent);
            }
        });
        viewGroups = (Button) findViewById(R.id.viewGroups);
        viewGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, View_all_groups.class);
                startActivity(intent);
            }
        });
        profile_linkedin_btn = (Button) findViewById(R.id.extractProfileLinkedInbtn);
        profile_linkedin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, LinkedInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume()
    {
      super.onResume();
        setContentView(R.layout.activity_afterlogin);
        settings = getApplicationContext().getSharedPreferences("my", 0);
        editor = settings.edit();

        int userSerialNo = Integer.parseInt(settings.getString("userSerialNo", ""));

        db = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        Cursor db_data2 = db.rawQuery("SELECT * FROM GROUP_INFO where Member_ID1="+userSerialNo+" OR Member_ID2="+userSerialNo+" OR Member_ID3="+userSerialNo+";",null);
        db_data2.moveToFirst();
        createMeetingbtn=(Button) findViewById(R.id.createMeetingbtn);
        createMeetingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, MeetingActivity.class);
                //Intent intent = new Intent(ProfileActivity.this, MyMeetings.class);
                startActivity(intent);
            }
        });

        myMeetingbtn=(Button) findViewById(R.id.myMeetingbtn);
        myMeetingbtn.setEnabled(false);
        myMeetingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, MyMeetings.class);
                intent.putExtra("Group_No_1",groupId);
                startActivity(intent);
            }
        });
        if(db_data2.getCount()>0)
        {
            db_data=db.rawQuery("SELECT * FROM GROUP_MEETING_INFO where GROUPID="+groupId+";",null);
            db_data.moveToFirst();
            if(db_data.getCount()>0) {
                myMeetingbtn.setEnabled(true);
            }
            else
            {
                myMeetingbtn.setEnabled(false);
            }
            editor.putString("userGroupId",db_data2.getString(0));
            createMeetingbtn.setEnabled(true);
        }
        else
        {
            createMeetingbtn.setEnabled(false);
        }


        viewProfile = (Button) findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, ProfileDetails.class);
                startActivity(intent);
            }
        });
        viewGroups = (Button) findViewById(R.id.viewGroups);
        viewGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, View_all_groups.class);
                startActivity(intent);
            }
        });
        profile_linkedin_btn = (Button) findViewById(R.id.extractProfileLinkedInbtn);
        profile_linkedin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, LinkedInActivity.class);
                startActivity(intent);
            }
        });
    }
}
