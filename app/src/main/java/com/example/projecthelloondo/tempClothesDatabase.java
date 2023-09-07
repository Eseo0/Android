package com.example.projecthelloondo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {tempClothes.class},version = 1)
public abstract class tempClothesDatabase extends RoomDatabase
{
    public abstract tempClothesDao getTempClothesDao();
}
