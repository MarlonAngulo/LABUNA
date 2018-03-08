package com.example.progland.labuna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ReportesPCSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_pcs);
        Mensaje("Reportes PCS");
    } // Fin del Oncreate de la Actividad Reportes PCS

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:32:45 p. m.] Fin de la Clase Actividad Reportes PCS
