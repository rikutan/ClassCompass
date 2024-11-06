package com.example.classcompass.dataModels;

public class ClassRoomsDataModel {

    //  ClassRoomsノードのデータ型の定義  /////////////////////////////////////////
    private String classRoomID;
    private String className;
    private String classQR;
    private String schoolName;
    private String year;
    private String classIcon;
    private String memberID;
    private String deviceID;
    private Boolean classRoomNotice;
    private String readClassRoomChatID;
    private String lastClassRoomChatID;
    private String classRoomChatID;
    private String sender;
    private String message;
    private String img;
    private String lotteryID;
    private String lotteryTitle;
    private String lotteryDate;
    private String lotteryItemID;
    private String lotteryItem;
    private Integer winners;
    private String lotteryResultID;
    private String albumID;
    private String albumName;
    private String created;
    private String photoID;
    private String photo;





    public ClassRoomsDataModel() {
        // デフォルトコンストラクタ
    }

    // コンストラクタ
    public ClassRoomsDataModel(String classRoomID, String className, String classQR, String schoolName, String year, String classIcon, String memberID, String deviceID, Boolean classRoomNotice, String readClassRoomChatID, String lastClassRoomChatID, String classRoomChatID, String sender, String message, String img, String lotteryID, String lotteryTitle, String lotteryDate, String lotteryItemID, String lotteryItem, Integer winners, String lotteryResultID, String albumID, String albumName, String created, String photoID, String photo) {
        this.classRoomID = classRoomID;
        this.className = className;
        this.classQR = classQR;
        this.schoolName = schoolName;
        this.year = year;
        this.classIcon = classIcon;
        this.memberID = memberID;
        this.deviceID = deviceID;
        this.classRoomNotice = classRoomNotice;
        this.readClassRoomChatID = readClassRoomChatID;
        this.lastClassRoomChatID = lastClassRoomChatID;
        this.classRoomChatID = classRoomChatID;
        this.sender = sender;
        this.message = message;
        this.img = img;
        this.lotteryID = lotteryID;
        this.lotteryTitle = lotteryTitle;
        this.lotteryDate = lotteryDate;
        this.lotteryItemID = lotteryItemID;
        this.lotteryItem = lotteryItem;
        this.winners = winners;
        this.lotteryResultID = lotteryResultID;
        this.albumID = albumID;
        this.albumName = albumName;
        this.created = created;
        this.photoID = photoID;
        this.photo = photo;
    }




    // ノードの値を取得したり, 登録したりする操作部品の定義
    public String getClassRoomID() {
        return classRoomID;
    }
    public void setClassRoomID(String classRoomID) {
        this.classRoomID = classRoomID;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassQR() {
        return classQR;
    }
    public void setClassQR(String classQR) {
        this.classQR = classQR;
    }

    public String getSchoolName() {
        return schoolName;
    }
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }

    public String getClassIcon() {
        return classIcon;
    }
    public void setClassIcon(String classIcon) {
        this.classIcon = classIcon;
    }

    public String getMemberID() {
        return memberID;
    }
    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Boolean getClassRoomNotice() {
        return classRoomNotice;
    }
    public void setClassRoomNotice(Boolean classRoomNotice) {
        this.classRoomNotice = classRoomNotice;
    }
    public String getReadClassRoomChatID() {
        return readClassRoomChatID;
    }
    public void setReadClassRoomChatID(String readClassRoomChatID) {
        this.readClassRoomChatID = readClassRoomChatID;
    }
    public String getLastClassRoomChatID() {
        return lastClassRoomChatID;
    }
    public void setLastClassRoomChatID(String lastClassRoomChatID) {
        this.lastClassRoomChatID = lastClassRoomChatID;
    }
    public String getClassRoomChatID() {
        return classRoomChatID;
    }
    public void setClassRoomChatID(String classRoomChatID) {
        this.classRoomChatID = classRoomChatID;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getLotteryID() {
        return lotteryID;
    }
    public void setLotteryID(String lotteryID) {
        this.lotteryID = lotteryID;
    }

    public String getLotteryTitle() {
        return lotteryTitle;
    }
    public void setLotteryTitle(String lotteryTitle) {
        this.lotteryTitle = lotteryTitle;
    }

    public String getLotteryDate() {
        return lotteryDate;
    }
    public void setLotteryDate(String lotteryDate) {
        this.lotteryDate = lotteryDate;
    }

    public String getLotteryItemID() {
        return lotteryItemID;
    }
    public void setLotteryItemID(String lotteryItemID) {
        this.lotteryItemID = lotteryItemID;
    }

    public String getLotteryItem() {
        return lotteryItem;
    }
    public void setLotteryItem(String lotteryItem) {
        this.lotteryItem = lotteryItem;
    }

    public Integer getWinners() {
        return winners;
    }
    public void setWinners(Integer winners) {
        this.winners = winners;
    }

    public String getLotteryResultID() {
        return lotteryResultID;
    }
    public void setLotteryResultID(String lotteryResultID) {
        this.lotteryResultID = lotteryResultID;
    }

    public String getAlbumID() {
        return albumID;
    }
    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public String getPhotoID() {
        return photoID;
    }
    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
