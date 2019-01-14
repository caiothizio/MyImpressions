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
import android.widget.ListView;

import java.util.List;

import br.edu.utfpr.caiot.myimpressions.model.Establishment;
import br.edu.utfpr.caiot.myimpressions.persistence.MyImpressionsDatabase;
import br.edu.utfpr.caiot.myimpressions.utils.UtilsGUI;

public class MyEstablishmentsActivity extends AppCompatActivity {

    private ListView                    listViewEstablishments;
    private EstablishmentAdapter        listaAdapter;
    private List<Establishment> listaEstabelecimentos;

    private ActionMode actionMode;
    private int        posicaoSelecionada = -1;
    private View       viewSelecionada;

    private static final int REQUEST_NOVO_ESTABELECIMENTO    = 1;
    private static final int REQUEST_ALTERAR_ESTABELECIMENTO = 2;

    private ConstraintLayout layout;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_my_establishments_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Establishment establishment = (Establishment) listViewEstablishments.getItemAtPosition(posicaoSelecionada);
            switch (item.getItemId()){
                case R.id.action_update_establishment:

                    EstablishmentActivity.alterar(MyEstablishmentsActivity.this, REQUEST_ALTERAR_ESTABELECIMENTO, establishment);
                    mode.finish();
                    return true;

                case R.id.action_delete_establishment:
                    excluirEstabelecimento(establishment);

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

            listViewEstablishments.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_establishments);

        setTitle(getString(R.string.my_establishments));

        listViewEstablishments = findViewById(R.id.listViewEstablishments);
        layout = findViewById(R.id.myEstablishmentsLayout);

        listViewEstablishments.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id){
                posicaoSelecionada = position;
                Establishment establishment = (Establishment) listViewEstablishments.getItemAtPosition(posicaoSelecionada);
                EstablishmentActivity.alterar(MyEstablishmentsActivity.this, REQUEST_ALTERAR_ESTABELECIMENTO, establishment);
            }

        });

        listViewEstablishments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewEstablishments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

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

                listViewEstablishments.setEnabled(false);

                actionMode = startSupportActionMode(mActionModeCallback);

                return true;
            }
        });

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);

        carregaEstabelecimentos();
        registerForContextMenu(listViewEstablishments);

        changeColor();
    }

    private void excluirEstabelecimento(final Establishment establishment){

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + establishment.getName();

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
                                                MyImpressionsDatabase.getDatabase(MyEstablishmentsActivity.this);

                                        database.establishmentDao().delete(establishment);

                                        MyEstablishmentsActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listaAdapter.remove(establishment);
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
        if ((requestCode == REQUEST_NOVO_ESTABELECIMENTO || requestCode == REQUEST_ALTERAR_ESTABELECIMENTO)
                && resultCode == Activity.RESULT_OK){

            carregaEstabelecimentos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_establishments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_establishment:
                EstablishmentActivity.novo(this, REQUEST_NOVO_ESTABELECIMENTO);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeColor(){
        layout.setBackgroundColor(PrincipalActivity.selectedColor);
    }

    private void carregaEstabelecimentos(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MyImpressionsDatabase database = MyImpressionsDatabase.getDatabase(MyEstablishmentsActivity.this);

                listaEstabelecimentos = database.establishmentDao().queryAll();

                MyEstablishmentsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new EstablishmentAdapter(MyEstablishmentsActivity.this, listaEstabelecimentos);

                        listViewEstablishments.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }
}