package com.example.cadastrodetarefas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

public class FiltroActivity extends AppCompatActivity {
    public static final String NOME_ARQUIVO_PREFS = "preferencias";
    public static final String PREF_ORDENACAO = "ordenacao";
    public static final String PREF_USAR_ORDEM_DECRESCENTE = "usar_ordem_decrescente";
    public static final String PREF_FILTRO = "filtro";
    private boolean alterouValores = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        final SharedPreferences prefs = getSharedPreferences(NOME_ARQUIVO_PREFS, Context.MODE_PRIVATE);
        String ordenacao = prefs.getString(PREF_ORDENACAO, "_id");
        final boolean usarOrdemDecrescente = prefs.getBoolean(PREF_USAR_ORDEM_DECRESCENTE, false);
        String filtro = prefs.getString(PREF_FILTRO, "");
        RadioGroup rgOrdenacao = findViewById(R.id.rgOrdenacao);
        int id;
        if (MainActivity.CAMPO_ID.equals(ordenacao)) {
            id = R.id.rbCodigo;
        } else {
            id = R.id.rbDescricao;
        }
        rgOrdenacao.check(id);
        rgOrdenacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = prefs.edit();
                String ordenacao;
                if (checkedId == R.id.rbCodigo) {
                    ordenacao = MainActivity.CAMPO_ID;
                } else {
                    ordenacao = MainActivity.CAMPO_DESCRICAO;
                }
                editor.putString(PREF_ORDENACAO, ordenacao);
                editor.apply();
                alterouValores = true;
            }
        });
        CheckBox cbUsarOrdemDecrescente = findViewById(R.id.cbUsarOrdemDecrescente);
        cbUsarOrdemDecrescente.setChecked(usarOrdemDecrescente);
        cbUsarOrdemDecrescente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PREF_USAR_ORDEM_DECRESCENTE, isChecked);
                editor.apply();
                alterouValores = true;
            }
        });
        EditText edtFiltro = findViewById(R.id.edtFiltro);
        edtFiltro.setText(filtro);
        edtFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_FILTRO, s.toString());
                editor.apply();
                alterouValores = true;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onBackPressed() {
        if (alterouValores) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }
}


