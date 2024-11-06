package com.example.classcompass;

public class CRSCAdapterItem {

    private String memberID = ""; //  教師か生徒か
    private String memberIcon = ""; //  アイコン
    private String memberName = ""; //  名前

    public CRSCAdapterItem(String memberID, String memberIcon, String memberName) {
        this.memberID = memberID;
        this.memberIcon = memberIcon;
        this.memberName = memberName;
    }

    public String getMemberID() {
        return memberID;
    }
    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }
    public String getMemberIcon() {
        return memberIcon;
    }
    public void setMemberIcon(String memberIcon) {
        this.memberIcon = memberIcon;
    }
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


}
