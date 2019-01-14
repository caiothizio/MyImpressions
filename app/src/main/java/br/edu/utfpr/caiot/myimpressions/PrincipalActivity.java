package br.edu.utfpr.caiot.myimpressions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class PrincipalActivity extends AppCompatActivity {

    private static final String ARQUIVO = "br.edu.utfpr.caiot.myimpressions.COLOR_PREFERENCES";
    private static final String COLOR    = "COLOR";

    public static int selectedColor = Color.TRANSPARENT;

    private ConstraintLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        layout = findViewById(R.id.layoutPrincipal);

        loadColorPref();
    }

    public void loadColorPref(){
        SharedPreferences sp = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        selectedColor = sp.getInt(COLOR, selectedColor);
        changeColor();
    }

    private void changeColor(){
        layout.setBackgroundColor(selectedColor);
    }

    private void saveColor(int color){
        SharedPreferences sp = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(COLOR, color);
        editor.commit();

        selectedColor = color;
        changeColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        switch (id){
            case R.id.action_my_establishments:
                Intent launchNewIntent = new Intent(this,
                                                    MyEstablishmentsActivity.class);
                startActivityForResult(launchNewIntent,0);
                return true;

            case R.id.action_my_categories:
                launchNewIntent = new Intent(this,
                        MyCategoriesActivity.class);
                startActivityForResult(launchNewIntent,0);

                return true;

            case R.id.action_color:
                chooseColor();

                return true;

            case R.id.action_info:
                Intent intentInfo = new Intent(this, InfoActivity.class);
                startActivityForResult(intentInfo, 0);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseColor(){
        CharSequence options[] = new CharSequence[]{getString(R.string.blue), getString(R.string.red), getString(R.string.yellow), getString(R.string.no_color)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.choose_color);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    saveColor(Color.rgb(178, 186, 255));
                }else if(which == 1) {
                    saveColor(Color.rgb(255, 140, 139));
                }else if(which == 2){
                    saveColor(Color.rgb(255, 244, 160));
                }else{
                    saveColor(Color.TRANSPARENT);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}
