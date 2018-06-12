package com.example.progland.labuna;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import Adaptador.AdaptadorTitulares;
import Personal.Titular;

public class DisponibilidadLABSActivity extends Activity {
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibilidad_labs);
        lista = (ListView)findViewById(R.id.lista);
        Titular[] args = new Titular[]
                {
                        new Titular("Laboratorio 1", "Disponible", R.drawable.logo),
                        new Titular("Laboratorio 2", "Reservado por Marlon y JIandro", R.drawable.logo),
                        new Titular("Laboratorio 3", "Disponible", R.drawable.logo),
                        new Titular("Laboratorio 4", "Reservado por Derian", R.drawable.logo),

                };
        AdaptadorTitulares adap = new AdaptadorTitulares(this, args);
        lista.setAdapter(adap);

        Mensaje("Disponibilidad de Laboratorios");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_menu_drawer, menu);
        return true;
    }


    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

}
