package br.edu.utfpr.caiot.myimpressions.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import br.edu.utfpr.caiot.myimpressions.R;
import br.edu.utfpr.caiot.myimpressions.model.Category;
import br.edu.utfpr.caiot.myimpressions.model.Establishment;

@Database(entities = {Establishment.class, Category.class}, version = 1)
public abstract class MyImpressionsDatabase extends RoomDatabase {

    public abstract EstablishmentDao establishmentDao();

    public abstract CategoryDao categoryDao();

    private static MyImpressionsDatabase instance;


    public static MyImpressionsDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (MyImpressionsDatabase.class) {
                if (instance == null) {
                    Builder builder =  Room.databaseBuilder(context,
                            MyImpressionsDatabase.class,
                            "myimpressions.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaCategoriasIniciais(context);
                                }
                            });
                        }
                    });

                    instance = (MyImpressionsDatabase) builder.build();
                }
            }
        }

        return instance;
    }

    private static void carregaCategoriasIniciais(final Context context){

        String[] categorias = context.getResources().getStringArray(R.array.categories);

        for (String categoria : categorias) {

            Category cat = new Category(categoria);

            instance.categoryDao().insert(cat);
        }
    }

}
