package br.edu.utfpr.caiot.myimpressions.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Establishment;

@Dao
public interface EstablishmentDao {
    @Insert
    long insert(Establishment establishment);

    @Delete
    void delete(Establishment establishment);

    @Update
    void update(Establishment establishment);

    @Query("SELECT * FROM establishments WHERE id = :id")
    Establishment queryForId(long id);

    @Query("SELECT * FROM establishments ORDER BY name ASC")
    List<Establishment> queryAll();
}
