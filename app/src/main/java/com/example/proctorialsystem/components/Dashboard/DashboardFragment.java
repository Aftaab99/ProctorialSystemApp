package com.example.proctorialsystem.components.Dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.example.proctorialsystem.login.AutoLoginPreferences;
import com.example.proctorialsystem.login.ProctorLoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DashboardFragment extends Fragment {

    String proctor_id, proctor_name;
    public static List<Student> students = new ArrayList<>();
    StudentLisViewAdapter adapter;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("Valid=" + Utility.isValidUSN("1DS17IS100"));
        proctor_id = getArguments().getString("proctor_id");
        proctor_name = getArguments().getString("name");
        Button logout_btn = view.findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoLoginPreferences.clearUserName(getActivity());
                Intent intent = new Intent(getActivity(), ProctorLoginActivity.class);
                startActivity(intent);
            }
        });
        progressBar=view.findViewById(R.id.progressBar);
        ListView studentList = view.findViewById(R.id.student_list);
        adapter = new StudentLisViewAdapter(getActivity()
                , R.id.student_list, R.id.row_id_tv, students);
        studentList.setAdapter(adapter);
        System.out.println("Proc_id" + proctor_id);
        TextView welcomeET = view.findViewById(R.id.welcome_text);
        welcomeET.setText(String.format("Welcome, %s", proctor_name));
        final TextInputEditText addUSNET = view.findViewById(R.id.student_usn_tag);
        final Button addStudent = view.findViewById(R.id.add_student);
        FetchStudents fetchStudents = new FetchStudents();
        String[] params = {proctor_id};
        fetchStudents.execute(params);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usn = addUSNET.getText().toString();
                System.out.println("USN=" + usn);
                if (Utility.isValidUSN(usn)) {
                    AddStudentToGroup addStudentToGroup = new AddStudentToGroup();
                    String[] params = {usn, proctor_id};
                    addStudentToGroup.execute(params);
                } else {
                    addUSNET.setError("Invalid USN");
                }
            }
        });

        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String usn = students.get(position).getUSN();
                System.out.println("Clicked="+usn);
                ShowStudentDetails showStudentDetails = new ShowStudentDetails();
                String[] params = {usn};
                showStudentDetails.execute(params);

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class AddStudentToGroup extends AsyncTask<String, Void, Void> {

        @NonNull
        Boolean errorOccured;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorOccured = true;
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);

            if (!errorOccured) {
                students.clear();
                FetchStudents fetchStudents = new FetchStudents();
                String[] params = {proctor_id};
                fetchStudents.execute(params);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String usn = strings[0];
                String proctor_id = strings[1];
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/add_student_proctor?student_usn=%s&faculty_id=%s", usn, proctor_id));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                errorOccured = result.getBoolean("error");
                System.out.println("Error=" + errorOccured);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ShowStudentDetails extends AsyncTask<String, Void, Void> {

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

            Intent intent = new Intent(getActivity(), StudentDetails.class);
            intent.putExtra("usn", usn);
            intent.putExtra("name", name);
            intent.putExtra("dept", dept);
            intent.putExtra("quota", quota);
            intent.putExtra("email", email);
            intent.putExtra("join", join);
            intent.putExtra("grad", grad);
            intent.putExtra("semester", semester);
            intent.putExtra("phone", phone);
            startActivity(intent);


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
                    int year = Calendar.getInstance().get(Calendar.YEAR) - join + 1;
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


    public class FetchStudents extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {

            String proctor_id = strings[0];

            try {
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/get_students?faculty_id=%s", proctor_id));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();
                String result = Utility.fetchResponseHttps(conn);
                JSONArray res_students = new JSONArray(result);
                ArrayList<Student> temp = new ArrayList<>();

                for (int i = 0; i < res_students.length(); i++) {
                    JSONObject item = res_students.getJSONObject(i);
                    temp.add(new Student(item.getString("usn"), item.getString("name"), item.getString("dept")));
                }
                students.clear();
                students.addAll(temp);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
