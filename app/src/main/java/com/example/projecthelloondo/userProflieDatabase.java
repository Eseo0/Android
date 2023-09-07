package com.example.projecthelloondo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {userProflie.class},version = 2)
public abstract class userProflieDatabase  extends RoomDatabase
{
    public abstract userprofileDao getUserProfileDao();
}
