package com.example.proctorialsystem.components.Dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class StudentLisViewAdapter extends ArrayAdapter {

    Context mContext;
    List<Student> students;


    public StudentLisViewAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Student> students) {
        super(context, resource, textViewResourceId, students);
        this.mContext = context;
        this.students = students;
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

        @NonNull
        Boolean removeSuccessful;
        int position;
        Student student;

        @Override
        protected void onPreExecute() {

            removeSuccessful = false;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
            System.out.println("Removing="+student.getName());
            try {
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/remove_student_proctor?student_usn=%s", usn));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
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
