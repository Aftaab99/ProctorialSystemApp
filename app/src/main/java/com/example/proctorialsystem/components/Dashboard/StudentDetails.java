package com.example.proctorialsystem.components.Dashboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proctorialsystem.R;

public class StudentDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        TextView usn_t = findViewById(R.id.student_usn);
        TextView name_t = findViewById(R.id.student_name);
        TextView dept_and_years_t = findViewById(R.id.student_dept_and_year);
        TextView email_t = findViewById(R.id.student_email);
        TextView phone_t = findViewById(R.id.student_phone);
        TextView quota_t = findViewById(R.id.student_quota);
        TextView semester_t = findViewById(R.id.student_sem);

        Bundle data = getIntent().getExtras();
        usn_t.setText(data.getString("usn"));
        name_t.setText(data.getString("name"));
        dept_and_years_t.setText(String.format("%s (%d-%d)", data.getString("dept"), data.getInt("join"), data.getInt("grad")));
        email_t.setText(data.getString("email"));
        phone_t.setText(""+data.getLong("phone"));
        quota_t.setText(data.getString("quota"));
        semester_t.setText(""+data.getInt("semester"));
    }
}
