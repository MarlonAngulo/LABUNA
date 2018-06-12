package com.example.progland.labuna;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Clase encargada de realizar alas reservas de un laboratorio.
 */
public class FechaReservacionActivity extends AppCompatActivity implements OnItemSelectedListener {
    // url to create new product
    private static String url_create_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/create_reservaciones.php";
    private static String url_delete_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/delete_reservaciones.php";
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_laboratorios/get_all_laboratorios.php";
    private static String url_all_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/get_all_reservaciones.php";

    private String URL_CATEGORIES = "http://www.cursoplataformasmoviles.com/prueba/get_categories.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_labs = "laboratorios";
    private static final String TAG_LID = "lid";
    private static final String TAG_Name = "nombre";
    private static final String TAG_reservaciones = "reservaciones";
    private static final String TAG_HORARIO = "horario";
    private static final String TAG_FECHA = "fecha";


    JSONArray users = null;
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
    ArrayList<HashMap<String, String>> listaReservas;
    ArrayList<HashMap<String, String>> LabsList;
    VariablesGlobales vg = VariablesGlobales.getInstance();


    JSONArray labs = null;
    DatePicker pickerDate;
    TextView info;
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Loading users in Background Thread
        setContentView(R.layout.activity_fecha_reservacion);
        Mensaje(" Reservacion de los LABS");

        Mensaje("usuario: " + vg.getMitexto() + " id: " + vg.getMivalor());
        LabsList = new ArrayList<HashMap<String, String>>();
        categoriesList = new ArrayList<laboratorios>();
        listaReservas = new ArrayList<HashMap<String, String>>();


        inputCalendario = (DatePicker) findViewById(R.id.datePicker);
        inputHorarioMannana = (CheckBox) findViewById(R.id.manana);
        inputHorarioTarde = (CheckBox) findViewById(R.id.tarde);
        inputHorarioNoche = (CheckBox) findViewById(R.id.noche);
        inputUsuario = (TextView) findViewById(R.id.txtusuario);
        spinnerFood = (Spinner) findViewById(R.id.spinner);
        spinnerFood.setOnItemSelectedListener(this);


        info = (TextView)findViewById(R.id.info);

        Calendar today = Calendar.getInstance();

        inputCalendario.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view,
                                              int year, int monthOfYear,int dayOfMonth) {
                        limpiarChecks();
                        Revisar();
                        System.out.println("EVENTO ()");

                    }});



        final Button boton1 = (Button)findViewById(R.id.btnsiguientereser);
        Button btncrearapartado = (Button) findViewById(R.id.btnapartar);
        new LoadAlllabs().execute();
        new LoadAllReserv().execute();
        inputUsuario.setText(vg.getMitexto());
        Calendar calendar = Calendar.getInstance();
            boton1.setText("Ver Reservaciones");
        if(vg.getTipo().equals("A")){
        }else{

            boton1.setVisibility(View.GONE);
        }


        // button click event
       btncrearapartado.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
               // creating new user in background thread
               new CreateNewReserva().execute();

           }
       });
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
        spinnerFood.setAdapter(spinnerAdapter);
    }

    /**
     * Clase encargada de caragar las reservas que se encuentran en la base de datos
     * para luego ver los horarios disponibles mediante el metodo Revisar().
     * */
    class LoadAllReserv extends AsyncTask<String, String, String> {
        @SuppressLint("LongLogTag")
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_all_reservaciones, "GET", params);
            Log.d("Todas las Reservaciones: ", json.toString());
            try {
                   int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                     users = json.getJSONArray(TAG_reservaciones);
                    //     pasando por todos los usuarios
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        //  Almacenar cada elemento json en variable

                        String horario = c.getString(TAG_HORARIO);
                        String fecha = c.getString(TAG_FECHA);
                        String lab = c.getString("lab");


                        // Creando nuevo HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        //  agregando cada nodo secundario a la clave HashMap => valor
                        map.put(TAG_HORARIO, horario+" "+lab);
                        map.put(TAG_FECHA, fecha);


                        // agregar HashSet a ArrayList
                        listaReservas.add(map);

                    }
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }


    }
    /**
     * Metodo publiclo Revisar, metodo encargado de mostrar los hroarios disponibles
     * para el día que se selecciona en el calendario, echo que lo realiza mediante permutaciones
     * */
    public void Revisar(){

        int dia, mes, ano;
        int diaa,mess,anoo;
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dff = new SimpleDateFormat("yyyy/MM/dd");
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
        Format formatter = new SimpleDateFormat("yyyy/MM/dd");
        Format formatterm = new SimpleDateFormat("yyyy/MM/dd");
        String hoy = formatter.format(c.getTime());
       // System.out.println("Lista"+listaReservas);

        String prueba =  spinnerFood.getSelectedItem().toString();
        prueba = prueba.replace(" ", "☺☺");
        String []  nuevo = prueba.split("☺☺");



        String lab =  "";
        String nuevoLab="";
        String labid [];
//        hoy = hoy.replace("/","-");

        for(int i=0;i<listaReservas.size();i++) {
            lab = listaReservas.get(i).get("horario");
            lab = lab.replace(" ", "☺");
            labid = lab.split("☺");

            System.out.println("ENTRA ---------------------------------------------------");
            System.out.println("Calendario "+calendario);
            System.out.println("Fecha "+listaReservas.get(i).get("fecha").toString()+" "+labid[0]+" "+labid[1]);

            //--------------------------------------------------
            if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("1")&& labid[1].equals(nuevo[0])/*listaReservas.get(i).get("horario").equals("Mañana")*/) {
                inputHorarioMannana.setEnabled(false);
            }else {
                // inputHorarioMannana.setEnabled(true);
            }
            if(calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("2")&& labid[1].equals(nuevo[0])){
                inputHorarioTarde.setEnabled(false);
            }else {
                //inputHorarioTarde.setEnabled(true);
            }
            if(calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("3")&& labid[1].equals(nuevo[0])){
                inputHorarioNoche.setEnabled(false);
            }else {
                //   inputHorarioNoche.setEnabled(true);
            }
            if(calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("123")&& labid[1].equals(nuevo[0])){
                inputHorarioMannana.setEnabled(false);
                inputHorarioTarde.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            }else {
                //   inputHorarioNoche.setEnabled(true);
            }
            if(calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("12")&& labid[1].equals(nuevo[0])){
                inputHorarioMannana.setEnabled(false);
                inputHorarioTarde.setEnabled(false);
            }else {
                //   inputHorarioNoche.setEnabled(true);
            }

            if(calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("23")&& labid[1].equals(nuevo[0])){
                inputHorarioTarde.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            }else {
                //   inputHorarioNoche.setEnabled(true);
            }

            if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("1")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("2")&& labid[1].equals(nuevo[0])) {
                inputHorarioMannana.setEnabled(false);
                inputHorarioTarde.setEnabled(false);
            } if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("1")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("3")&& labid[1].equals(nuevo[0])) {
                inputHorarioTarde.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            } if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("3")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("1")&& labid[1].equals(nuevo[0])) {
                inputHorarioMannana.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            } if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("3")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("2")&& labid[1].equals(nuevo[0])) {
                inputHorarioTarde.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            } if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("2")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("1")&& labid[1].equals(nuevo[0])) {
                inputHorarioMannana.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            } if (calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("2")&& labid[1].equals(nuevo[0]) && calendario.equals(listaReservas.get(i).get("fecha")) && labid[0].equals("3")&& labid[1].equals(nuevo[0])) {
                inputHorarioTarde.setEnabled(false);
                inputHorarioNoche.setEnabled(false);
            }


        }
    }

    /**
     * Clase encargada de cargar los labs que luego se mostraran en el spinner.
     */
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
     * Clase encargada de realizar la funcion de crear una reservacion en el dia seleccionado en el componente
     * DatePicker, mas el horario elegido en los checks box y el laboratorio elegido en el spinner.
     */
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
         * Funcion Tipo String, encargada de realizar meramente la reserva
         * */
        protected String doInBackground(String... args) {
            int dia, mes, ano;
            int diaa,mess,anoo;
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat dff = new SimpleDateFormat("yyyy/MM/dd");
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
            Format formatter = new SimpleDateFormat("yyyy/MM/dd");
            Format formatterm = new SimpleDateFormat("yyyy/MM/dd");
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
               //Creando los parametros segun el check elegido
               List<NameValuePair> params = new ArrayList<NameValuePair>();

               if (inputHorarioMannana.isChecked() == true &&inputHorarioTarde.isChecked() == true&&inputHorarioNoche.isChecked() == true ||
                       inputHorarioMannana.isChecked() == true && inputHorarioNoche.isChecked() == true&&inputHorarioTarde.isChecked() == true ||
                       inputHorarioTarde.isChecked() == true  && inputHorarioNoche.isChecked() == true&&inputHorarioMannana.isChecked() == true ||
                       inputHorarioTarde.isChecked() == true  &&inputHorarioMannana.isChecked() == true&&inputHorarioNoche.isChecked() == true ||
                       inputHorarioNoche.isChecked() == true&&inputHorarioMannana.isChecked() == true&&inputHorarioTarde.isChecked() == true||
                       inputHorarioNoche.isChecked() == true&&inputHorarioTarde.isChecked() == true&&inputHorarioMannana.isChecked() == true) {
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "123"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               }else if (inputHorarioMannana.isChecked() == true && inputHorarioTarde.isChecked() == true ||
                       inputHorarioTarde.isChecked() == true && inputHorarioMannana.isChecked() == true){
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "12"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               }else if (inputHorarioMannana.isChecked() == true && inputHorarioNoche.isChecked() == true ||
                       inputHorarioNoche.isChecked() == true && inputHorarioMannana.isChecked() == true){
                   inputHorarioTarde.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "13"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               }else if (inputHorarioNoche.isChecked() == true && inputHorarioTarde.isChecked() == true ||
                       inputHorarioTarde.isChecked() == true && inputHorarioNoche.isChecked() == true){
                   System.out.println("ENTRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                   inputHorarioMannana.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "23"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               }else if (inputHorarioMannana.isChecked() == true && inputHorarioTarde.isChecked() == false && inputHorarioNoche.isChecked() == false) {
                   inputHorarioTarde.setChecked(false);
                   inputHorarioNoche.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "1"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";

               } else if (inputHorarioTarde.isChecked() == true && inputHorarioMannana.isChecked() == false && inputHorarioNoche.isChecked() == false) {
                   inputHorarioMannana.setChecked(false);
                   inputHorarioNoche.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "2"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               } else if (inputHorarioNoche.isChecked() == true && inputHorarioMannana.isChecked() == false && inputHorarioTarde.isChecked() == false) {
                   inputHorarioMannana.setChecked(false);
                   inputHorarioTarde.setChecked(false);
                   params.add(new BasicNameValuePair("fecha", calendario));
                   params.add(new BasicNameValuePair("horario", "3"));
                   params.add(new BasicNameValuePair("lab", labid[0]));
                   params.add(new BasicNameValuePair("usuario", Integer.toString(vg.getMivalor())));
                   mannna="";
               }

               JSONObject json = jsonParser.makeHttpRequest(url_create_reservaciones,
                       "POST", params);
               Log.d("Create Response", json.toString());

               try {

                   try {
                       int success = json.getInt(TAG_SUCCESS);

                       if (success == 1) {
                           // successfully created product
                           Intent i = new Intent(getApplicationContext(), FechaReservacionActivity.class);
                           startActivity(i);
                           Mensaje("Laboratorio Reservado");

                           // closing this screen
                           finish();
                       } else if(success == 2){
                           // failed to create product
                           Mensaje("RESERVA CON EXITO!");
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
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
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
        limpiarChecks();
        Revisar();

    }

    public void setOnClickListener() {


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * Metodo limpiarChecks, realiza la funcion de habilitar y deschekear todos los checkbox
     */
    public void limpiarChecks(){
        inputHorarioMannana.setEnabled(true);
        inputHorarioTarde.setEnabled(true);
        inputHorarioNoche.setEnabled(true);
        inputHorarioMannana.setChecked(false);
        inputHorarioTarde.setChecked(false);
        inputHorarioNoche.setChecked(false);

    }

    /**
     *  Metodo Mensaje, raliza la funcion de mostrar los mensajes
     * @param msg parametro de tipo String
     */

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} // [11:05:08 p. m.] Fin de la Clase Actividad Registro