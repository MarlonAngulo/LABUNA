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
//clase para cargar ver todos los reportes existentes
public class VerComputadorasActivity extends ListActivity {

    private ProgressDialog pDialog;

    // Crear objeto JSON Parser
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> LabsList;

    // url para coptener todos los reportes
    private static String url_all_labs ="http://www.cursoplataformasmoviles.com/labuna/tbl_computadoras/get_all_computadoras.php";

    // Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_comp = "computadoras";
    private static final String TAG_CID = "cid";
    private static final String TAG_Marca = "marca";

    // labs JSONArray
    JSONArray labs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_coputadoras);

        // Hashmap for ListView
        LabsList = new ArrayList<HashMap<String, String>>();

        // Cargando pcs en Subproceso de fondo
        new  LoadAllNew().execute();

        // Obtener una lista
        ListView lv = getListView();

// al seleccionar un usuario individual // al iniciar la pantalla Editar pc
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // obteniendo valores del ListItem seleccionado
                String cid = ((TextView) view.findViewById(R.id.cid)).getText()
                        .toString();

                // Iniciando una nueva intención
                Intent in = new Intent(getApplicationContext(),
                        editarReportesPCS.class);
                // enviando lid a la siguiente actividad
                in.putExtra(TAG_CID, cid);

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
            // significa pc editado / eliminado pc
            // vuelve a cargar esta pantalla
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Tarea de fondo Async para cargar a todos los usuarios haciendo una solicitud HTTP
     * */
    class LoadAllNew extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
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
         * obteniendo todos los pcs de url
         * */
        protected String doInBackground(String... args) {
            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            // obteniendo cadena JSON de URL
            JSONObject json = jParser.makeHttpRequest(url_all_labs, "GET", params);

            // Verifique su log cat para obtener una respuesta JSON

            Log.d("All Labs: ", json.toString());





            try {
                //Comprobando la etiqueta de ÉXITO
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
// labs encontrados
                    // Obtener matriz de labs
                    labs = json.getJSONArray(TAG_comp);

                    // pasando por todos los labs
                    for (int i = 0; i < labs.length(); i++) {
                        JSONObject c = labs.getJSONObject(i);

                        // Almacenar cada elemento json en variable
                        String id = c.getString(TAG_CID);
                        String nombre = c.getString(TAG_Marca);

                        // creando un nuevo HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // agregando cada nodo secundario a la clave HashMap => valor
                        map.put(TAG_CID, id);
                        map.put(TAG_Marca, nombre);

                        //adding HashList to ArrayList
                        LabsList.add(map);
                    }
                } else {
// No se encontraron labs
// Lanzamiento Agregar nueva actividad de lab
                    Intent i = new Intent(getApplicationContext(),
                            ReportesPCSActivity.class);
                    //Cerrando todas las actividades previas
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
            //actualizar la interfaz de usuario desde el hilo de fondo
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Actualización de datos JSON analizados en ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            VerComputadorasActivity.this, LabsList,
                            R.layout.list_item_pc, new String[] { TAG_CID,
                            TAG_Marca},
                            new int[] { R.id.cid, R.id.marca});
                    // actualización lista vista
                    setListAdapter(adapter);
                }
            });

        }

    }


}
