package com.example.proctorialsystem.components.Reports;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;
import com.example.proctorialsystem.components.Dashboard.DashboardFragment;
import com.example.proctorialsystem.components.Dashboard.Student;
import com.example.proctorialsystem.login.ProctorLoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ReportsFragment extends Fragment {

    ArrayList<Report> reports = new ArrayList<>();
    ProgressBar pb;
    ReportListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String proctor_id = getArguments().getString("proctor_id");


        pb = view.findViewById(R.id.progressBar);

        adapter = new ReportListAdapter(getActivity(), R.layout.proctor_meet_list_item, R.id.count_indictor_tv, reports);
        ListView reportListView = view.findViewById(R.id.proctor_meet_list);
        reportListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report rep = reports.get(position);
                Intent intent = new Intent(getActivity(), ShowRemarkList.class);
                intent.putExtra("reports", rep.getReportEntries());
                startActivity(intent);
            }
        });

        FetchReports fetchReports = new FetchReports();
        String[] params = {proctor_id};
        fetchReports.execute(params);
        Button startNewMeet = view.findViewById(R.id.startNewProctorMeet);
        startNewMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> usns = new ArrayList<>();
                for (Student s : DashboardFragment.students) {
                    usns.add(s.getUSN());
                }
                String[] usns_arr = new String[usns.size()];
                for (int i = 0; i < usns.size(); i++) {
                    usns_arr[i] = usns.get(i);
                }
                Intent intent = new Intent(getActivity(), NewProctorMeetActivity.class);
                intent.putExtra("students", usns_arr);

                intent.putExtra("proctor_id", proctor_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FetchReports fetchReports = new FetchReports();
        String[] params = {getArguments().getString("proctor_id")};
        fetchReports.execute(params);
    }

    public class FetchReports extends AsyncTask<String, Void, Void> {
        String token;
        Boolean sessionInvalidated;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sessionInvalidated = false;
            token = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("JWT_TOKEN", "");
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb.setVisibility(View.INVISIBLE);
            if(sessionInvalidated){
                Toast.makeText(getContext(), "Session Invalidated, logging out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ProctorLoginActivity.class));
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                URL url = new URL(String.format("https://proctorial-system.herokuapp.com/app/fetch_reports?proctor_id=%s", strings[0]));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", token);
                conn.connect();
                if(conn.getResponseCode() == 403){
                    sessionInvalidated = true;
                    return null;
                }
                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                System.out.println(res);
                Iterator<String> keys = result.keys();
                reports.clear();
                while (keys.hasNext()) {
                    String date = keys.next();
                    JSONArray usnRemarks = result.getJSONArray(date);
                    ArrayList<ReportEntry> re = new ArrayList<>();
                    for (int i = 0; i < usnRemarks.length(); i++) {
                        String usn = usnRemarks.getJSONArray(i).getString(0);
                        String remark = usnRemarks.getJSONArray(i).getString(1);
                        re.add(new ReportEntry(usn, remark));
                    }
                    reports.add(new Report(parseDate(date), re));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}