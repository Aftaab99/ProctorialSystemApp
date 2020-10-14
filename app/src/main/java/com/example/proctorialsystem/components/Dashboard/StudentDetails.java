package com.example.proctorialsystem.components.Dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;

import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class StudentDetails extends AppCompatActivity {
    TextView usn_t, name_t, dept_and_years_t, email_t, phone_t, quota_t, semester_t;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        String usn = getIntent().getExtras().getString("usn");
        usn_t = findViewById(R.id.student_usn);
        name_t = findViewById(R.id.student_name);
        dept_and_years_t = findViewById(R.id.student_dept_and_year);
        email_t = findViewById(R.id.student_email);
        phone_t = findViewById(R.id.student_phone);
        quota_t = findViewById(R.id.student_quota);
        semester_t = findViewById(R.id.student_sem);
        progressBar = findViewById(R.id.progressBar);
        FetchStudentDetails fetch = new FetchStudentDetails();
        fetch.execute(usn);

    }

    public class FetchStudentDetails extends AsyncTask<String, Void, Void> {

        String usn, name, dept, quota, email;
        Integer join, grad, semester;
        Long phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            usn_t.setText(usn);
            name_t.setText(name);
            dept_and_years_t.setText(String.format(Locale.getDefault(), "%s(%d-%d)", dept, join, grad));
            quota_t.setText(quota);
            email_t.setText(email);
            semester_t.setText(semester);
            phone_t.setText(String.format(Locale.getDefault(), "%d", phone));
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String usn1 = strings[0];
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/get_student_details?student_usn=%s", usn1));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();

                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                if (!result.getBoolean("error")) {
                    name = result.getString("name");
                    usn = result.getString("usn");
                    dept = result.getString("dept_id");
                    quota = result.getString("quota");
                    email = result.getString("email");
                    join = result.getInt("joining_year");
                    grad = result.getInt("graduation_year");
                    email = result.getString("email");
                    phone = result.getLong("phone");
                    int year = Calendar.getInstance().get(Calendar.YEAR) - join+1;
                    if (Calendar.getInstance().get(Calendar.MONTH) <= 6) {
                        semester = year * 2 - 1;
                    } else
                        semester = year * 2;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
