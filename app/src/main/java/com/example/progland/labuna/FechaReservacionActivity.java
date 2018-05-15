package com.example.progland.labuna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.progland.labuna.laboratorios;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FechaReservacionActivity extends AppCompatActivity {
    // url to create new product
    private static String url_create_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/create_reservaciones.php";
    private static String url_delete_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/delete_reservaciones.php";
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_all_laboratorios.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_labs = "laboratorios";
    private static final String TAG_LID = "lid";
    private static final String TAG_Name = "nombre";
    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();

    public DatePicker inputCalendario;
    CheckBox inputHorarioMannana;
    CheckBox inputHorarioTarde;
    CheckBox inputHorarioNoche;
    TextView inputUsuario;
    private Spinner inputdropdown;
    ArrayList<HashMap<String, String>> LabsList;

    JSONArray labs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Loading users in Background Thread
        setContentView(R.layout.activity_fecha_reservacion);
        Mensaje(" Reservacion de los LABS");
        LabsList = new ArrayList<HashMap<String, String>>();

        inputCalendario = (DatePicker) findViewById(R.id.widget54);
        inputHorarioMannana = (CheckBox) findViewById(R.id.widget57);
        inputHorarioTarde = (CheckBox) findViewById(R.id.widget59);
        inputHorarioNoche = (CheckBox) findViewById(R.id.widget58);
        inputUsuario = (TextView) findViewById(R.id.txtusuario);
        Spinner dropdown = findViewById(R.id.spinner);
        Spinner inputLabs = (Spinner) findViewById(R.id.spinner);

        final Button boton1 = (Button)findViewById(R.id.btnsiguientereser);
        Button btncrearapartado = (Button) findViewById(R.id.btnapartar);
        Button btnEliminarapartado = (Button) findViewById(R.id.btneliminar);
        new LoadAlllabs().execute();


        // button click event
        btncrearapartado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                new CreateNewReserva().execute();
            }
        });

        // button click event
        btnEliminarapartado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new user in background thread
                // new DeleteReserva().execute();
            }
        });

        //boton1.setText("No has pulsado el boton");
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(getApplicationContext(),VerReservaciones.class);
                startActivity(intento);
                // boton1.setText("Has pulsado el boton "+clicks+" veces");
            }
        });
        System.out.println("estoy aqui    "+LabsList.get(0).get("nombre"));
       // System.out.println(LabsList.get(1).get("nombre"));
       String[] letra = new String[LabsList.size()];
        for (int i = 0; i < letra.length; i++)
        {
            System.out.println("entre");
            letra[i] = LabsList.get(i).get("nombre");
        }

        inputLabs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        System.out.println(letra[1]+"ojocnweuvneveovbue");
    } // Fin del Oncreate



    /**
     * Background Async Task to Load all user by making HTTP Request
     * */
    class LoadAlllabs extends AsyncTask<String, String, String> {

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
                    labs = json.getJSONArray(TAG_labs);

                    // looping through All Users
                    for (int i = 0; i < labs.length(); i++) {
                        JSONObject c = labs.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_LID);
                        String nombre = c.getString(TAG_Name);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_LID, id);
                        map.put(TAG_Name, nombre);

                        // adding HashList to ArrayList
                        LabsList.add(map);
                        //populationSpinner(TAG_SUCCESS);
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



    }


    /**
     * Background Async Task to CreateNewUser
     * */
    class DeleteReserva extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FechaReservacionActivity.this);
            pDialog.setMessage("Eliminando Reservación...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String id = "0";


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("rid",id));




            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_delete_reservaciones,
                    "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
//                    "POST", params);

            // check log cat fro response
//AQUI SE CAE EN ESTA LINEA SIGUIENTE



            Log.d("Create Response", json.toString());

            try {


                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully created product
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                        Mensaje("Eliminado el  Reservado");

                        // closing this screen
                        finish();
                    } else if(success == 2){
                        // failed to create product
                        Mensaje("Eliminado");
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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
    /*public void populationSpinner( String respuesta ){
        ArrayList<laboratorios> lables = new ArrayList<laboratorios>();
try {
    JSONArray jsonArreglo = new JSONArray(respuesta);


    for (int i = 0; i < jsonArreglo.length(); i++) {
        laboratorios labo = new laboratorios();
        labo.setNombre(jsonArreglo.getJSONObject(i).getString(TAG_Name));
        lables.add(labo);


    }
    ArrayAdapter<laboratorios> spinnerAdapter = new ArrayAdapter<laboratorios>(this, android.R.layout.simple_dropdown_item_1line, lables);

   // spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    inputdropdown.setAdapter(spinnerAdapter);
}catch(Exception i) {
    i.printStackTrace();
}
    }*/

    public void datosllenar()
    {


    }

    class CreateNewReserva extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FechaReservacionActivity.this);
            pDialog.setMessage("Creando Reservación...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String calendario = "Prueba";//inputCalendario.toString();
            String mannana = inputHorarioMannana.getText().toString();
            String tarde = inputHorarioTarde.getText().toString();
            String noche = inputHorarioNoche.getText().toString();
            String usuario = inputUsuario.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if(inputHorarioMannana.isChecked() == true) {
                params.add(new BasicNameValuePair("fecha", calendario));
                params.add(new BasicNameValuePair("horario", mannana));
                params.add(new BasicNameValuePair("lab", "1"));
                params.add(new BasicNameValuePair("usuario", usuario));

            }else if(inputHorarioTarde.isChecked() == true){
                params.add(new BasicNameValuePair("fecha", calendario));
                params.add(new BasicNameValuePair("horario", tarde));
                params.add(new BasicNameValuePair("lab", "1"));
                params.add(new BasicNameValuePair("usuario",usuario));
            }else if(inputHorarioNoche.isChecked() == true) {
                params.add(new BasicNameValuePair("fecha", calendario));
                params.add(new BasicNameValuePair("horario", noche));
                params.add(new BasicNameValuePair("lab", "1"));
                params.add(new BasicNameValuePair("usuario",usuario));
            }


            // getting JSON Object
            // Note that create product url accepts POST method

            JSONObject json = jsonParser.makeHttpRequest(url_create_reservaciones,
                    "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
//                    "POST", params);

            // check log cat fro response
//AQUI SE CAE EN ESTA LINEA SIGUIENTE



            Log.d("Create Response", json.toString());

            try {


                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully created product
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                        Mensaje("Laboratorio Reservado");

                        // closing this screen
                        finish();
                    } else if(success == 2){
                        // failed to create product
                        Mensaje("Reservado");
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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }


    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:05:08 p. m.] Fin de la Clase Actividad Registro