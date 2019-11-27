package com.example.proctorialsystem.components.Reports;

import java.io.Serializable;

public class ReportEntry implements Serializable {
    private String studentUsn;
    private String remarkMessage;

    public ReportEntry(String studentUsn, String remarkMessage) {
        this.studentUsn = studentUsn;
        this.remarkMessage = remarkMessage;
    }

    public String getStudentUsn() {
        return studentUsn;
    }

    public void setStudentUsn(String studentUsn) {
        this.studentUsn = studentUsn;
    }

    public String getRemarkMessage() {
        return remarkMessage;
    }

    public void setRemarkMessage(String remarkMessage) {
        this.remarkMessage = remarkMessage;
    }
}
