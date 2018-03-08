package com.example.progland.labuna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class AgregarLABSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_labs);
        Mensaje("Agregar LABS");
    } // Fin del Oncreate de la Actividad Agregar LABS

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:47:39 p. m.] Fin de la Clase Actividad Agregar LABS