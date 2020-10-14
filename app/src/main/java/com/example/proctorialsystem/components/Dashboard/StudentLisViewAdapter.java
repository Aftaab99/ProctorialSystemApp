package com.example.proctorialsystem.components.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.example.proctorialsystem.login.ProctorLoginActivity;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class StudentLisViewAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Student> students;
    private DashboardFragment fragmentRef;

    StudentLisViewAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Student> students, @NonNull DashboardFragment fragmentRef) {
        super(context, resource, textViewResourceId, students);
        this.mContext = context;
        this.students = students;
        this.fragmentRef = fragmentRef;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.student_list_item, parent, false);
        final Student stud = students.get(position);
        TextView student_name = convertView.findViewById(R.id.student_name);
        TextView student_usn = convertView.findViewById(R.id.student_usn_tag);
        TextView student_dept = convertView.findViewById(R.id.student_dept_and_year);
        ImageButton removeStudent = convertView.findViewById(R.id.removeBtn);
        student_name.setText(stud.getName());
        student_usn.setText(stud.getUSN());
        student_dept.setText(stud.getDept());

        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveStudent removeStudent = new RemoveStudent();
                String[] params = {stud.getUSN(), Integer.toString(position)};
                removeStudent.execute(params);
            }
        });


        return super.getView(position, convertView, parent);
    }


    public class RemoveStudent extends AsyncTask<String, Void, Void> {

        Boolean removeSuccessful, sessionInvalidated;
        int position;
        Student student;
        String token;

        @Override
        protected void onPreExecute() {
            removeSuccessful = false;
            sessionInvalidated = false;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            token = preferences.getString("JWT_TOKEN", "");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(sessionInvalidated){
                Toast.makeText(getContext(), "Session Invalidated, logging out.", Toast.LENGTH_SHORT).show();
                fragmentRef.gotoLoginActivity();
                return;
            }
            if (removeSuccessful) {
                if (student != null)
                    students.remove(student);

                StudentLisViewAdapter.this.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            String usn = strings[0];
            position = Integer.parseInt(strings[1]);
            student = students.get(position);
            try {
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/remove_student_proctor?student_usn=%s", usn));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", token);
                connection.connect();
                if(connection.getResponseCode()==403)
                {
                    sessionInvalidated = true;
                }
                String result = Utility.fetchResponseHttps(connection);
                JSONObject res = new JSONObject(result);
                if (!res.getBoolean("error"))
                    removeSuccessful = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
