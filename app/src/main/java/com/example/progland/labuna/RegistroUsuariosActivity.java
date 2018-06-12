package com.example.progland.labuna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//clase para agregar usuarios
public class RegistroUsuariosActivity extends AppCompatActivity {

    // Diálogo de progreso
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    //declaracion de variables para casteo
    public EditText inputUsuario;
    EditText inputContrasenna;
    Spinner inputPuesto;

    VariablesGlobales vg = VariablesGlobales.getInstance();//llamado de la clase variables glovales

    // url to para crear un usuario
    private static String url_create_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/create_usuarios.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuarios);

        // Editar texto
        //casteo de los componentes UI
         inputUsuario = (EditText) findViewById(R.id.edUsuario);
         inputContrasenna = (EditText) findViewById(R.id.edContrasena);
          inputPuesto = (Spinner) findViewById(R.id.spPuesto);

        // casteo de botones crear y ver
        Button btnCreateUser = (Button) findViewById(R.id.btnAgregarUsu);
        Button btnverUser = (Button) findViewById(R.id.btnVerusuarios);

        String[] letra = {"Administrador","Profesor","Tutor"};//vector con los tipos de usuarios
        //lleno el spinner con el vector anterios
        inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Mensaje("Registro de Usuarios");//mensaje de bienvenida

        //Mensaje(vg.getTipo());

        // botón clic evento

        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creando un nuevo usuario en el hilo de fondo
                new CreateNewUser().execute();
            }
        });
//boton ver usuarios y llamado de la actividad correspondiente
        btnverUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intento = new Intent(getApplicationContext(), VerUsuarios.class);
                startActivity(intento);
            }
        });

    }

    /**
     * Tarea asincrónica de fondo para CreateNewUser
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(RegistroUsuariosActivity.this);
            pDialog.setMessage("Creating User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * creando usuario
         * */
        protected String doInBackground(String... args) {
            String usuario = inputUsuario .getText().toString();
            String contrasenna = inputContrasenna.getText().toString();
            String tipo = inputPuesto.getSelectedItem().toString();

            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("usuario", usuario));
            params.add(new BasicNameValuePair("contrasenna", contrasenna));
            params.add(new BasicNameValuePair("tipo", tipo));

// obteniendo el objeto JSON
// Tenga en cuenta que crear url de producto acepta el método POST

                JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                        "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
//                    "POST", params);

            //verifique el logcat de la respuesta

            Log.d("Create Response", json.toString());

            try {


            // verificar la etiqueta de éxito
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // usuario creado con éxito
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                    Mensaje("usuario registrado");

                    // cerrando esta pantalla
                    finish();
                } else if(success == 2){
                    // no se pudo crear el producto
                    Mensaje("putooo");
                    Toast.makeText(getApplicationContext(), "kejfkjerfk", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        /**
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez hecho
            pDialog.dismiss();
        }

    }



    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} //Fin de la Clase Actividad Registro