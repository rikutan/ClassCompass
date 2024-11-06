package com.example.classcompass.dataModels;


public class StudentsDataModel {


    //  Studentsノードのデータ型の定義  /////////////////////////////////////////
    private String studentID;
    private String studentName;
    private String studentNameKana;
    private String studentMail;
    private String studentIcon;
    private String studentBirthday;
    private String studentComment;



    public StudentsDataModel() {
        // デフォルトコンストラクタ
    }

    // コンストラクタ
    public StudentsDataModel(String studentID, String studentName, String studentNameKana, String studentMail, String studentIcon, String studentBirthday, String studentComment) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentNameKana = studentNameKana;
        this.studentMail = studentMail;
        this.studentIcon = studentIcon;
        this.studentBirthday = studentBirthday;
        this.studentComment = studentComment;
    }




    // ノードの値を取得したり, 登録したりする操作部品の定義
    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNameKana() {
        return studentNameKana;
    }
    public void setStudentNameKana(String studentNameKana) {
        this.studentNameKana = studentNameKana;
    }

    public String getStudentMail() {
        return studentMail;
    }
    public void setStudentMail(String studentMail) {
        this.studentMail = studentMail;
    }

    public String getStudentIcon() {
        return studentIcon;
    }
    public void setStudentIcon(String studentIcon) {
        this.studentIcon = studentIcon;
    }

    public String getStudentBirthday() {
        return studentBirthday;
    }
    public void setStudentBirthday(String studentBirthday) {
        this.studentBirthday = studentBirthday;
    }

    public String getStudentComment() {
        return studentComment;
    }
    public void setStudentComment(String studentComment) {
        this.studentComment = studentComment;
    }



}
