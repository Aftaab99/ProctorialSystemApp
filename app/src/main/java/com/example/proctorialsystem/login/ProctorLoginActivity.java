package com.example.proctorialsystem.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proctorialsystem.ProctorMainActivity;
import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class ProctorLoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AutoLoginPreferences.allowLogin(this)){
            String email = AutoLoginPreferences.getUserName(this);
            String name = AutoLoginPreferences.getName(this);
            Intent gotoMain = new Intent(ProctorLoginActivity.this, ProctorMainActivity.class);
            gotoMain.putExtra("proctor_id", email);
            gotoMain.putExtra("name", name);
            gotoMain.putExtra("type", "proctor");

            startActivity(gotoMain);

        }
        setContentView(R.layout.activity_proctor_login);
        final TextInputEditText proctor_id_et = findViewById(R.id.proctor_id);
        final TextInputEditText password_et = findViewById(R.id.proctor_password);
        Button login_btn = findViewById(R.id.login_btn);




        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("\nClicked!!!!!!\n");
                Log.d("PA", "\nYO\n");

                String proctor_id = proctor_id_et.getText().toString();
                String password =password_et.getText().toString();
                if(Utility.isValidEmail(proctor_id) && password.length()>=6){
                    CheckProctorCredentials check = new CheckProctorCredentials();
                    String[] args = {proctor_id, password};
                    check.execute(args);
                }
                else{
                    System.out.println("YO");
                    if(!Utility.isValidEmail(proctor_id))
                        proctor_id_et.setError("Email not valid");
                    else
                        password_et.setError("Password too short");
                }

            }
        });

    }

    public class CheckProctorCredentials extends AsyncTask<String, Void, Void>{

        @NonNull
        Boolean validProctor;
        String name;
        String email;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            validProctor=false;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(validProctor){
                Intent gotoMain = new Intent(ProctorLoginActivity.this, ProctorMainActivity.class);
                gotoMain.putExtra("proctor_id", email);
                gotoMain.putExtra("name", name);
                gotoMain.putExtra("type", "proctor");

                startActivity(gotoMain);
            }
            else{
                Toast.makeText(ProctorLoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            String proctor_id = strings[0];
            String password = strings[1];
            password = Utility.md5(password);
            System.out.println(password);
            try {
                JSONObject reqObj = new JSONObject();
                reqObj.put("proctor_id", proctor_id);
                reqObj.put("password", password);
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/check_proctor_cred?proctor_id=%s&password=%s", proctor_id, password));
                HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();

                String response = Utility.fetchResponseHttps(conn);
                System.out.println(response);
                JSONObject res = new JSONObject(response);
                if(!res.getBoolean("error")){
                    validProctor=true;
                    name=res.getString("username");
                    email=proctor_id;
                    AutoLoginPreferences.setUserName(ProctorLoginActivity.this, email);
                    AutoLoginPreferences.setName(ProctorLoginActivity.this, name);

                }

            }catch(Exception e){e.printStackTrace();}


            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
