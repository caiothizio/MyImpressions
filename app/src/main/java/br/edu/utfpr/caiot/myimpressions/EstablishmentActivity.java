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
import android.widget.Spinner;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Category;
import br.edu.utfpr.caiot.myimpressions.model.Establishment;
import br.edu.utfpr.caiot.myimpressions.persistence.MyImpressionsDatabase;
import br.edu.utfpr.caiot.myimpressions.utils.UtilsGUI;

public class EstablishmentActivity extends AppCompatActivity {
    private EditText editTextName;
    private Spinner spinnerCategory;
    private EditText editTextAddress;
    private List<Category> listaCategorias;
    private ArrayAdapter<Category> listaAdapter;

    private int modo;

    public static final String ESTABLISHMENT = "ESTABLISHMENT";
    public static final String MODO = "MODO";
    public static final String ID = "ID";

    public static final int NOVO = 1;
    public static final int UPDATE = 2;

    private ConstraintLayout layout;
    private Establishment establishment;
    private Category category;

    public static void novo(Activity activity, int requestCode){

        Intent intent = new Intent(activity, EstablishmentActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Establishment establishment){

        Intent intent = new Intent(activity, EstablishmentActivity.class);

        intent.putExtra(MODO, UPDATE);

        intent.putExtra(ID, establishment.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_establishment);

        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        layout  = findViewById(R.id.establishmentLayout);

        //populateSpinner();

        carregaCategorias();

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if(bundle != null){
            modo = bundle.getInt(MODO, NOVO);

            if(modo == NOVO){
                setTitle(R.string.new_establishment);

                establishment = new Establishment("", "");
            }else{
                setTitle(R.string.update_establishment);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);

                        MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(EstablishmentActivity.this);

                        establishment = database.establishmentDao().queryForId(id);

                        category = database.categoryDao().queryForId(establishment.getCategoryId());

                        EstablishmentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                editTextName.setText(establishment.getName());

                                editTextAddress.setText(establishment.getAddress());

                                int posicao = posicaoCategoria(establishment.getCategoryId());
                                spinnerCategory.setSelection(posicao);
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
        saveEstablishment();
    }

    private void saveEstablishment(){
        String name = UtilsGUI.validaCampoTexto(this, editTextName, R.string.name_empty);
        if(name == null)return;

        String address = UtilsGUI.validaCampoTexto(this, editTextAddress, R.string.address_empty);
        if(address == null)return;

        establishment.setName(name);
        establishment.setAddress(address);

        Category category = (Category) spinnerCategory.getSelectedItem();
        if(category != null)establishment.setCategoryId(category.getId());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(EstablishmentActivity.this);

                if (modo == NOVO) {

                    int novoId = (int) database.establishmentDao().insert(establishment);

                    establishment.setId(novoId);

                } else {

                    database.establishmentDao().update(establishment);
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

    private int posicaoCategoria(int categoryId){

        for (int pos = 0; pos < listaCategorias.size(); pos++){

            Category c = listaCategorias.get(pos);

            if (c.getId() == categoryId){
                return pos;
            }
        }

        return -1;
    }

    private void carregaCategorias(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(EstablishmentActivity.this);

                listaCategorias = database.categoryDao().queryAll();

                EstablishmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(EstablishmentActivity.this,
                                android.R.layout.simple_list_item_1,
                                listaCategorias);

                        spinnerCategory.setAdapter(spinnerAdapter);


                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        cancelar();
    }
}
