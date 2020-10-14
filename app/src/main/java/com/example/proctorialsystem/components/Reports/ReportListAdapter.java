package com.example.proctorialsystem.components.Reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proctorialsystem.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReportListAdapter extends ArrayAdapter<Report> {

    private Context context;
    private ArrayList<Report> reports;

    public ReportListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Report> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.reports = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.proctor_meet_list_item, parent, false);

        Report report = reports.get(position);
        TextView repDate = convertView.findViewById(R.id.proctor_meet_date);
        repDate.setText(dateToString(report.getReportDate()));
        return super.getView(position, convertView, parent);

    }

    // To represent date in words, example: 3/6/19 as 3rd June, 2019
    private String dateToString(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day % 10 == 1) {
            return String.format(Locale.getDefault(), "%dst %s, %d", day, month, year);
        }
        if (day % 10 == 2) {
            return String.format(Locale.getDefault(), "%dnd %s, %d", day, month, year);
        }
        if (day % 10 == 3) {
            return String.format(Locale.getDefault(), "%drd %s, %d", day, month, year);
        }
        return String.format(Locale.getDefault(), "%dth %s, %d", day, month, year);
    }
}
