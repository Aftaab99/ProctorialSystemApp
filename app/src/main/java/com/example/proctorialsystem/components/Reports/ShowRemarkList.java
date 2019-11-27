package com.example.proctorialsystem.components.Reports;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proctorialsystem.R;

import java.util.ArrayList;

public class ShowRemarkList extends AppCompatActivity {

    ArrayList<ReportEntry> reportEntries = new ArrayList<>();
    RemarkListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_remark_list);
        ListView remarkList = findViewById(R.id.remark_list);

        reportEntries=(ArrayList<ReportEntry>)getIntent().getSerializableExtra("reports");
        adapter = new RemarkListAdapter(this, R.layout.show_remark_list_item, R.id.row_id_tv1, reportEntries);
        remarkList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class RemarkListAdapter extends ArrayAdapter<ReportEntry> {
        Context context;
        ArrayList<ReportEntry> re;

        public RemarkListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<ReportEntry> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            re = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.show_remark_list_item, parent, false);
            TextView usntv = convertView.findViewById(R.id.usn);
            TextView remarktv = convertView.findViewById(R.id.remark);
            usntv.setText(re.get(position).getStudentUsn());
            if(re.get(position).getRemarkMessage().equals(""))
            remarktv.setText("No remarks");
            else
                remarktv.setText(re.get(position).getRemarkMessage());
            return super.getView(position, convertView, parent);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
