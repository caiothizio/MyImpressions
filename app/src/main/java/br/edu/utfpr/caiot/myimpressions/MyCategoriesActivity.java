package br.edu.utfpr.caiot.myimpressions;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Category;
import br.edu.utfpr.caiot.myimpressions.persistence.MyImpressionsDatabase;
import br.edu.utfpr.caiot.myimpressions.utils.UtilsGUI;

public class MyCategoriesActivity extends AppCompatActivity {

    private ListView listViewCategories;
    private ArrayAdapter<Category> listaAdapter;
    private List<Category> listaCategorias;

    private ActionMode actionMode;
    private int        posicaoSelecionada = -1;
    private View viewSelecionada;

    private static final int REQUEST_NOVA_CATEGORIA    = 1;
    private static final int REQUEST_ALTERAR_CATEGORIA = 2;

    private ConstraintLayout layout;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_my_categories_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Category category = (Category) listViewCategories.getItemAtPosition(posicaoSelecionada);
            switch (item.getItemId()){
                case R.id.action_update_category:

                    CategoryActivity.alterar(MyCategoriesActivity.this, REQUEST_ALTERAR_CATEGORIA, category);
                    mode.finish();
                    return true;

                case R.id.action_delete_category:
                    excluirCategoria(category);

                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(viewSelecionada != null){
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelecionada = null;

            listViewCategories.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_categories);

        setTitle(getString(R.string.my_categories));

        listViewCategories = findViewById(R.id.listViewCategories);
        layout = findViewById(R.id.myCategoriesLayout);

        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id){
                posicaoSelecionada = position;
                Category category = (Category) listViewCategories.getItemAtPosition(posicaoSelecionada);
                CategoryActivity.alterar(MyCategoriesActivity.this, REQUEST_ALTERAR_CATEGORIA, category);
            }

        });

        listViewCategories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view,
                                           int position,
                                           long id){
                if(actionMode != null){
                    return false;
                }

                posicaoSelecionada = position;

                view.setBackgroundColor(Color.LTGRAY);

                viewSelecionada = view;

                listViewCategories.setEnabled(false);

                actionMode = startSupportActionMode(mActionModeCallback);

                return true;
            }
        });

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);

        carregaCategorias();
        registerForContextMenu(listViewCategories);

        changeColor();
    }

    private void excluirCategoria(final Category category){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + category.getCategoryName();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        MyImpressionsDatabase database =
                                                MyImpressionsDatabase.getDatabase(MyCategoriesActivity.this);

                                        database.categoryDao().delete(category);

                                        MyCategoriesActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(category);
                                            }
                                        });
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data){
        if ((requestCode == REQUEST_NOVA_CATEGORIA || requestCode == REQUEST_ALTERAR_CATEGORIA)
                && resultCode == Activity.RESULT_OK){

            carregaCategorias();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_category:
                CategoryActivity.novo(this, REQUEST_NOVA_CATEGORIA);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeColor(){
        layout.setBackgroundColor(PrincipalActivity.selectedColor);
    }

    private void carregaCategorias(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(MyCategoriesActivity.this);

                listaCategorias = database.categoryDao().queryAll();

                MyCategoriesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(MyCategoriesActivity.this, android.R.layout.simple_list_item_1, listaCategorias);

                        listViewCategories.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }
}
