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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class editarReportesPCS extends AppCompatActivity {



    EditText txtNombreMarca;
    EditText txtCodigoArticulo;
    EditText txtCodigoLAB;
    EditText txtCodigoDetalle;
    Button btnSave;
    Button btnDelete;

    String cid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single pc url
    private static final String url_pc_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/get_computadoras_details.php";

    // url to update pc
    private static final String url_update_pc = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/update_computadoras.php";

    // url to delete pc
    private static final String url_delete_pc = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/delete_computadoras.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PC = "computadora";
    private static final String TAG_CID = "cid";
    private static final String TAG_MARCA = "marca";
    private static final String TAG_CODIGO = "codigo";
    private static final String TAG_DESCRIPCION = "descripcion";
    private static final String TAG_LAB = "lab";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reportes_pcs);

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
        cid = i.getStringExtra(TAG_CID);

        // Getting complete pc details in background thread
        new GetUserDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update pc
                new SaveUserDetails().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting pc in background thread
                new DeleteUser().execute();
                Intent in = new Intent(getApplicationContext(),
                        VerUsuarios.class);
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
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Loading pc details. Please wait...");
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
                        params.add(new BasicNameValuePair("cid", cid));

                        System.out.println("cid: " +cid);
                        System.out.println("hola lleguee");


                        // getting pc details by making HTTP request
                        // Note that pc details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_pc_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // check your log for json response
                        Log.d("Single Pc Details", json.toString());


                        // json success tag

                        System.out.println(success);
                        if (success == 1) {
                            // successfully received pc details
                            JSONArray userObj = json
                                    .getJSONArray(TAG_PC); // JSON Array

                            // get first pc object from JSON Array
                            JSONObject pc = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            // pc with this cid found
                            // Edit Text
                            txtNombreMarca = (EditText) findViewById(R.id.inputNombreArticulo);
                            txtCodigoArticulo = (EditText) findViewById(R.id.inputCodigoArticulo);
                            txtCodigoLAB = (EditText) findViewById(R.id.inputCodigoLAB);
                            txtCodigoDetalle = (EditText) findViewById(R.id.inputDetalle);

                            // display pc data in EditText
                            txtNombreMarca.setText(pc.getString(TAG_MARCA));
                            txtCodigoArticulo.setText(pc.getString(TAG_CODIGO));
                            txtCodigoDetalle.setText(pc.getString(TAG_DESCRIPCION));
                            txtCodigoLAB.setText(pc.getString(TAG_LAB));


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
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Saving pc ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving pc
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String marca = txtNombreMarca.getText().toString();
            String codigo = txtCodigoArticulo.getText().toString();
            String descripcion = txtCodigoDetalle.getText().toString();
            String lab = txtCodigoLAB.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CID, cid));
            params.add(new BasicNameValuePair(TAG_MARCA, marca));
            params.add(new BasicNameValuePair(TAG_CODIGO, codigo));
            params.add(new BasicNameValuePair(TAG_DESCRIPCION, descripcion));
            params.add(new BasicNameValuePair(TAG_LAB, lab));

            // sending modified data through http request
            // Notice that update pc url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_pc,
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
            pDialog = new ProgressDialog(editarReportesPCS.this);
            pDialog.setMessage("Deleting User...");
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
                params.add(new BasicNameValuePair("cid", cid));

                // getting pc details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_pc, "POST", params);

                // check your log for json response
                Log.d("Delete User", json.toString());

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

}
