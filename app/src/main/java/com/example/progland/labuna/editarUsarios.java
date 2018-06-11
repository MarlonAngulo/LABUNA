package com.example.progland.labuna;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//clase para editar pc
public class editarUsarios extends Activity {


    //******declaro variables de los campos de texto y botones******
    EditText txtUsuario;
    EditText txtContrasenna;
    EditText txtTipo;
    Button btnSave;
    Button btnDelete;
    //*************************************************************

    String uid;//vqriable para manejar el id del laboratorio a editar

    // Progress Dialog
    private ProgressDialog pDialog;//declaracion del visual mientas se carga o realiza una accion

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // url para optener el detalle del usuario
    private static final String url_user_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/get_usuarios_details.php";

    // url para actualizar el usuario
    private static final String url_update_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/update_usuarios.php";

    // url para eliminar usuario
    private static final String url_delete_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/delete_usuarios.php";

    // Nombre del nodo JSON***********************************************
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "usuario";
    private static final String TAG_UID = "uid";
    private static final String TAG_USUARIO = "usuario";
    private static final String TAG_CONTRASEÑA = "contrasenna";
    private static final String TAG_TIPO = "tipo";
//**************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usarios);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                // StrictMode se usa más comúnmente para detectar acceso accidental a disco o red en el hilo principal de la aplicación

                .penaltyLog().build());
        // boto guardar y eliminar
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // obtener los detalles de la Usario de la intención
        Intent i = getIntent();

        // Obteniendo la ID de la Usario (uid) de la intención
        uid = i.getStringExtra(TAG_UID);

        // Obtener detalles completos de la Usario en el hilo de fondo
        new GetUserDetails().execute();

        // botón Guardar clic evento
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // comenzando la tarea de fondo para actualizar la Usario
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
            }
        });

        // Eliminar click evento
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // eliminando la Usario en el hilo de fondo
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
            }
        });

    }

    /**
     * Tarea de fondo Async para obtener detalles completos de Usario
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarUsarios.this);
            pDialog.setMessage("Loading user details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Obtener detalles de la Usario en el hilo de fondo
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
                        params.add(new BasicNameValuePair("uid", uid));

                        System.out.println("uid: " +uid);
                        System.out.println("hola lleguee");


                        // obtener detalles de la Usario al hacer una solicitud HTTP
                        // Tenga en cuenta que la URL de detalles de la Usario usará la solicitud GET
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_user_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // revisa tu registro para ver si hay respuesta
                        Log.d("Single User Details", json.toString());


                        // etiqueta json success

                        System.out.println(success);
                        if (success == 1) {
                            //recibió con éxito los detalles de la Usario
                            JSONArray userObj = json
                                    .getJSONArray(TAG_USER); // JSON Array

                            // obtener el primer objeto de PC desde JSON Array
                            JSONObject user = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            //Usario con este cid encontrado
                            // Editar texto
                            txtUsuario = (EditText) findViewById(R.id.inputUsuario);
                            txtContrasenna = (EditText) findViewById(R.id.inputContrasena);
                            txtTipo = (EditText) findViewById(R.id.inputTipo);

                            //  mostrar datos de Usario en EditText
                            txtUsuario.setText(user.getString(TAG_USUARIO));
                            txtContrasenna.setText(user.getString(TAG_CONTRASEÑA));
                            txtTipo.setText(user.getString(TAG_TIPO));

                        }else{
                            // Usario con uid no encontrado
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
     * Tarea de fondo Async para guardar Usario Detalles
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarUsarios.this);
            pDialog.setMessage("Saving user ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * guardar reservacion
         * */
        protected String doInBackground(String... args) {

            // obtener datos actualizados de EditTexts
            String usuario = txtUsuario.getText().toString();
            String contrasenna = txtContrasenna.getText().toString();
            String tipo = txtTipo.getText().toString();

            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_UID, uid));
            params.add(new BasicNameValuePair(TAG_USUARIO, usuario));
            params.add(new BasicNameValuePair(TAG_CONTRASEÑA, contrasenna));
            params.add(new BasicNameValuePair(TAG_TIPO, tipo));

            // enviando datos modificados a través de una solicitud http
            // Tenga en cuenta que update Usario url acepta el método POST
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // comprobar la etiqueta de éxito json
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // actualizado exitosamente
                    Intent i = getIntent();
                    //  envíe el código de resultado 100 para notificar sobre la actualización de la Usario

                    setResult(100, i);
                    finish();
                } else {
                    //no se pudo actualizar la Usario
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
            // descartar el diálogo una vez que la Usario se haya actualizado
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Tarea asincrónica de fondo para eliminar Usario
     * */
    class DeleteUser extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarUsarios.this);
            pDialog.setMessage("Deleting User...");
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
                params.add(new BasicNameValuePair("uid", uid));

                // obtener detalles de la Usario al hacer una solicitud HTTP
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_user, "POST", params);

                // crevisa tu LG para obtener una respuesta json
                Log.d("Delete User", json.toString());

                // etiqueta json success
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // PC eliminado con éxito
                    // notifica actividad previa enviando el código 100
                    Intent i = getIntent();
                    // envíe el código de resultado 100 para notificar sobre la eliminación de la Usario
                    setResult(100, i);
                    finish();
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
            // escartar el diálogo una vez que la Usario haya eliminado
            pDialog.dismiss();

        }


    }


}
