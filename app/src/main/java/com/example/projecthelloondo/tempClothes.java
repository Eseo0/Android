package com.example.projecthelloondo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class tempClothes {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int temp;

    private String clothes;

    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getClothes() {
        return clothes;
    }

    public void setClothes(String clothes) {
        this.clothes = clothes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
