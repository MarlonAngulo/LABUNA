package com.example.progland.labuna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FechaReservacionActivity extends AppCompatActivity implements OnItemSelectedListener {
    // url to create new product
    private static String url_create_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/create_reservaciones.php";
    private static String url_delete_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/delete_reservaciones.php";
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_all_laboratorios.php";
    private String URL_CATEGORIES = "http://www.cursoplataformasmoviles.com/prueba/get_categories.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_labs = "laboratorios";
    private static final String TAG_LID = "lid";
    private static final String TAG_Name = "nombre";
    private ArrayList<laboratorios> categoriesList;
    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();

    public DatePicker inputCalendario;
    CheckBox inputHorarioMannana;
    CheckBox inputHorarioTarde;
    CheckBox inputHorarioNoche;
    TextView inputUsuario;
    private Spinner spinnerFood;
    ArrayList<HashMap<String, String>> LabsList;

    JSONArray labs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Loading users in Background Thread
        setContentView(R.layout.activity_fecha_reservacion);
        Mensaje(" Reservacion de los LABS");
        LabsList = new ArrayList<HashMap<String, String>>();
        categoriesList = new ArrayList<laboratorios>();



        inputCalendario = (DatePicker) findViewById(R.id.widget54);
        inputHorarioMannana = (CheckBox) findViewById(R.id.widget57);
        inputHorarioTarde = (CheckBox) findViewById(R.id.widget59);
        inputHorarioNoche = (CheckBox) findViewById(R.id.widget58);
        inputUsuario = (TextView) findViewById(R.id.txtusuario);
        spinnerFood = (Spinner) findViewById(R.id.spinner);

        spinnerFood.setOnItemSelectedListener(this);


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
                 //creating new user in background thread;
                 new DeleteReserva().execute();
            }
        });

        boton1.setText("No has pulsado el boton");
        boton1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intento = new Intent(getApplicationContext(),VerReservaciones.class);
               startActivity(intento);
        //         boton1.setText("Has pulsado el boton "+clicks+" veces");
           }
       });
        //System.out.println("estoy aqui    "+LabsList.get(0).get("nombre"));
       // System.out.println(LabsList.get(1).get("nombre"));


    } // Fin del Oncreate

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
        spinnerFood.setAdapter(spinnerAdapter);
    }

    /**
     * Background Async Task to Load all user by making HTTP Request
     * */
    public class LoadAlllabs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FechaReservacionActivity.this);
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
                            System.out.println("En array: "+categoriesList.get(i).getNombre());
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

         //   JSONObject json = jsonParser.makeHttpRequest(url_create_user,
        //            "POST", params);

            // check log cat fro response
          ////AQUI SE CAE EN ESTA LINEA SIGUIENTE
//
//
//
            Log.d("Create Response", json.toString());

            try {


                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                      // successfully created product
                  //  Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                 //   startActivity(i);
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

//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
//
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
            int dia, mes, ano;
            int diaa,mess,anoo;
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            SimpleDateFormat dff = new SimpleDateFormat("dd/MMM/yyyy");
            String calendario = df.format(new Date(inputCalendario.getYear() - 1900, inputCalendario.getMonth(), inputCalendario.getDayOfMonth()));
            final Calendar c = Calendar.getInstance();
            final Calendar m = Calendar.getInstance();
            ano = c.get(Calendar.YEAR);
            mes = c.get(Calendar.MONTH);
            dia = c.get(Calendar.DAY_OF_MONTH);
            m.add(Calendar.DATE,+1);
            anoo = m.get(Calendar.YEAR);
            mess = m.get(Calendar.MONTH);
            diaa = m.get(Calendar.DATE);
            Format formatter = new SimpleDateFormat("dd/MMM/yyyy");
            Format formatterm = new SimpleDateFormat("dd/MMM/yyyy");
            String hoy = formatter.format(c.getTime());
            String mannna = formatterm.format(m.getTime());
            String mannana = inputHorarioMannana.getText().toString();
            String tarde = inputHorarioTarde.getText().toString();
            String noche = inputHorarioNoche.getText().toString();
            String usuario = inputUsuario.getText().toString();
            String lab =  (String) spinnerFood.getSelectedItem();
            lab = lab.replace(" ", "☺");
            String []  labid = lab.split("☺");
           if(calendario.equals(hoy) || calendario.equals(mannna)) {

               // Building Parameters
               List<NameValuePair> params = new ArrayList<NameValuePair>();
               if (inputHorarioMannana.isChecked() == true) {
                   inputHorarioTarde.setChecked(false);
                   inputHorarioNoche.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", mannana));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", usuario));
                   mannna="";

               } else if (inputHorarioTarde.isChecked() == true) {
                   inputHorarioMannana.setChecked(false);
                   inputHorarioNoche.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", tarde));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", usuario));
                   mannna="";
               } else if (inputHorarioNoche.isChecked() == true) {
                   inputHorarioMannana.setChecked(false);
                   inputHorarioTarde.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", noche));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", usuario));
                   mannna="";
               }
               JSONObject json = jsonParser.makeHttpRequest(url_create_reservaciones,
                       "POST", params);
               Log.d("Create Response", json.toString());

               try {


                   // check for success tag
                   try {
                       int success = json.getInt(TAG_SUCCESS);

                       if (success == 1) {
                           // successfully created product
                           // Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                           // startActivity(i);
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





           }else {
               try {
                   Mensaje("Las Reservas son un día antes");
               }catch (Exception k ){

               }
           }
            // getting JSON Object
            // Note that create product url accepts POST method




            // check log cat fro response
//AQUI SE CAE EN ESTA LINEA SIGUIENTE






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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long lid) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Selected" ,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:05:08 p. m.] Fin de la Clase Actividad Registro