package com.example.progland.labuna;

import android.annotation.SuppressLint;
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
//clase para optener todas las reseraciones
public class VerReservaciones extends ListActivity {

    private ProgressDialog pDialog;

    // Crear objeto JSON Parser
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> userssList;

    // url para optener todas las reservaciones
    private static String url_all_reservaciones = "http://www.cursoplataformasmoviles.com/labuna/tbl_reservaciones/get_all_reservaciones.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_reservaciones = "reservaciones";
    private static final String TAG_RID = "rid";
    private static final String TAG_FECHA = "fecha";

    // reserva JSONArray
    JSONArray users = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        // Hashmap for ListView
        userssList = new ArrayList<HashMap<String, String>>();

        // Cargando pcs en Subproceso de fondo
        new  LoadAllReserv().execute();

        // Obtener una lista
        ListView lv = getListView();

// al seleccionar un reserva individual // al iniciar la pantalla Editar reserva
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // obteniendo valores del ListItem seleccionado
                String rid = ((TextView) view.findViewById(R.id.rid)).getText()
                        .toString();

                // Iniciando una nueva intención
                Intent in = new Intent(getApplicationContext(),
                        editarReservaciones.class);
                // enviando lid a la siguiente actividad
                in.putExtra(TAG_RID, rid);
                System.out.println("AQUIIIIIII: "+rid);

                // comenzar una nueva actividad y esperar alguna respuesta
                startActivityForResult(in, 100);
            }
        });

    }

    // Respuesta de Edit User Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // si el resultado es el código 100
        if (resultCode == 100) {
// si se recibe el código de resultado 100
            // significa reser editado / eliminado reserv
            // vuelve a cargar esta pantalla
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Tarea de fondo Async para cargar a todos los reserva haciendo una solicitud HTTP
     * */
    class LoadAllReserv extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerReservaciones.this);
            pDialog.setMessage("Cargando Reservaciones...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los pcs de url
         * */
        @SuppressLint("LongLogTag")
        protected String doInBackground(String... args) {
            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            // obteniendo cadena JSON de URL
            JSONObject json = jParser.makeHttpRequest(url_all_reservaciones, "GET", params);

            // Verifique su log cat para obtener una respuesta JSON

            Log.d("Todas las Reservaciones: ", json.toString());





            try {
                // Comprobando la etiqueta de ÉXITO
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
// reserva encontrados
                    // Obtener matriz de reserva
                    users = json.getJSONArray(TAG_reservaciones);

                    // pasando por todos los resera
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Almacenar cada elemento json en variable
                        String id = c.getString(TAG_RID);
                        String fecha = c.getString(TAG_FECHA);

                        // creando un nuevo HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // agregando cada nodo secundario a la clave HashMap => valor
                        map.put(TAG_RID, id);
                        map.put(TAG_FECHA, fecha);

                        // adding HashList to ArrayList
                        userssList.add(map);
                    }
                } else {
// No se encontraron reserva
// Lanzamiento Agregar nueva actividad de reserva
                    Intent i = new Intent(getApplicationContext(),
                            FechaReservacionActivity.class);
                    // Cerrando todas las actividades previas
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return null;
        }

        /**
         * Después de completar la tarea de fondo Descartar el diálogo de progreso
         * **/
        protected void onPostExecute(String file_url) {
            // descartar el diálogo después de obtener todos los labs
            pDialog.dismiss();
            // actualizar la interfaz de usuario desde el hilo de fondo
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Actualización de datos JSON analizados en ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            VerReservaciones.this, userssList,
                            R.layout.list_item_reserv, new String[] { TAG_RID,
                            TAG_FECHA},
                            new int[] { R.id.rid, R.id.fechas });
                    // actualización lista vista
                    setListAdapter(adapter);
                }
            });

        }

    }


}
