package com.example.progland.labuna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//clase para el menu
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton imageButton;
    VariablesGlobales vg = VariablesGlobales.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // imageButton = (ImageButton) findViewById(R.id.btnimgNotificacion);





        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Mensaje("Menu");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(vg.getTipo().equals("A")){
        }else{
            fab.setVisibility(View.GONE);
        }
        System.out.println("TIPO DE USUARIO: "+vg.getTipo() );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intento = new Intent(getApplicationContext(), RegistroUsuariosActivity.class);
                startActivity(intento);

            }
        });
        //addListenerOnButton();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //imageButton.setVisibility(View.GONE);

        TextView Mi_textview = (TextView) findViewById(R.id.txtVSaludo);
        Mi_textview.setText("bienvenido(a)  " + vg.getMitexto() + " !");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflar el menú; esto agrega elementos a la barra de acción si está presente.
//getMenuInflater().inflate(R.menu.menu, menú);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar clics del elemento de la barra de acción aquí. La barra de acción
        // maneja automáticamente los clics en el botón Inicio / Arriba, tan largo
    // cuando especifica una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Manejar los ítems de vista de navegación aquí.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {// Maneje la acción de la reservaciones
            Intent intento = new Intent(getApplicationContext(), FechaReservacionActivity.class);
            startActivity(intento);
            // Maneje la acción de la reportes
        } else if (id == R.id.nav_gallery) {
            Intent intento = new Intent(getApplicationContext(), ReportesPCSActivity.class);
            startActivity(intento);

        } else if (id == R.id.nav_slideshow) {// Maneje la acción de la agregar labs
            Intent intento = new Intent(getApplicationContext(), AgregarLABSActivity.class);
            startActivity(intento);

        } else if (id == R.id.nav_manage) {// Maneje la acción de creditos

            Intent intento = new Intent(getApplicationContext(), creditos.class);
            startActivity(intento);

        } else if (id == R.id.nav_share) {// Maneje la acción de pagina coto web
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse( "http://www.coto.una.ac.cr/" ));
            startActivity(i);

        } else if (id == R.id.nav_send) {// Maneje la acción de pagina de instrucciones

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse( "http://cursoplataformasmoviles.com/labuna/index.html" ));
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }
    public void addListenerOnButton() {



    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // Fin de la Clase Actividad Menu