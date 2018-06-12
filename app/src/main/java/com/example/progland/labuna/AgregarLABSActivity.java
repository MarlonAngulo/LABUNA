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
//clase para agregar laboratorios
public class AgregarLABSActivity extends AppCompatActivity {



    // Diálogo de progreso
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    //declaracion de variables para casteo
    public EditText inputCodigo;
    EditText inputCantidadPCS;
    EditText inputEstado;
    EditText inputDetalle;

    // url para crear labs
    private static String url_create_labs = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/create_laboratorios.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";
    VariablesGlobales vg = VariablesGlobales.getInstance();//llamado de la clase variables glovales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_labs);
        Mensaje("Agregar LABS");//llamado del metodo para mensajes

        // Editar texto
        //casteo de los componentes UI
        inputCodigo = (EditText) findViewById(R.id.edCodigoLAB);
        inputCantidadPCS = (EditText) findViewById(R.id.edCantidadPCSLab);
        inputEstado = (EditText) findViewById(R.id.edEstadoLabs);
        inputDetalle = (EditText) findViewById(R.id.edDetalleLAB);

        // casteo de botones crear y ver
        Button btnCreateLab = (Button) findViewById(R.id.btnagregarLABS);
        Button btnverlabs = (Button) findViewById(R.id.btnverlabs);


        //funcion para ocultar las acciones dependiendo de los permisos tipo de usuario
        if(vg.getTipo().equals("A")){
        }else{
            btnverlabs.setVisibility(View.GONE);
        }


        // button click event
        btnCreateLab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creando un nuevo lab en el hilo de fondo
                new CreateNewLAB().execute();
            }
        });
//boton ver reportes y llamado de la actividad correspondiente
        btnverlabs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent VerLabo = new Intent(getApplicationContext(), New.class);
                startActivity(VerLabo);
            }
        });



    }

    /**
     * Tarea asincrónica de fondo para CreateNewUser
     * */
    class CreateNewLAB extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
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
         * creando lab
         * */
        protected String doInBackground(String... args) {
            String nombre = inputCodigo .getText().toString();
            String cantidad = inputCantidadPCS.getText().toString();
            String estado = inputEstado.getText().toString();
            String detalle = inputDetalle.getText().toString();

            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombre", nombre));
            params.add(new BasicNameValuePair("cantidad", cantidad));
            params.add(new BasicNameValuePair("detalle", detalle));
            params.add(new BasicNameValuePair("estado", estado));

// obteniendo el objeto JSON
// Tenga en cuenta que crear url de producto acepta el método POST

            JSONObject json = jsonParser.makeHttpRequest(url_create_labs,
                    "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
//                    "POST", params);

//verifique el logcat de la respuesta



            Log.d("Create Response", json.toString());

            try {


                //cverificar la etiqueta de éxito
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        //  lab creado con éxito
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                        Mensaje("laboratorio registrado");

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

} //  Fin de la Clase Actividad Agregar LABS