package com.example.progland.labuna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FechaReservacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_reservacion);
        Mensaje(" Reservacion de los LABS");

        final Button boton1 = (Button)findViewById(R.id.btnsiguiente);
        //boton1.setText("No has pulsado el boton");
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(getApplicationContext(), DisponibilidadLABSActivity.class);
                startActivity(intento);
               // boton1.setText("Has pulsado el boton "+clicks+" veces");
            }
        });
    } // Fin del Oncreate

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

}
