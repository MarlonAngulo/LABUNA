package com.example.progland.labuna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//clase para editar laboratorios
public class editarLABS extends AppCompatActivity {




    //******declaro variables de los campos de texto y botones******
    EditText txtNombre;
    EditText txtcantidad;
    EditText txtdetalle;
    EditText txtestado;
    Button btnSave;
    Button btnDelete;
//**********************************************************************
    String lid;//vqriable para manejar el id del laboratorio a editar

    // Progress Dialog
    private ProgressDialog pDialog;//declaracion del visual mientas se carga o realiza una accion

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // url para optener un laboratorio
    private static final String url_lab_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_laboratorios_details.php";

    // url para actualizar una LAB
    private static final String url_update_lab = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/update_laboratorios.php";

    // url para eliminar un LAB
    private static final String url_delete_lab = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/delete_laboratorios.php";

    // Nombre del nodo JSON***********************************************
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LAB = "laboratorio";
    private static final String TAG_LID = "lid";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_CANTIDAD = "cantidad";
    private static final String TAG_DETALLE = "detalle";
    private static final String TAG_ESTADO = "estado";
        //**************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_labs);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                // StrictMode se usa más comúnmente para detectar acceso accidental a disco o red en el hilo principal de la aplicación
                .penaltyLog().build());
        // boto guardar y eliminar
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // obtener los detalles de la LAB de la intención
        Intent i = getIntent();

        // Obteniendo la ID de lal LAB (lid) de la intención
        lid = i.getStringExtra(TAG_LID);

        // Obtener detalles completos de la lab en el hilo de fondo
        new GetUserDetails().execute();

        // botón Guardar clic evento
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // comenzando la tarea de fondo para actualizar la lab
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

        // Eliminar click evento
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // eliminando la lab en el hilo de fondo
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

    }

    /**
     * Tarea de fondo Async para obtener detalles completos del lab
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarLABS.this);
            pDialog.setMessage("Loading labs details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Obtener detalles de la lab en el hilo de fondo
         * */
        protected String doInBackground(String... params) {

            // actualizar la interfaz de usuario desde el hilo de fondo
            runOnUiThread(new Runnable() {
                public void run() {
                    // Compruebe la etiqueta de éxito
                    int success;
                    try {
                        // Parámetros de construcción
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("lid", lid));

                        System.out.println("lid: " +lid);
                        System.out.println("hola lleguee");


                        // obtener detalles de la lab al hacer una solicitud HTTP
                         // Tenga en cuenta que la URL de detalles de la lab usará la solicitud GET
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_lab_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // revisa tu registro para ver si hay respuesta
                        Log.d("Single Lab Details", json.toString());


                        // etiqueta json success

                        System.out.println(success);
                        if (success == 1) {
                            // recibió con éxito los detalles de la lab
                            JSONArray userObj = json
                                    .getJSONArray(TAG_LAB); // JSON Array

                            // obtener el primer objeto de lab desde JSON Array
                            JSONObject lab = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            //lab con este lid encontrado
                            // Editar texto
                            txtNombre = (EditText) findViewById(R.id.inputNombreLaboratorio);
                            txtcantidad = (EditText) findViewById(R.id.inputCantidaddePC);
                            txtdetalle = (EditText) findViewById(R.id.inputDetalle);
                            txtestado = (EditText) findViewById(R.id.inputEstadodellaboratorio);

                            // mostrar datos de lab en EditText
                            txtNombre.setText(lab.getString(TAG_NOMBRE));
                            txtcantidad.setText(lab.getString(TAG_CANTIDAD));
                            txtdetalle.setText(lab.getString(TAG_DETALLE));
                            txtestado.setText(lab.getString(TAG_ESTADO));


                        }else{
                            //LAB con lid no encontrado
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez que obtuvo todos los detalles
            pDialog.dismiss();
        }
    }

    /**
     * Tarea de fondo Async para guardar LAB Detalles
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarLABS.this);
            pDialog.setMessage("Saving lab ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * guardar lab
         * */
        protected String doInBackground(String... args) {

            // obtener datos actualizados de EditTexts
            String nombre = txtNombre.getText().toString();
            String cantidad = txtcantidad.getText().toString();
            String detalle = txtdetalle.getText().toString();
            String estado = txtestado.getText().toString();


            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_LID, lid));
            params.add(new BasicNameValuePair(TAG_NOMBRE, nombre));
            params.add(new BasicNameValuePair(TAG_CANTIDAD, cantidad));
            params.add(new BasicNameValuePair(TAG_DETALLE, detalle));
            params.add(new BasicNameValuePair(TAG_ESTADO, estado));

            // enviando datos modificados a través de una solicitud http
            // Tenga en cuenta que update lab url acepta el método POST
            JSONObject json = jsonParser.makeHttpRequest(url_update_lab,
                    "POST", params);

            // comprobar la etiqueta de éxito json
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // actualizado exitosamente
                    Intent i = getIntent();
                    // envíe el código de resultado 100 para notificar sobre la actualización de la lab
                    setResult(100, i);
                    finish();
                } else {
                    // no se pudo actualizar la lab
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez que la lab se haya actualizado
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Tarea asincrónica de fondo para eliminar lab
     * */
    class DeleteUser extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarLABS.this);
            pDialog.setMessage("Deleting LAB...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * eliminar lab
         * */
        protected String doInBackground(String... args) {

            // Compruebe la etiqueta de éxito
            int success;
            try {
                // Parámetros de construcción
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("lid", lid));
                System.out.println(params);
                // obtener detalles de la lab al hacer una solicitud HTTP
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_lab, "POST", params);

                    // revisa tu LG para obtener una respuesta json
                Log.d("Delete Lab", json.toString());

                // etiqueta json success
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // PC eliminado con éxito
                    // notifica actividad previa enviando el código 100
                    Intent i = getIntent();
                    // envíe el código de resultado 100 para notificar sobre la eliminación de la lab
                    setResult(100, i);
                    finish();
                }
//                else{
//                  Mensaje("El laboratorio no se puede eliminar ya que esta en alguna reservacion");
//                    //finish();
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez que la lab haya eliminado
            pDialog.dismiss();

        }

    }
    //evento para el mensaje
    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};


}
