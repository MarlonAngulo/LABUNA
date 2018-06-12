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
//clase para editar pc

public class editarReservaciones extends AppCompatActivity {





    //******declaro variables de los campos de texto y botones******
    EditText txtFecha;
    EditText txtHorario;
    EditText txtLab;
    EditText txtUsuario;
    Button btnSave;
    Button btnDelete;
    //*************************************************************
    String rid;//vqriable para manejar el id del laboratorio a editar

    // Progress Dialog
    private ProgressDialog pDialog;//declaracion del visual mientas se carga o realiza una accion

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // url para optener detalle de una reservacion
    private static final String url_reserv_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/get_reservaciones_details.php";

    // url tpara actualizar reservacion
    private static final String url_update_reserv = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/update_reservaciones.php";

    // url para e,iminar reservacion
    private static final String url_delete_reserv = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/delete_reservaciones.php";

    // Nombre del nodo JSON***********************************************
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESERVACION = "reservacion";
    private static final String TAG_RID = "rid";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_HORARIO = "horario";
    private static final String TAG_LAB = "lab";
    private static final String TAG_USUARIO = "usuario";
    //**************************************************************


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reservaciones);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                // StrictMode se usa más comúnmente para detectar acceso accidental a disco o red en el hilo principal de la aplicación

                .penaltyLog().build());
        // boto guardar y eliminar
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnSave.setVisibility(View.GONE);
        //playButton.setVisibility(View.GONE)

        // obtener los detalles de Reservacion de la intención
        Intent i = getIntent();

        // Obteniendo la ID de la reservacion (rid) de la intención
        rid = i.getStringExtra(TAG_RID);

        // Obtener detalles completos de la reservacion en el hilo de fondo
        new GetUserDetails().execute();

        // botón Guardar clic evento
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // comenzando la tarea de fondo para actualizar la Reservacion
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

        // Eliminar click evento
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // eliminando la Reservacion en el hilo de fondo
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

    }

    /**
     * Tarea de fondo Async para obtener detalles completos de Reservacion
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarReservaciones.this);
            pDialog.setMessage("Loading reserv details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Obtener detalles de la Reservacion en el hilo de fondo
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
                        params.add(new BasicNameValuePair("rid", rid));

                        System.out.println("rid: " +rid);
                        System.out.println("hola lleguee");


                        // obtener detalles de la Reservacion al hacer una solicitud HTTP
                        // Tenga en cuenta que la URL de detalles de la Reservacion usará la solicitud GET
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_reserv_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // revisa tu registro para ver si hay respuesta
                        Log.d("Single Lab Details", json.toString());


                        // etiqueta json success

                        System.out.println(success);
                        if (success == 1) {
                            // recibió con éxito los detalles de la Reservacion
                            JSONArray userObj = json
                                    .getJSONArray(TAG_RESERVACION); // JSON Array

                            // obtener el primer objeto de Reservacion desde JSON Array
                            JSONObject reserv = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            //PC con este cid encontrado
                            // Editar texto
                            txtFecha = (EditText) findViewById(R.id.inputFechadeReservacion);
                            txtHorario = (EditText) findViewById(R.id.inputHorario);
                            txtLab = (EditText) findViewById(R.id.inputLaboratorioreservado);
                            txtUsuario = (EditText) findViewById(R.id.inputUsuarioquereservo);

                            // mostrar datos de Reservacion en EditText
                            txtFecha.setText(reserv.getString(TAG_FECHA));
                            txtHorario.setText(reserv.getString(TAG_HORARIO));
                            txtLab.setText(reserv.getString(TAG_LAB));
                            txtUsuario.setText(reserv.getString(TAG_USUARIO));


                        }else{
                            // reservacion con rid no encontrado
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
     * Tarea de fondo Async para guardar Reservacion Detalles
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarReservaciones.this);
            pDialog.setMessage("Saving lab ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * guardar reservacion
         * */
        protected String doInBackground(String... args) {

            // obtener datos actualizados de EditTexts
            String fecha = txtFecha.getText().toString();
            String horario = txtHorario.getText().toString();
            String lab = txtLab.getText().toString();
            String usuario = txtUsuario.getText().toString();


            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_RID, rid));
            params.add(new BasicNameValuePair(TAG_FECHA, fecha));
            params.add(new BasicNameValuePair(TAG_HORARIO, horario));
            params.add(new BasicNameValuePair(TAG_LAB, lab));
            params.add(new BasicNameValuePair(TAG_USUARIO, usuario));

            // enviando datos modificados a través de una solicitud http
            // Tenga en cuenta que update Reservacion url acepta el método POST
            JSONObject json = jsonParser.makeHttpRequest(url_update_reserv,
                    "POST", params);

            // comprobar la etiqueta de éxito json
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // actualizado exitosamente
                    Intent i = getIntent();
                    //  envíe el código de resultado 100 para notificar sobre la actualización de la Reservacion

                    setResult(100, i);
                    finish();
                } else {
                    // no se pudo actualizar la Reservacion
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
            // descartar el diálogo una vez que la Reservacion se haya actualizado
            pDialog.dismiss();
        }
    }
    /*****************************************************************
     * Tarea asincrónica de fondo para eliminar Reservacion
     * */
    class DeleteUser extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarReservaciones.this);
            pDialog.setMessage("Deleting LAB...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * eliminar reservacion
         * */
        protected String doInBackground(String... args) {

            // Compruebe la etiqueta de éxito
            int success;
            try {
                // Parámetros de construcción
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("rid", rid));
                System.out.println(params);
                // obtener detalles de la Reservacion al hacer una solicitud HTTP
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_reserv, "POST", params);

                // revisa tu LG para obtener una respuesta json
                Log.d("Delete Lab", json.toString());

                // etiqueta json success
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Reservacion eliminado con éxito
                    // notifica actividad previa enviando el código 100
                    Intent i = getIntent();
                    // envíe el código de resultado 100 para notificar sobre la eliminación de la Reservacion

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
            // descartar el diálogo una vez que la Reservacion haya eliminado
            pDialog.dismiss();

        }


    }
    //evento de mensaje
    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};



}
