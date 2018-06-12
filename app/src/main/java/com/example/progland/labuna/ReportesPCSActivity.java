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
//clase para realizar reportes de pc
public class ReportesPCSActivity extends AppCompatActivity {


    // Diálogo de progreso
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    //declaracion de variables para casteo
    public EditText inputNombre;
    EditText inputCodigoAr;
    EditText inputCodigoLab;
    EditText inputDetalle;
    VariablesGlobales vg = VariablesGlobales.getInstance();//llamado de la clase variables glovales
    // url para crear un nuevo reporte
    private static String url_create_pcs = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/create_computadoras.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_pcs);
        Mensaje("Reportes PCS");

        // Editar texto
        //casteo de los componentes UI
        inputNombre = (EditText) findViewById(R.id.edCodigoLAB);
        inputCodigoAr = (EditText) findViewById(R.id.edCantidadPCSLab);
        inputCodigoLab = (EditText) findViewById(R.id.edEstadoLabs);
        inputDetalle = (EditText) findViewById(R.id.edDetalleLAB);
        // casteo de botones crear y ver
        Button btnCreatePcs = (Button) findViewById(R.id.btnreportar);
        Button btvercp = (Button) findViewById(R.id.btnvercomputadora);
        // Create button

        //funcion para los permisos si es admin o profe o tutor
        if(vg.getTipo().equals("A")){
        }else{
            btvercp.setVisibility(View.GONE);
        }


        //String[] letra = {"Administrador","Profesor","Tutor"};
        //inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));


        //  botón clic evento
        btnCreatePcs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //creando un nuevo usuario en el hilo de fondo
                new CreateNewPCS().execute();
            }
        });
        //boton ver reportes y llamado de la actividad correspondiente
        btvercp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent VerPc = new Intent(getApplicationContext(), VerComputadorasActivity.class);
                startActivity(VerPc);
            }
        });



    }

    /**
     * Tarea asincrónica de fondo para CreateNewUser
     * */
    class CreateNewPCS extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ReportesPCSActivity .this);
            pDialog.setMessage("Creando Reporte..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * creando usuario
         * */
        protected String doInBackground(String... args) {
            String marca = inputNombre.getText().toString();
            String codigo = inputCodigoAr.getText().toString();
            String lab = inputCodigoLab.getText().toString();
            String descripcion = inputDetalle.getText().toString();

            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("marca", marca));
            params.add(new BasicNameValuePair("codigo", codigo));
            params.add(new BasicNameValuePair("descripcion", descripcion));
            params.add(new BasicNameValuePair("lab", lab));

// obteniendo el objeto JSON
// Tenga en cuenta que crear url de producto acepta el método POST

            JSONObject json = jsonParser.makeHttpRequest(url_create_pcs,
                    "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_pcs,
//                    "POST", params);

//verifique el logcat de la respuesta



            Log.d("Create Response", json.toString());

            try {


                // cverificar la etiqueta de éxito
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // pc creado con éxito
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                        Mensaje("Reporte registrado");

                        // cerrando esta pantalla
                        finish();
                    } else if(success == 2){
                        //  no se pudo crear el producto
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
         *  Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez hecho
            pDialog.dismiss();
        }

    }




    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // Fin de la Clase Actividad Reportes PCS
