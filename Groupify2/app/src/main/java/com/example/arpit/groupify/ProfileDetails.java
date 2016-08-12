package com.example.arpit.groupify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mamtora on 18-04-2016.
 */
public class ProfileDetails extends AppCompatActivity {
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetails);
        String methodName = null;
        try {
            methodName = getIntent().getExtras().getString("methodName");
            if (methodName != null && methodName.equals("displayGroupMembersInfo")) {
                displayGroupMembersInfo();
            } else {
                displayUserProfileInfo();
            }
        } catch (Exception e) {
            displayUserProfileInfo();
        }
    }
    public void displayGroupMembersInfo()
    {
        SQLiteDatabase db= SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        Cursor db_data=db.rawQuery("SELECT * from USER_PROFILE_INFO WHERE Serial_no='" + getIntent().getExtras().getString("GMId")+"';", null);
        db_data.moveToFirst();


        Button linkedInurl=(Button)findViewById(R.id.linkedinUrl);
        linkedInurl.setText("LinkedIn Url: " + db_data.getString(6));
        TextView summary = (TextView) findViewById(R.id.summary);
        summary.setText("Summary:"+db_data.getString(3));
        TextView industry = (TextView) findViewById(R.id.currentIndustry);
        industry.setText("Industry: "+db_data.getString(2));
        TextView skills = (TextView) findViewById(R.id.skills);
        skills.setText("Skills: "+db_data.getString(4));
        TextView projects = (TextView) findViewById(R.id.projects);
        projects.setText("Projects: "+db_data.getString(5));
    }
    public  void displayUserProfileInfo()
    {
            settings = getApplicationContext().getSharedPreferences("my", 0);
            editor = settings.edit();
            String profileDetails=settings.getString("userProfileObject","");
            JSONObject JSON=null;
            try {
                JSON=new JSONObject(profileDetails);
                Button linkedInurl=(Button)findViewById(R.id.linkedinUrl);
                linkedInurl.setText("LinkedIn Url: "+JSON.getString("url").toString());
                TextView summary = (TextView) findViewById(R.id.summary);
                summary.setText("Summary:"+JSON.getString("summary").toString());
                TextView industry = (TextView) findViewById(R.id.currentIndustry);
                industry.setText("Industry: "+JSON.getString("industry").toString());
                TextView skills = (TextView) findViewById(R.id.skills);
                skills.setText("Skills: "+JSON.getString("skills").toString());
                TextView projects = (TextView) findViewById(R.id.projects);
                projects.setText("Projects: "+JSON.getString("projects").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }





    }
}
