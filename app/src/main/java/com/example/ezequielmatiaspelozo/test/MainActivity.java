package com.example.ezequielmatiaspelozo.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvNotas;
    private Button boton;
    private Button botonBorrar;
    private  EditText miTexto;
    private SQLiteDatabase db;
    private BasedeDatos b;
    private ContentValues nuevoRegistro;
    private TextView salida;
    private String palabra = "";

    // agragar en esta lista
    private ArrayList<String> notas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //busco mi listView, mi boton y mi editText
        lvNotas = (ListView) findViewById(R.id.MiListaDeCompras);
        boton = (Button) findViewById(R.id.Boton);
        botonBorrar = (Button) findViewById(R.id.BotonBorrar);
        miTexto = (EditText) findViewById(R.id.ingreso);
        salida = (TextView) findViewById(R.id.TextoDeSalida);

        // creamos la base de datos
        b = new BasedeDatos(this, "Ejemplo", null, 2);
        // la abrimos en modo escritura
        db = b.getWritableDatabase();


        //funcionalida del boton de borrar
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // limpiamos los TextView
                db = b.getWritableDatabase();
                db.beginTransaction();
                try {
                    // Order of deletions is important when foreign key relationships exist.
                    db.delete("Ejemplo", null, null);

                    db.setTransactionSuccessful();
                } catch (Exception e) {

                } finally {
                    db.endTransaction();
                }
            }


        });


        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String texto = miTexto.getText().toString();
                notas.add(texto);
                miTexto.setText("");
                // para la base
                if (!texto.isEmpty()) {
                    // inicializo ContentValue
                    nuevoRegistro = new ContentValues();
                    // insertamos los datos en el ContentValues
                    nuevoRegistro.put("nombre", texto);
                    nuevoRegistro.put("cantidad", 1);
                    // insertamos en la base
                    db.insert("Ejemplo", null, nuevoRegistro);
                    nuevoRegistro.clear();
                    }
            }
        });
    //continuar aca para leer la base y lo concateno en un String para probar
        db = b.getReadableDatabase();
        //utilizo un cursor para recorrer mi base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM Ejemplo",null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    notas.add(cursor.getString(cursor.getColumnIndex("nombre")));
                    //estoy probando que se graban los ingrsos en la base
                    palabra += cursor.getString(cursor.getColumnIndex("nombre"));

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        // mostramos en el TextView de prueba

            salida.setText(palabra);





        //creo mi array adapter simple
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notas);
        //seteo adapter
        lvNotas.setAdapter(itemsAdapter);



    }
}
