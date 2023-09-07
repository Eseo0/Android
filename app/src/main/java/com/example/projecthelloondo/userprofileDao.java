package com.example.projecthelloondo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface userprofileDao {
    @Insert
    void insert(userProflie userProflie);

    @Query("SELECT kakao_id FROM userProflie")
    List<String> getkakaoid();

    @Query("SELECT constitution FROM userProflie where nickname = :nickname")
    String selectConstitution(String nickname);

    @Query("update userProflie set constitution = :constitution where nickname = :nickname")
    void update_Constitution(String constitution, String nickname);

}
