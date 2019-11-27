package com.example.proctorialsystem.components.Updates;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdatesFragment extends Fragment {


    private ProgressBar progressBar;
    private Button parentPhone,parentEmail,studentPhone,studentEmail;
    private LinearLayout studentDetails,parentDetails;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_updates, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextInputEditText studentUSNET = view.findViewById(R.id.student_usn);
        parentPhone = view.findViewById(R.id.parent_phone_btn);
        parentEmail = view.findViewById(R.id.parent_email_btn);
        studentPhone = view.findViewById(R.id.student_phone_btn);
        studentEmail = view.findViewById(R.id.student_email_btn);
        progressBar = view.findViewById(R.id.progressBar);
        Button fetchContact = view.findViewById(R.id.get_details);

        studentDetails = view.findViewById(R.id.student_contact);
        parentDetails = view.findViewById(R.id.parent_contact);

        fetchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchParentContact fetch = new FetchParentContact();
                String[] params = {studentUSNET.getText().toString()};
                fetch.execute(params);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    public class FetchParentContact extends AsyncTask<String, Void, Void> {
        String parentPhoneStr, parentEmailStr, studentPhoneStr, studentEmailStr;
        Boolean errorOccured;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            errorOccured=true;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String usn = strings[0];

                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/fetch_parent_details?student_usn=%s", usn));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();

                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                if (!result.getBoolean("error")) {
                    errorOccured=false;
                    parentEmailStr = result.getString("parent_email");
                    parentPhoneStr = result.getString("parent_phone");
                    studentEmailStr = result.getString("student_email");
                    studentPhoneStr = result.getString("student_phone");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            if(!errorOccured) {
                parentPhone.setText(parentPhoneStr);
                parentPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = ("tel:" + parentPhoneStr);
                        Intent mIntent = new Intent(Intent.ACTION_CALL);
                        mIntent.setData(Uri.parse(number));
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    33);

                        } else {
                            //You already have permission
                            try {
                                startActivity(mIntent);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                studentPhone.setText(studentPhoneStr);
                studentPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = ("tel:" + studentPhoneStr);
                        Intent mIntent = new Intent(Intent.ACTION_CALL);
                        mIntent.setData(Uri.parse(number));
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    33);

                        } else {
                            //You already have permission
                            try {
                                startActivity(mIntent);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                parentEmail.setText(parentEmailStr);
                parentEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, parentEmailStr);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance update");
                        intent.putExtra(Intent.EXTRA_TEXT, "This is a autogenerated email to inform you that your ward has shortage of attendance and may not be elligible to take them the next examination");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                });
                studentEmail.setText(studentEmailStr);
                studentEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, studentEmailStr);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance update");
                        intent.putExtra(Intent.EXTRA_TEXT, "This is a autogenerated email to inform you that your ward has shortage of attendance and may not be elligible to take them the next examination");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                });

                studentDetails.setVisibility(View.VISIBLE);
                parentDetails.setVisibility(View.VISIBLE);


            }

            else{
                Toast.makeText(getContext(), "Unable to fetch details", Toast.LENGTH_LONG);
            }
        }
    }

}