package com.example.cadastrodetarefas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String NOME_BD = "cadastro_tarefas";
    public static final String NOME_TABELA = "tarefa";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_DESCRICAO = "descricao";
    public static final String[] CAMPOS_TAREFA = {CAMPO_ID, CAMPO_DESCRICAO};
    public static final int FILTRO_REQUEST_CODE = 0;

    private EditText edtCodigo;
    private EditText edtDescricao;
    private ListView listaTarefas;
    private SQLiteDatabase database;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtCodigo = findViewById(R.id.edtCodigo);
        edtDescricao = findViewById(R.id.edtDescricao);
        listaTarefas = findViewById(R.id.listaTarefas);

        database = openOrCreateDatabase(NOME_BD, Context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + " (" +
                CAMPO_ID + " INTEGER PRIMARY KEY, " +
                CAMPO_DESCRICAO + " TEXT);");
        int[] elementos = {android.R.id.text1, android.R.id.text2};
        Cursor cursor = obterTarefas();
        adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, CAMPOS_TAREFA, elementos);
        listaTarefas.setAdapter(adapter);
        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carregarTarefa(String.valueOf(id));
            }
        });
    }

    private Cursor obterTarefas() {
        SharedPreferences prefs = getSharedPreferences(FiltroActivity.NOME_ARQUIVO_PREFS, Context.MODE_PRIVATE);
        String ordenacao = prefs.getString(FiltroActivity.PREF_ORDENACAO, CAMPO_ID);
        boolean usarOrdemDecrescente = prefs.getBoolean(FiltroActivity.PREF_FILTRO, false);
        String filtro = prefs.getString(FiltroActivity.PREF_FILTRO, "");

        String where = null;
        if (!filtro.trim().isEmpty()) {
            where = "UPPER (" + CAMPO_DESCRICAO + ") LIKE '" + filtro.toUpperCase() + "%'";
        }

        String orderBy = ordenacao;
        if (usarOrdemDecrescente) {
            orderBy += " DESC";
        }
        return database.query(NOME_TABELA, CAMPOS_TAREFA, where, null, null, null, orderBy);
    }

    private void carregarTarefa(String codigo) {
        Cursor c = database.query(NOME_TABELA, CAMPOS_TAREFA, CAMPO_ID + " = " + codigo, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            edtCodigo.setText(c.getString(0));
            edtDescricao.setText(c.getString(1));
        } else {
            Toast.makeText(this, R.string.registro_nao_encontrado, Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    private void atualizarLista() {
        Cursor cursor = obterTarefas();
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    public void limparCampos() {
        edtCodigo.setText("");
        edtDescricao.setText("");
        edtDescricao.setText("");
    }

    public void onSalvarClick(View v) {
        String descricao = edtDescricao.getText().toString();

        if (descricao.trim().isEmpty()) {
            Toast.makeText(this, "informe a descricao", Toast.LENGTH_SHORT).show();
            edtDescricao.requestFocus();
        } else {
            ContentValues values = new ContentValues();
            values.put(CAMPO_DESCRICAO, descricao);
            String codigo = edtCodigo.getText().toString();
            if (codigo.trim().isEmpty()) {
                database.insert(NOME_TABELA, null, values);
            } else {
                database.update(NOME_TABELA, values, CAMPO_ID + " = " + codigo, null);
            }
            limparCampos();
            atualizarLista();
        }
    }

    public void onExcluirClick(View v) {
        String codigo = edtCodigo.getText().toString().trim();
        if (codigo.isEmpty()) {
            edtCodigo.requestFocus();
            Toast.makeText(this, "codigo obrigatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        database.delete(NOME_TABELA, CAMPO_ID + " = " + codigo, null);
        limparCampos();
        atualizarLista();
    }

    public void onLimparCampos() {
        limparCampos();
    }

    public void onFiltroClick(View v) {
        Intent intent = new Intent(this, FiltroActivity.class);
        startActivityForResult(intent, FILTRO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FILTRO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            atualizarLista();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}