package br.edu.utfpr.caiot.myimpressions.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "establishments",
        foreignKeys = @ForeignKey(entity = Category.class,
                                   parentColumns = "id",
                                   childColumns = "categoryId"))

public class Establishment implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @ColumnInfo(index = true)
    private int categoryId;

    @NonNull
    private String address;

    //constructor
    public Establishment(String name, String address){
        setName(name);
        setAddress(address);
    }

    //getters
    public int getId(){return id;}
    @NonNull public String getName(){return name;}
    public int getCategoryId(){return categoryId;}
    @NonNull public String getAddress(){return address;}

    //setters
    public void setId(int id){this.id = id;}
    public void setName(@NonNull String name){this.name = name;}
    public void setCategoryId(int categoryId){this.categoryId = categoryId;}
    public void setAddress(@NonNull String address){this.address = address;}


    @Override
    public String toString(){return getName();}
}
