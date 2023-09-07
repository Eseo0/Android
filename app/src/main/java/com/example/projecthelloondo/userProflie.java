package com.example.projecthelloondo;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity//(indices = {@Index(value = {"kakao_id"},unique = true)})
public class userProflie {
    @PrimaryKey(autoGenerate = true)
    private  int id;

    private String constitution;

    public String getConstitution() {
        return constitution;
    }

    public void setConstitution(String constitution) {
        this.constitution = constitution;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge_range() {
        return age_range;
    }

    public void setAge_range(String age_range) {
        this.age_range = age_range;
    }

    @ColumnInfo(name = "kakao_id")
    private String kakao_id;

    private String nickname;
    private String gender;
    private String age_range;

    public String getProflieImg() {
        return proflieImg;
    }

    public void setProflieImg(String proflieImg) {
        this.proflieImg = proflieImg;
    }

    private String proflieImg;
}
