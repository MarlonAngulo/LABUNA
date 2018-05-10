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

public class VerUsuarios extends ListActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> userssList;

    // url to get all users list
    private static String url_all_users = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/get_all_usuarios.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_users = "usuarios";
    private static final String TAG_UID = "uid";
    private static final String TAG_USER = "usuario";

    // users JSONArray
    JSONArray users = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        // Hashmap for ListView
        userssList = new ArrayList<HashMap<String, String>>();

        // Loading users in Background Thread
        new LoadAllUsers().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single user
        // launching Edit User Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String uid = ((TextView) view.findViewById(R.id.uid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        editarUsarios.class);
                // sending pid to next activity
                in.putExtra(TAG_UID, uid);
                System.out.println("AQUIIIIIII: "+uid);

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
    class LoadAllUsers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerUsuarios.this);
            pDialog.setMessage("Loading users. Please wait...");
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
            JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);

            // Check your log cat for JSON reponse

            Log.d("All Users: ", json.toString());





            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // users found
                    // Getting Array of Users
                    users = json.getJSONArray(TAG_users);

                    // looping through All Users
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_UID);
                        String usuario = c.getString(TAG_USER);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_UID, id);
                        map.put(TAG_USER, usuario);

                        // adding HashList to ArrayList
                        userssList.add(map);
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
                            VerUsuarios.this, userssList,
                            R.layout.list_item, new String[] { TAG_UID,
                            TAG_USER},
                            new int[] { R.id.uid, R.id.usuario });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }


}
