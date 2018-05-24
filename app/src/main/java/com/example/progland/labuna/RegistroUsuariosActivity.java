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


public class RegistroUsuariosActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    public EditText inputUsuario;
    EditText inputContrasenna;
    Spinner inputPuesto;

    VariablesGlobales vg = VariablesGlobales.getInstance();

    // url to create new product
    private static String url_create_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/create_usuarios.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuarios);

        // Edit Text
         inputUsuario = (EditText) findViewById(R.id.edUsuario);
         inputContrasenna = (EditText) findViewById(R.id.edContrasena);
          inputPuesto = (Spinner) findViewById(R.id.spPuesto);

        // Create button
        Button btnCreateUser = (Button) findViewById(R.id.btnAgregarUsu);
        Button btnverUser = (Button) findViewById(R.id.btnVerusuarios);

        String[] letra = {"Administrador","Profesor","Tutor"};
        inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        Mensaje("Registro de Usuarios");

        //Mensaje(vg.getTipo());

        // button click event

        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewUser().execute();
            }
        });

        btnverUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intento = new Intent(getApplicationContext(), VerUsuarios.class);
                startActivity(intento);
            }
        });

    }

    /**
     * Background Async Task to CreateNewUser
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String usuario = inputUsuario .getText().toString();
            String contrasenna = inputContrasenna.getText().toString();
            String tipo = inputPuesto.getSelectedItem().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("usuario", usuario));
            params.add(new BasicNameValuePair("contrasenna", contrasenna));
            params.add(new BasicNameValuePair("tipo", tipo));

            // getting JSON Object
            // Note that create product url accepts POST method

                JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                        "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
//                    "POST", params);

            // check log cat fro response
//AQUI SE CAE EN ESTA LINEA SIGUIENTE



            Log.d("Create Response", json.toString());

            try {


            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                    Mensaje("usuario registrado");

                    // closing this screen
                    finish();
                } else if(success == 2){
                    // failed to create product
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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }



    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:05:08 p. m.] Fin de la Clase Actividad Registro