package com.example.progland.labuna;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class editarUsarios extends Activity {



    EditText txtUsuario;
    EditText txtContrasenna;
    EditText txtTipo;
    Button btnSave;
    Button btnDelete;

    String uid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single user url
    private static final String url_user_detials = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/get_usuarios_details.php";

    // url to update user
    private static final String url_update_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/update_usuarios.php";

    // url to delete user
    private static final String url_delete_user = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/delete_usuarios.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_UID = "uid";
    private static final String TAG_USUARIO = "usuario";
    private static final String TAG_CONTRASEÑA = "contraseña";
    private static final String TAG_TIPO = "tipo";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usarios);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting user details from intent
        Intent i = getIntent();

        // getting user id (uid) from intent
        uid = i.getStringExtra(TAG_UID);

        // Getting complete user details in background thread
        new GetUserDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update user
                new SaveUserDetails().execute();
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting user in background thread
                new DeleteUser().execute();
            }
        });

    }

    /**
     * Background Async Task to Get complete user details
     * */
    class GetUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Getting user details in background thread
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
                        params.add(new BasicNameValuePair("uid", uid));

                        System.out.println("uid: " +uid);
                        System.out.println("hola lleguee");


                        // getting user details by making HTTP request
                        // Note that user details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_user_detials, "GET", params);
                        success = json.getInt(TAG_SUCCESS);

                        System.out.println(success);
                        // check your log for json response
                        Log.d("Single User Details", json.toString());


                        // json success tag

                        System.out.println(success);
                        if (success == 1) {
                            // successfully received user details
                            JSONArray userObj = json
                                    .getJSONArray(TAG_USER); // JSON Array

                            // get first user object from JSON Array
                            JSONObject user = userObj.getJSONObject(0);
                            System.out.println("mmm: ");

                            // user with this uid found
                            // Edit Text
                            txtUsuario = (EditText) findViewById(R.id.inputUsuario);
                            txtContrasenna = (EditText) findViewById(R.id.inputContrasena);
                            txtTipo = (EditText) findViewById(R.id.inputTipo);

                            // display user data in EditText
                            txtUsuario.setText(user.getString(TAG_USUARIO));
                            txtContrasenna.setText(user.getString(TAG_CONTRASEÑA));
                            txtTipo.setText(user.getString(TAG_TIPO));

                        }else{
                            // user with uid not found
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
     * Background Async Task to  Save user Details
     * */
    class SaveUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
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
         * Saving user
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String usuario = txtUsuario.getText().toString();
            String contraseña = txtContrasenna.getText().toString();
            String tipo = txtTipo.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_UID, uid));
            params.add(new BasicNameValuePair(TAG_USUARIO, usuario));
            params.add(new BasicNameValuePair(TAG_CONTRASEÑA, contraseña));
            params.add(new BasicNameValuePair(TAG_TIPO, tipo));

            // sending modified data through http request
            // Notice that update user url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about user update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update user
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
            // dismiss the dialog once user uupdated
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
            pDialog = new ProgressDialog(editarUsarios.this);
            pDialog.setMessage("Deleting User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting user
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("uid", uid));

                // getting user details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_user, "POST", params);

                // check your log for json response
                Log.d("Delete User", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // user successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about user deletion
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
            // dismiss the dialog once user deleted
            pDialog.dismiss();

        }


    }


}
