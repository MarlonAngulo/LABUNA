package com.example.progland.labuna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AgregarLABSActivity extends AppCompatActivity {



    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    public EditText inputCodigo;
    EditText inputCantidadPCS;
    EditText inputEstado;
    EditText inputDetalle;

    // url to create new product
    private static String url_create_labs = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/create_laboratorios.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    VariablesGlobales vg = VariablesGlobales.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_labs);
        Mensaje("Agregar LABS");

        // Edit Text
        inputCodigo = (EditText) findViewById(R.id.edCodigoLAB);
        inputCantidadPCS = (EditText) findViewById(R.id.edCantidadPCSLab);
        inputEstado = (EditText) findViewById(R.id.edEstadoLabs);
        inputDetalle = (EditText) findViewById(R.id.edDetalleLAB);

        // Create button
        Button btnCreateLab = (Button) findViewById(R.id.btnagregarLABS);
        Button btnverlabs = (Button) findViewById(R.id.btnverlabs);

        //String[] letra = {"Administrador","Profesor","Tutor"};
        //inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        if(vg.getTipo().equals("A")){
        }else{
            btnverlabs.setVisibility(View.GONE);
        }


        // button click event
        btnCreateLab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewLAB().execute();
            }
        });

        btnverlabs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent VerLabo = new Intent(getApplicationContext(), New.class);
                startActivity(VerLabo);
            }
        });



    }

    /**
     * Background Async Task to CreateNewUser
     * */
    class CreateNewLAB extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(AgregarLABSActivity.this);
            pDialog.setMessage("Creating User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String nombre = inputCodigo .getText().toString();
            String cantidad = inputCantidadPCS.getText().toString();
            String estado = inputEstado.getText().toString();
            String detalle = inputDetalle.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombre", nombre));
            params.add(new BasicNameValuePair("cantidad", cantidad));
            params.add(new BasicNameValuePair("detalle", detalle));
            params.add(new BasicNameValuePair("estado", estado));

            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_create_labs,
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
                        Mensaje("laboratorio registrado");

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

} // [11:47:39 p. m.] Fin de la Clase Actividad Agregar LABS