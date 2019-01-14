package br.edu.utfpr.caiot.myimpressions.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "categories",
        indices = @Index(value = {"categoryName"}, unique = true)
)
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String categoryName;

    public Category(@NonNull String categoryName){setCategoryName(categoryName);}

    //setters
    public void setId(int id){this.id = id;}
    public void setCategoryName(@NonNull String categoryName){this.categoryName = categoryName;}

    //getters
    public int getId(){return this.id;}
    @NonNull public String getCategoryName(){return this.categoryName;}

    @Override
    public String toString(){return getCategoryName();}
}
