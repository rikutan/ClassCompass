package com.example.classcompass.dataModels;


public class TeachersDataModel {


    //  Teachersノードのデータ型の定義  /////////////////////////////////////////
    private String teacherID;
    private String teacherName;
    private String teacherNameKana;
    private String teacherMail;
    private String teacherIcon;
    private String teacherBirthday;
    private String teacherComment;



    public TeachersDataModel() {
        // デフォルトコンストラクタ
    }

    // コンストラクタ
    public TeachersDataModel(String teacherID, String teacherName, String teacherNameKana, String teacherMail, String teacherIcon, String teacherBirthday, String teacherComment) {
        this.teacherID = teacherID;
        this.teacherName = teacherName;
        this.teacherNameKana = teacherNameKana;
        this.teacherMail = teacherMail;
        this.teacherIcon = teacherIcon;
        this.teacherBirthday = teacherBirthday;
        this.teacherComment = teacherComment;
    }




    // ノードの値を取得したり, 登録したりする操作部品の定義
    public String getTeacherID() {
        return teacherID;
    }
    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherNameKana() {
        return teacherNameKana;
    }
    public void setTeacherNameKana(String teacherNameKana) {
        this.teacherNameKana = teacherNameKana;
    }

    public String getTeacherMail() {
        return teacherMail;
    }
    public void setTeacherMail(String teacherMail) {
        this.teacherMail = teacherMail;
    }

    public String getTeacherIcon() {
        return teacherIcon;
    }
    public void setTeacherIcon(String teacherIcon) {
        this.teacherIcon = teacherIcon;
    }

    public String getTeacherBirthday() {
        return teacherBirthday;
    }
    public void setTeacherBirthday(String teacherBirthday) {
        this.teacherBirthday = teacherBirthday;
    }

    public String getTeacherComment() {
        return teacherComment;
    }
    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }



}
