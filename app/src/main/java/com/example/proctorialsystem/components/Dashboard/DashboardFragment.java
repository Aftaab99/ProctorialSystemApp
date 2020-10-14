package com.example.proctorialsystem.components.Dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.example.proctorialsystem.login.ProctorLoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DashboardFragment extends Fragment {

    private String proctor_id;
    public static List<Student> students = new ArrayList<>();
    private StudentLisViewAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains("JWT_TOKEN")) {
            Intent gotoLogin = new Intent(getActivity(), ProctorLoginActivity.class);
            startActivity(gotoLogin);
        }
        proctor_id = getArguments().getString("proctor_id");
        String proctor_name = getArguments().getString("name");

        Button logout_btn = view.findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("JWT_TOKEN");
                editor.apply();
                editor.commit();

                Intent intent = new Intent(getActivity(), ProctorLoginActivity.class);
                startActivity(intent);
            }
        });
        progressBar = view.findViewById(R.id.progressBar);
        ListView studentList = view.findViewById(R.id.student_list);
        adapter = new StudentLisViewAdapter(Objects.requireNonNull(getActivity())
                , R.id.student_list, R.id.row_id_tv, students, this);
        studentList.setAdapter(adapter);
        TextView welcomeET = view.findViewById(R.id.welcome_text);
        welcomeET.setText(String.format("Welcome, %s", proctor_name));
        final TextInputEditText addUSNET = view.findViewById(R.id.student_usn_tag);
        final Button addStudent = view.findViewById(R.id.add_student);
        FetchStudents fetchStudents = new FetchStudents();
        fetchStudents.execute(proctor_id);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usn = Objects.requireNonNull(addUSNET.getText()).toString();
                System.out.println("USN=" + usn);
                if (Utility.isValidUSN(usn)) {
                    AddStudentToGroup addStudentToGroup = new AddStudentToGroup();
                    addStudentToGroup.execute(usn, proctor_id);
                } else {
                    addUSNET.setError("Invalid USN");
                }
            }
        });

        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String usn = students.get(position).getUSN();
                System.out.println("Clicked=" + usn);

                Intent gotoStudentDetails = new Intent(getActivity(), StudentDetails.class);
                gotoStudentDetails.putExtra("usn", usn);
                startActivity(gotoStudentDetails);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class AddStudentToGroup extends AsyncTask<String, Void, Void> {

        Boolean errorOccurred, sessionInvalidated;
        String proctor_id;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            token = preferences.getString("JWT_TOKEN", "");
            errorOccurred = false;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            if (!sessionInvalidated) {
                if (!errorOccurred) {
                    students.clear();
                    FetchStudents fetchStudents = new FetchStudents();
                    fetchStudents.execute(proctor_id);
                } else
                    Toast.makeText(getContext(), "Unable to add student. Student does not exist", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Session Invalidated, logging out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ProctorLoginActivity.class));
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String usn = strings[0];
                String proctor_id = strings[1];
                String session_token = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("JWT_TOKEN", "");
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/add_student_proctor?student_usn=%s&faculty_id=%s", usn, proctor_id));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", session_token);
                conn.setRequestMethod("GET");
                conn.connect();
                if (conn.getResponseCode() == 403) {
                    sessionInvalidated = true;
                    return null;
                }
                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                errorOccurred = result.getBoolean("error");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void gotoLoginActivity(){
        Toast.makeText(getActivity(), "Session Invalidated, logging out.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), ProctorLoginActivity.class));
    }

    public class FetchStudents extends AsyncTask<String, Void, Void> {
        String token;
        Boolean sessionInvalidated;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            token = preferences.getString("JWT_TOKEN", "");
            sessionInvalidated = false;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sessionInvalidated) {
                Toast.makeText(getContext(), "Session Invalidated, logging out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ProctorLoginActivity.class));
                return;
            }

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
                conn.setRequestProperty("Authorization", token);
                conn.connect();
                if (conn.getResponseCode() == 403) {
                    sessionInvalidated = true;
                    return null;
                }
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
