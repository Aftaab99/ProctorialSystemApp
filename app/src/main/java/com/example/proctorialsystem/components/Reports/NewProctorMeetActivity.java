package com.example.proctorialsystem.components.Reports;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proctorialsystem.R;
import com.example.proctorialsystem.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class NewProctorMeetActivity extends AppCompatActivity {

    String[] usns;
    String proctor_id;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_proctor_meet);
        usns = getIntent().getExtras().getStringArray("students");
        proctor_id = getIntent().getExtras().getString("proctor_id");
        final ListView remarkList = findViewById(R.id.student_remark_list);
        RemarkListAdapter adapter = new RemarkListAdapter(this, R.layout.remark_list_item, R.id.row_id_tv, usns);
        remarkList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ReportEntry> re = new ArrayList<>();

                for (int i = 0; i < usns.length; i++) {
                    String usn_ = usns[i];
                    final View view1 = remarkList.getChildAt(i);

                    EditText em_et = view1.findViewById(R.id.remark_text);
                    CheckBox cb = view1.findViewById(R.id.checkBox);
                    if (cb.isChecked()) {
                        String remark = em_et.getText().toString();
                        re.add(new ReportEntry(usn_, remark));

                    }
                }

                ReportEntry[] res = new ReportEntry[re.size()];
                for (int i = 0; i < re.size(); i++) {
                    res[i] = re.get(i);
                    System.out.println("\nDATA:"+res[i]);
                }

                SendReportData srd = new SendReportData();
                srd.execute(res);
            }
        });

    }


    public class SendReportData extends AsyncTask<ReportEntry, Void, Void> {
        Boolean errorOccured;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorOccured=true;
            String pattern = "dd/MM/yyyy";
            date =new SimpleDateFormat(pattern).format(new Date());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!errorOccured){
                Toast.makeText(NewProctorMeetActivity.this, "Report saved", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected Void doInBackground(ReportEntry... reportEntries) {

            try {
                URL url=new URL("https://proctorial-system.herokuapp.com/app/store_proctor_details");
                HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                JSONObject reqParent =new JSONObject();
                JSONArray reqObj = new JSONArray();
                for(int i=0;i<reportEntries.length;i++){
                    JSONObject sub=new JSONObject();
                    sub.put("usn", reportEntries[i].getStudentUsn());
                    sub.put("remark", reportEntries[i].getRemarkMessage());
                    reqObj.put(i, sub);
                }
                reqParent.put("report_entries", reqObj);
                reqParent.put("proctor_id", proctor_id);
                reqParent.put("meet_date", date);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches (false);

                conn.setRequestProperty("Content-Type","application/json");
                DataOutputStream printout = new DataOutputStream(conn.getOutputStream ());
                printout.writeBytes(reqParent.toString());
                printout.flush ();
                printout.close ();
                conn.connect();

                String res = Utility.fetchResponseHttps(conn);
                JSONObject result = new JSONObject(res);
                if(!result.getBoolean("error")){
                    errorOccured=false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class RemarkListAdapter extends ArrayAdapter<String> {

        Context mContext;
        String[] usn_list;

        public RemarkListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
            super(context, resource, textViewResourceId, objects);
            mContext = context;
            usn_list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.remark_list_item, parent, false);

            CheckBox checkBox = convertView.findViewById(R.id.checkBox);
            final EditText remarkField = convertView.findViewById(R.id.remark_text);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    remarkField.setEnabled(isChecked);
                }
            });
            checkBox.setText(usn_list[position]);
            return super.getView(position, convertView, parent);
        }
    }




}
