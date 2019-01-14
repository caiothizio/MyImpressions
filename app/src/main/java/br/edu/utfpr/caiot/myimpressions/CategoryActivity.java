package br.edu.utfpr.caiot.myimpressions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Category;
import br.edu.utfpr.caiot.myimpressions.persistence.MyImpressionsDatabase;
import br.edu.utfpr.caiot.myimpressions.utils.UtilsGUI;

public class CategoryActivity extends AppCompatActivity {

    private EditText editTextCatName;
    private List<Category> listaCategorias;
    private ArrayAdapter<Category> listaAdapter;

    private int modo;

    public static final String CATEGORY = "CATEGORY";
    public static final String MODO = "MODO";
    public static final String ID = "ID";

    public static final int NOVO = 1;
    public static final int UPDATE = 2;

    private ConstraintLayout layout;
    private Category category;

    public static void novo(Activity activity, int requestCode){

        Intent intent = new Intent(activity, CategoryActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Category category){

        Intent intent = new Intent(activity, CategoryActivity.class);

        intent.putExtra(MODO, UPDATE);

        intent.putExtra(ID, category.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        editTextCatName = findViewById(R.id.editTextCatName);

        layout  = findViewById(R.id.newcategoryLayout);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if(bundle != null){
            modo = bundle.getInt(MODO, NOVO);

            if(modo == NOVO){
                setTitle(R.string.new_category);

                category = new Category("");
            }else{
                setTitle(R.string.update_category);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);

                        MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(CategoryActivity.this);

                        category = database.categoryDao().queryForId(id);

                        CategoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                editTextCatName.setText(category.getCategoryName());
                            }
                        });
                    }
                });
            }
        }
        changeColor();

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
    }

    private void changeColor(){
        layout.setBackgroundColor(PrincipalActivity.selectedColor);
    }

    public void salvarButtonClicked(View view){
        saveCategory();
    }

    private void saveCategory(){
        String name = UtilsGUI.validaCampoTexto(this, editTextCatName, R.string.name_empty);
        if(name == null)return;

        category.setCategoryName(name);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(CategoryActivity.this);

                if (modo == NOVO) {

                    int novoId = (int) database.categoryDao().insert(category);

                    category.setId(novoId);

                } else {

                    database.categoryDao().update(category);
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelar();
    }
}
