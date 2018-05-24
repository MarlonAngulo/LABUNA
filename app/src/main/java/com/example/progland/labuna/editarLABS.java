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

public class editarLABS extends AppCompatActivity {




    EditText txtNombre;
    EditText txtcantidad;
    EditText txtdetalle;
    EditText txtestado;
    Button btnSave;
    Button btnDelete;

    String lid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single pc url
    private static final String url_lab_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_laboratorios_details.php";

    // url to update pc
    private static final String url_update_lab = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/update_laboratorios.php";

    // url to delete pc
    private static final String url_delete_lab = "http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/delete_laboratorios.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LAB = "laboratorio";
    private static final String TAG_LID = "lid";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_CANTIDAD = "cantidad";
    private static final String TAG_DETALLE = "detalle";
    private static final String TAG_ESTADO = "estado";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_labs);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                // StrictMode is most commonly used to catch accidental disk or network access on the application's main thread
                .penaltyLog().build());
        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting pc details from intent
        Intent i = getIntent();

        // getting pc id (cid) from intent
        lid = i.getStringExtra(TAG_LID);

        // Getting complete pc details in background thread
        new GetUserDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update pc
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting pc in background thread
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        New.class);
            }
        });

    }

    /**
     * Background Async Task to Get complete pc details
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Getting pc details in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("lid", lid));

                        System.out.println("lid: " +lid);
                        System.out.println("hola lleguee");


                        // getting pc details by making HTTP request
                        // Note that pc details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_lab_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // check your log for json response
                        Log.d("Single Lab Details", json.toString());


                        // json success tag

                        System.out.println(success);
                        if (success == 1) {
                            // successfully received pc details
                            JSONArray userObj = json
                                    .getJSONArray(TAG_LAB); // JSON Array

                            // get first pc object from JSON Array
                            JSONObject lab = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            // pc with this cid found
                            // Edit Text
                            txtNombre = (EditText) findViewById(R.id.inputNombreLaboratorio);
                            txtcantidad = (EditText) findViewById(R.id.inputCantidaddePC);
                            txtdetalle = (EditText) findViewById(R.id.inputDetalle);
                            txtestado = (EditText) findViewById(R.id.inputEstadodellaboratorio);

                            // display pc data in EditText
                            txtNombre.setText(lab.getString(TAG_NOMBRE));
                            txtcantidad.setText(lab.getString(TAG_CANTIDAD));
                            txtdetalle.setText(lab.getString(TAG_DETALLE));
                            txtestado.setText(lab.getString(TAG_ESTADO));


                        }else{
                            // pc with cid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to  Save pc Details
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Saving pc
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String nombre = txtNombre.getText().toString();
            String cantidad = txtcantidad.getText().toString();
            String detalle = txtdetalle.getText().toString();
            String estado = txtestado.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_LID, lid));
            params.add(new BasicNameValuePair(TAG_NOMBRE, nombre));
            params.add(new BasicNameValuePair(TAG_CANTIDAD, cantidad));
            params.add(new BasicNameValuePair(TAG_DETALLE, detalle));
            params.add(new BasicNameValuePair(TAG_ESTADO, estado));

            // sending modified data through http request
            // Notice that update pc url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_lab,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about pc update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update pc
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once pc uupdated
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete User
     * */
    class DeleteUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Deleting pc
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("lid", lid));
                System.out.println(params);
                // getting pc details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_lab, "POST", params);

                // check your log for json response
                Log.d("Delete Lab", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // pc successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about pc deletion
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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once pc deleted
            pDialog.dismiss();

        }


    }
    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};


}