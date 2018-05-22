package com.example.progland.labuna;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerComputadorasActivity extends ListActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> LabsList;

    // url to get all users list
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/get_all_computadoras.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_comp = "computadoras";
    private static final String TAG_CID = "cid";
    private static final String TAG_Marca = "marca";

    // users JSONArray
    JSONArray labs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_coputadoras);

        // Hashmap for ListView
        LabsList = new ArrayList<HashMap<String, String>>();

        // Loading users in Background Thread
        new  LoadAllNew().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single user
        // launching Edit User Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String cid = ((TextView) view.findViewById(R.id.cid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        editarReportesPCS.class);
                // sending pid to next activity
                in.putExtra(TAG_CID, cid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit User Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted user
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all user by making HTTP Request
     * */
    class LoadAllNew extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerComputadorasActivity.this);
            pDialog.setMessage("Loading labs. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All users from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_labs, "GET", params);

            // Check your log cat for JSON reponse

            Log.d("All Labs: ", json.toString());





            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // users found
                    // Getting Array of Users
                    labs = json.getJSONArray(TAG_comp);

                    // looping through All Users
                    for (int i = 0; i < labs.length(); i++) {
                        JSONObject c = labs.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_CID);
                        String nombre = c.getString(TAG_Marca);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_CID, id);
                        map.put(TAG_Marca, nombre);

                        // adding HashList to ArrayList
                        LabsList.add(map);
                    }
                } else {
                    // no users found
                    // Launch Add New user Activity
                    Intent i = new Intent(getApplicationContext(),
                            RegistroUsuariosActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
            // dismiss the dialog after getting all users
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            VerComputadorasActivity.this, LabsList,
                            R.layout.list_item_pc, new String[] { TAG_CID,
                            TAG_Marca},
                            new int[] { R.id.cid, R.id.marca});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }


}
