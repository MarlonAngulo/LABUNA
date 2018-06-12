package com.example.progland.labuna;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//clase para editar pc
public class editarReportesPCS extends AppCompatActivity {


    //******declaro variables de los campos de texto y botones******
    EditText txtNombreMarca;
    EditText txtCodigoArticulo;
    EditText txtCodigoLAB;
    EditText txtCodigoDetalle;
    Button btnSave;
    Button btnDelete;
    //*************************************************************

    String cid;//vqriable para manejar el id del laboratorio a editar

    // Progress Dialog
    private ProgressDialog pDialog;//declaracion del visual mientas se carga o realiza una accion

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // url ara optener detalles de una pc
    private static final String url_pc_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/get_computadoras_details.php";

    // url para actualizar pc
    private static final String url_update_pc = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/update_computadoras.php";

    // url para eliminar pc
    private static final String url_delete_pc = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/delete_computadoras.php";

    //url para cargar laboratorios
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_all_laboratorios.php";

    // Nombre del nodo JSON***********************************************
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PC = "computadora";
    private static final String TAG_CID = "cid";
    private static final String TAG_MARCA = "marca";
    private static final String TAG_CODIGO = "codigo";
    private static final String TAG_DESCRIPCION = "descripcion";
    private static final String TAG_LAB = "lab";
    private static final String TAG_labs = "laboratorios";
    private static final String TAG_LID = "lid";
    private static final String TAG_Name = "nombre";
    //**************************************************************
    private ArrayList<laboratorios> categoriesList;

    private Spinner spinnerFE;

    ArrayList<HashMap<String, String>> LabsList;

    JSONArray labs = null;
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reportes_pcs);
        LabsList = new ArrayList<HashMap<String, String>>();
        categoriesList = new ArrayList<laboratorios>();

        spinnerFE = (Spinner) findViewById(R.id.spinnerDos);
        spinnerFE.setOnItemSelectedListener(this);
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
        cid = i.getStringExtra(TAG_CID);

        // Obtener detalles completos de la PC en el hilo de fondo
        new GetUserDetails().execute();


        // botón Guardar clic evento
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // comenzando la tarea de fondo para actualizar la PC
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
            }
        });

        // Eliminar click evento
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // eliminando la pc en el hilo de fondo
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
            }
        });
        // Obtener detalles completos del spinner en el hilo de fondo
        new LoadAlllabs().execute();

    }
    /**
     * Metodo populateSpinner, Realiza la funcion de cargar el spinner con los laboratorios extrahídos
     * en el JSON.
     */
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

//        txtCategory.setText("");
//
        for (int i = 0; i < categoriesList.size(); i++) {
            lables.add(Integer.toString(categoriesList.get(i).getId())+" "+categoriesList.get(i).getNombre());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerFE.setAdapter(spinnerAdapter);
    }

    /**
     * Clase encargada de cargar los labs que luego se mostraran en el spinner.
     */
    public class LoadAlllabs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Cargando labs..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(url_all_labs, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);
            System.out.println(json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        System.out.println("entre");
                        JSONArray laboratorios = jsonObj
                                .getJSONArray("laboratorios");
                        System.out.println("entre 2");

                        for (int i = 0; i < laboratorios.length(); i++) {
                            JSONObject catObj = (JSONObject) laboratorios.get(i);
                            laboratorios cat = new laboratorios(catObj.getInt("lid"),
                                    catObj.getString("nombre"));
                            System.out.println(cat.getId());
                            categoriesList.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();

            //  Revisar();
        }



    }

    /**
     * Tarea de fondo Async para obtener detalles completos de pc
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Loading pc details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Obtener detalles de la pc en el hilo de fondo
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
                        params.add(new BasicNameValuePair("cid", cid));

                        System.out.println("cid: " +cid);
                        System.out.println("hola lleguee");


                        // obtener detalles de la PC al hacer una solicitud HTTP
                        // Tenga en cuenta que la URL de detalles de la PC usará la solicitud GET
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_pc_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // revisa tu registro para ver si hay respuesta
                        Log.d("Single Pc Details", json.toString());


                        // etiqueta json success

                        System.out.println(success);
                        if (success == 1) {
                            // recibió con éxito los detalles de la PC
                            JSONArray userObj = json
                                    .getJSONArray(TAG_PC); // JSON Array

                            //obtener el primer objeto de PC desde JSON Array
                            JSONObject pc = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            //PC con este cid encontrado
                            // Editar texto
                            txtNombreMarca = (EditText) findViewById(R.id.inputNombreArticulo);
                            txtCodigoArticulo = (EditText) findViewById(R.id.inputCodigoArticulo);
                            txtCodigoLAB = (EditText) findViewById(R.id.inputCodigoLAB);
                            txtCodigoDetalle = (EditText) findViewById(R.id.inputDetalle);

                            //  mostrar datos de pc en EditText
                            txtNombreMarca.setText(pc.getString(TAG_MARCA));
                            txtCodigoArticulo.setText(pc.getString(TAG_CODIGO));
                            txtCodigoDetalle.setText(pc.getString(TAG_DESCRIPCION));
                            txtCodigoLAB.setText(pc.getString(TAG_LAB));


                        }else{
                            // LAB con lid no encontrado
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
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Saving pc ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * guardar pc
         * */
        protected String doInBackground(String... args) {

            // obtener datos actualizados de EditTexts
            String marca = txtNombreMarca.getText().toString();
            String codigo = txtCodigoArticulo.getText().toString();
            String descripcion = txtCodigoDetalle.getText().toString();
            String lab =  (String) spinnerFE.getSelectedItem();
            lab = lab.replace(" ", "☺");
            String []  labid = lab.split("☺");


            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CID, cid));
            params.add(new BasicNameValuePair(TAG_MARCA, marca));
            params.add(new BasicNameValuePair(TAG_CODIGO, codigo));
            params.add(new BasicNameValuePair(TAG_DESCRIPCION, descripcion));
            params.add(new BasicNameValuePair(TAG_LAB, labid[0]));

            // enviando datos modificados a través de una solicitud http
            // Tenga en cuenta que update pc url acepta el método POST
            JSONObject json = jsonParser.makeHttpRequest(url_update_pc,
                    "POST", params);

            // comprobar la etiqueta de éxito json
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // actualizado exitosamente
                    Intent i = getIntent();
                    //  envíe el código de resultado 100 para notificar sobre la actualización de la PC

                    setResult(100, i);
                    finish();
                } else {
                    // no se pudo actualizar la pc
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         *  Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez que la pc se haya actualizado
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
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Deleting User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * eliminar pc
         * */
        protected String doInBackground(String... args) {

            // Compruebe la etiqueta de éxito
            int success;
            try {
                // Parámetros de construcción
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("cid", cid));

                // obtener detalles de la PC al hacer una solicitud HTTP
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_pc, "POST", params);

                // revisa tu LG para obtener una respuesta json
                Log.d("Delete User", json.toString());

                // etiqueta json success
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // PC eliminado con éxito
                    // notifica actividad previa enviando el código 100
                    Intent i = getIntent();
                    // envíe el código de resultado 100 para notificar sobre la eliminación de la PC

                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }



        /**
         *Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez que la PC haya eliminado
            pDialog.dismiss();

        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long lid) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Selected" ,
                Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
