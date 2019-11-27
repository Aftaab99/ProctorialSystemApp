package com.example.proctorialsystem.components.Reports;

import java.util.ArrayList;
import java.util.Date;

public class Report {
    private Date reportDate;
    private ArrayList<ReportEntry> reportEntries;

    public Report(Date reportDate, ArrayList<ReportEntry> reportEntries) {
        this.reportDate = reportDate;
        this.reportEntries = reportEntries;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public ArrayList<ReportEntry> getReportEntries() {
        return reportEntries;
    }

    public void setReportEntries(ArrayList<ReportEntry> reportEntries) {
        this.reportEntries = reportEntries;
    }
}
