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
//clase para realizar reportes de pc
public class ReportesPCSActivity extends AppCompatActivity implements OnItemSelectedListener  {


    // Diálogo de progreso
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();

    //declaracion de variables para casteo
    public EditText inputNombre;
    EditText inputCodigoAr;
    EditText inputCodigoLab;
    EditText inputDetalle;
    VariablesGlobales vg = VariablesGlobales.getInstance();//llamado de la clase variables glovales
    // url para crear un nuevo reporte
    private static String url_create_pcs = "http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/create_computadoras.php";
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_all_laboratorios.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_labs = "laboratorios";
    private static final String TAG_LID = "lid";
    private static final String TAG_Name = "nombre";

    private ArrayList<laboratorios> categoriesList;

    private Spinner spinnerF;

    ArrayList<HashMap<String, String>> LabsList;

    JSONArray labs = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_pcs);
        Mensaje("Reportes PCS");

        LabsList = new ArrayList<HashMap<String, String>>();
        categoriesList = new ArrayList<laboratorios>();

        // Editar texto
        //casteo de los componentes UI
        inputNombre = (EditText) findViewById(R.id.edCodigoLAB);
        inputCodigoAr = (EditText) findViewById(R.id.edCantidadPCSLab);
        inputCodigoLab = (EditText) findViewById(R.id.edEstadoLabs);
        inputDetalle = (EditText) findViewById(R.id.edDetalleLAB);
        spinnerF = (Spinner) findViewById(R.id.splabor);
        spinnerF.setOnItemSelectedListener(this);



        // casteo de botones crear y ver
        Button btnCreatePcs = (Button) findViewById(R.id.btnreportar);
        Button btvercp = (Button) findViewById(R.id.btnvercomputadora);
        // Create button

        //funcion para los permisos si es admin o profe o tutor
        if(vg.getTipo().equals("A")){
        }else{
            btvercp.setVisibility(View.GONE);
        }


        //String[] letra = {"Administrador","Profesor","Tutor"};
        //inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));


        //  botón clic evento
        btnCreatePcs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //creando un nuevo usuario en el hilo de fondo
                new CreateNewPCS().execute();
            }
        });
        //boton ver reportes y llamado de la actividad correspondiente
        btvercp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent VerPc = new Intent(getApplicationContext(), VerComputadorasActivity.class);
                startActivity(VerPc);
            }
        });

        new LoadAlllabs().execute();



    }
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
        spinnerF.setAdapter(spinnerAdapter);
    }

    public class LoadAlllabs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReportesPCSActivity.this);
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
     * Tarea asincrónica de fondo para CreateNewUser
     * */
    class CreateNewPCS extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ReportesPCSActivity .this);
            pDialog.setMessage("Creando Reporte..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * creando usuario
         * */
        protected String doInBackground(String... args) {
            String marca = inputNombre.getText().toString();
            String codigo = inputCodigoAr.getText().toString();
            //String lab = inputCodigoLab.getText().toString();
            String descripcion = inputDetalle.getText().toString();



            String lab =  (String) spinnerF.getSelectedItem();
            lab = lab.replace(" ", "☺");
            String []  labid = lab.split("☺");

            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("marca", marca));
            params.add(new BasicNameValuePair("codigo", codigo));
            params.add(new BasicNameValuePair("descripcion", descripcion));
            params.add(new BasicNameValuePair("lab", labid[0]));

// obteniendo el objeto JSON
// Tenga en cuenta que crear url de producto acepta el método POST

            JSONObject json = jsonParser.makeHttpRequest(url_create_pcs,
                    "POST", params);

//            JSONObject json = jsonParser.makeHttpRequest(url_create_pcs,
//                    "POST", params);

//verifique el logcat de la respuesta



            Log.d("Create Response", json.toString());

            try {


                // cverificar la etiqueta de éxito
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // pc creado con éxito
//                    Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
//                    startActivity(i);
                        Mensaje("Reporte registrado");

                        // cerrando esta pantalla
                        finish();
                    } else if(success == 2){
                        //  no se pudo crear el producto
                        Mensaje("putooo");
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
         *  Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo una vez hecho
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


    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // Fin de la Clase Actividad Reportes PCS
