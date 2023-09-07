package com.example.projecthelloondo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface tempClothesDao {

    @Insert
    void insert(tempClothes tempClothes);

    @Query("SELECT clothes FROM tempClothes WHERE `temp` = :windChill")
    String SelectClothes(int windChill);

    @Query("SELECT category FROM tempClothes WHERE `temp` = :temp")
    String SelectCategory(int temp);

    @Query("SELECT * FROM tempClothes")
    List<tempClothes> SelectAll();

}
