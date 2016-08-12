package com.example.arpit.groupify;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.APIHelper;

import org.json.JSONObject;

/**
 * Created by surbhi on 3/29/2016.
 */
public class ApiActivity extends Activity {
    Gson gson;
    SQLiteDatabase db1;
    public static final String DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    private UserProfile userProfile;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private static final String host = "api.linkedin.com";
    //private static final String topCardUrl = "https://" + host + "/v1/people/~:(first-name,last-name,public-profile-url,skills,educations)";
    private static final String apiUrl = "https://" + host + "/v1/people/~:(summary,industry,public-profile-url)";
    int count=0;
    boolean runnable=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //final Button makeApiCall = (Button) findViewById(R.id.makeApiCall);

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(ApiActivity.this, apiUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse s) {
                try {
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                    JSONObject responseJSON = s.getResponseDataAsJson();

                    userProfile = new UserProfile();
                    settings = getApplicationContext().getSharedPreferences("my", 0);
                    editor = settings.edit();
                    String userEmail = settings.getString("userEmail", "");
                    int userSerialNo = Integer.parseInt(settings.getString("userSerialNo", ""));


                    userProfile.setEmail(userEmail);
                    userProfile.setSerialNo(userSerialNo);
                    userProfile.setIndustry(responseJSON.get("industry").toString());
                    userProfile.setSummary(responseJSON.get("summary").toString());
                    userProfile.setUrl(responseJSON.get("publicProfileUrl").toString());





                    ((TextView) findViewById(R.id.summary)).setText(userProfile.getSummary());
                    ((TextView) findViewById(R.id.currentIndustry)).setText(userProfile.getIndustry());
                    ((Button) findViewById(R.id.profileUrlbtn)).setText(userProfile.getUrl());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onApiError(LIApiError error) {
                // ((TextView) findViewById(R.id.response)).setText(error.toString());
            }
        });

        Button updateProfilebtn = (Button) findViewById(R.id.updateProfilebtn);
        updateProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "button clicked", Toast.LENGTH_LONG).show();
                userProfile.setSummary(((EditText) findViewById(R.id.summary)).getText().toString());
                userProfile.setIndustry(((EditText) findViewById(R.id.currentIndustry)).getText().toString());
                userProfile.setSkills(((EditText) findViewById(R.id.skills)).getText().toString());
                userProfile.setProjects(((EditText) findViewById(R.id.projects)).getText().toString());

                gson = new Gson();
                String json = gson.toJson(userProfile); // myObject - instance of MyObject
                editor.putString("userProfileObject", json);
                editor.commit();
                downloadBeforeProfileSave();


            }
        });
    }
    boolean downloadBeforeProfileSave()
    {
        Toast.makeText(getApplicationContext(), "download start", Toast.LENGTH_LONG).show();
        new DownloadFileFromURL().execute(file_url);
        runnable = true;
        saveProfileThread.start();
        return true;
    }
    public Thread saveProfileThread = new Thread() {
        @Override
        public void run() {
            while (runnable) {

                try {

                    Thread.sleep(7000);
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
           saveUserProfile();
        }
    };
    void saveUserProfile()
    {
        if(count==0) {
            Toast.makeText(getApplicationContext(), "save", Toast.LENGTH_LONG).show();
            count++;
            String json = settings.getString("userProfileObject", "");
            UserProfile user = gson.fromJson(json, UserProfile.class);
            db1 = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
            try {

                Cursor db_data = getData("USER_PROFILE_INFO");
                int db_count = db_data.getCount();
                boolean found = false;
                runnable = false;
               // String userSerialNo=null;
                String IDMatch=null;
               // String PWMatch;
                //db.close();
                for (int i = 0; i <= db_count - 1; i++) {
                db_data.moveToNext();

                IDMatch = db_data.getString(1);
                if (IDMatch.equals(user.getEmail())) {
                    Toast.makeText(getApplicationContext(), "found", Toast.LENGTH_SHORT).show();
                    found = true;
                    break;
                }
                //db_data.moveToNext();

            }


                db1.beginTransaction();
                if(found) {
                    Toast.makeText(getApplicationContext(), "sql", Toast.LENGTH_SHORT).show();
                    db1.execSQL("update USER_PROFILE_INFO set industry='"+user.getIndustry()+"',education='"+user.getSummary()+"',projects='"+user.getProjects()+"',skills='"+user.getSkills()+"' where serial_no="+user.getSerialNo()+";");

                    Toast.makeText(getApplicationContext(), "Already Present", Toast.LENGTH_SHORT).show();
                }
                    else
                //perform your database operations here ...
                db1.execSQL("insert into USER_PROFILE_INFO(serial_no, Email_ID, industry,education,skills,projects,url) values ('" + user.getSerialNo() + "', '" + user.getEmail() + "', '" + user.getIndustry() + "', '" + user.getSummary() + "', '" + user.getSkills() + "', '" + user.getProjects() + "','"+user.getUrl()+"');");
                db1.setTransactionSuccessful(); //commit your changes

                Intent intent=new Intent(ApiActivity.this,ProfileActivity.class);
                startActivity(intent);
            } catch (SQLiteException e) {
                //report problem
            } finally {
                db1.endTransaction();
                db1.close();
                runnable = false;
                new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                //Intent intent = new Intent(ApiActivity.this, Register.class);

            }
        }
    }

       public Cursor getData(String Table_Name){


           Cursor db_data = db1.rawQuery("SELECT * from "+ Table_Name,null);
           return db_data;


       }

}
