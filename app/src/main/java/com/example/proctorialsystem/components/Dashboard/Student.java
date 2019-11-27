package com.example.proctorialsystem.components.Dashboard;

public class Student {
    String USN;
    String name;
    String dept;

    public String getUSN() {
        return USN;
    }

    public void setUSN(String USN) {
        this.USN = USN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Student(String USN, String name, String dept) {
        this.USN = USN;
        this.name = name;
        this.dept = dept;
    }
}
