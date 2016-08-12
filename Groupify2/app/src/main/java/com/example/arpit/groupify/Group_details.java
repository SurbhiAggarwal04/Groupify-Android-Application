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
import android.widget.Toast;

import com.google.gson.Gson;

public class Group_details extends AppCompatActivity {
    SQLiteDatabase db2;
    Button JoinGroup;
    public static final String DATABASE_NAME = "GROUPIFY_DB";
    final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "GROUPIFY_DB";
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
    final String file_url = "https://impact.asu.edu/Appenstance/GROUPIFY_DB";
    String grp_no;
    boolean found=false;
    String MID1,MID2,MID3,GRP_Count;
    private SharedPreferences settings;
    private int userSerialNo;
    private SharedPreferences.Editor editor;
    TextView GM3;
    TextView GM2;
    TextView GM1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = getApplicationContext().getSharedPreferences("my", 0);
        editor = settings.edit();
        String serialNo = settings.getString("userSerialNo", "");
        userSerialNo=Integer.parseInt(serialNo);

        //Gson gson;
        //private UserProfile userProfile;

        //gson = new Gson();
        //String json = gson.toJson(userProfile);
        //UserProfile user = gson.fromJson(json, UserProfile.class);
        db2 = SQLiteDatabase.openDatabase("/storage/emulated/0/GROUPIFY_DB", null, 0);
        String data = getIntent().getExtras().getString("Group_No");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        TextView Group_Number = (TextView) findViewById(R.id.text_Group_No);
        GM1 = (TextView) findViewById(R.id.text_gm1_name);
        GM1.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                              if(!GM1.getText().equals("Empty Slot"))
                              {
                                    Intent intent=new Intent(Group_details.this,ProfileDetails.class);
                                    intent.putExtra("methodName","displayGroupMembersInfo");
                                    intent.putExtra("GMId",MID1);
                                  startActivity(intent);
                              }
                                   }
                               });
        GM2 = (TextView) findViewById(R.id.text_gm2_name);
        GM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GM2.getText().equals("Empty Slot"))
                {
                    Intent intent=new Intent(Group_details.this,ProfileDetails.class);
                    intent.putExtra("methodName","displayGroupMembersInfo");
                    intent.putExtra("GMId",MID2);
                    startActivity(intent);
                }
            }
        });
        GM3 = (TextView) findViewById(R.id.text_gm3_name);
        GM3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GM3.getText().equals("Empty Slot"))
                {
                    Intent intent=new Intent(Group_details.this,ProfileDetails.class);
                    intent.putExtra("methodName","displayGroupMembersInfo");
                    intent.putExtra("GMId",MID3);
                    startActivity(intent);
                }
            }
        });
        JoinGroup = (Button) findViewById(R.id.bJoin);
        Group_Number.setText("GROUP NUMBER  " + data);
        Cursor db_data = getData();
        db_data.moveToFirst();
        int db_count = db_data.getCount();
        Toast.makeText(Group_details.this,"'"+ db_count+"'", Toast.LENGTH_SHORT).show();
        for(int i=0;i<db_count-1;i++){

            grp_no = db_data.getString(0);
            MID1 = db_data.getString(1);
            Toast.makeText(Group_details.this, MID1, Toast.LENGTH_SHORT).show();
            MID2 = db_data.getString(2);
            MID3 = db_data.getString(3);
            GRP_Count = db_data.getString(4);
            if(grp_no.equals(data)){
                found = true;
                break;
            }
            db_data.moveToNext();

        }
        if(found ==true) {
            if(userSerialNo==Integer.parseInt(MID1) || userSerialNo==Integer.parseInt(MID2) || userSerialNo==Integer.parseInt(MID3))
            {
                JoinGroup.setText("Exit Group");

            }
                if(!MID1.equals("0"))
            {
                Cursor db_data2 = getData2(MID1);
                GM1.setText(db_data2.getString(1));

            }
            if(!MID2.equals("0"))
            {
                Cursor db_data2 = getData2(MID2);
                GM2.setText(db_data2.getString(2));

            }
            if(!MID3.equals("0"))
            {
                Cursor db_data2 = getData2(MID3);
                GM3.setText(db_data2.getString(3));
            }
        }
            //Toast.makeText(getApplicationContext(), "Found Group. Please try logging in", Toast.LENGTH_SHORT).show();




        if (Integer.parseInt(GRP_Count)<3){

            JoinGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     String data = getIntent().getExtras().getString("Group_No");
                    int count=Integer.parseInt(GRP_Count);
                    if(userSerialNo==Integer.parseInt(MID1) || userSerialNo==Integer.parseInt(MID2) || userSerialNo==Integer.parseInt(MID3))
                    {
                        count--;
                        if (userSerialNo==Integer.parseInt(MID1)) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID1='0',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        } else if (userSerialNo==Integer.parseInt(MID2)) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID2='0',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        } else if (userSerialNo==Integer.parseInt(MID3)) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID3='0',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        }

                    }
                    else {
                        count++;
                        if (MID1.equals("0")) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID1='" + userSerialNo + "',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        } else if (MID2.equals("0")) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID2='" + userSerialNo + "',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        } else if (MID3.equals("0")) {
                            db2.beginTransaction();
                            db2.execSQL("update GROUP_INFO set Member_ID3='" + userSerialNo + "',Count='" + count + "' where Group_no=" + Integer.parseInt(data) + ";");
                            db2.setTransactionSuccessful();
                            db2.endTransaction();
                            new UploadFiletoURL().execute(uploadFilePath, uploadFileName, upLoadServerUri);
                        }
                    }
                    //    Intent intent = new Intent(Group_details.this, Group_details.class);
                    //startActivity(intent);
                }
            });
        }
    }
    public Cursor getData(){
        Cursor db_data = db2.rawQuery("SELECT * FROM GROUP_INFO", null);
        //Cursor db_data = db.rawQuery("SELECT * FROM USER_INFO where Email_ID = " + Email_ID_Text ,null);
        return db_data;


    }
    public Cursor getData2(String ID){

      //  String query="SELECT * FROM USER_INFO";
        Cursor db_data2 = db2.rawQuery("SELECT * FROM USER_INFO where User_Id='"+Integer.parseInt(ID)+"';",null);
        db_data2.moveToFirst();
        Toast.makeText(Group_details.this, "'"+db_data2.getCount()+"'", Toast.LENGTH_SHORT).show();
       //Cursor mycursor = myDatabase.rawQuery("SELECT " + COLUMN_CATEGORIES  + " FROM "+ StringCategory_Table +" WHERE " + _ID + " = " + catId, null);
        //Cursor db_data = db.rawQuery("SELECT * FROM USER_INFO where Email_ID = " + Email_ID_Text ,null);
        return db_data2;


    }

}
