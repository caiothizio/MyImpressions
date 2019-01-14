package br.edu.utfpr.caiot.myimpressions.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Category;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Delete
    void delete(Category category);

    @Update
    void update(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category queryForId(long id);

    @Query("SELECT * FROM categories ORDER BY categoryName ASC")
    List<Category> queryAll();
}
