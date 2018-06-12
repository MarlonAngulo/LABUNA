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

/**
 * clase para optener y ver los usuarios
 */
public class VerUsuarios extends ListActivity {

    private ProgressDialog pDialog;

    // Crear objeto JSON Parser
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> userssList;

    // url para optener los usuarios
    private static String url_all_users = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/get_all_usuarios.php";

    //Nombres de nodos JSON
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_users = "usuarios";
    private static final String TAG_UID = "uid";
    private static final String TAG_USER = "usuario";

    // reserva JSONArray
    JSONArray users = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        //  Hashmap for ListView
        userssList = new ArrayList<HashMap<String, String>>();

        // Cargando pcs en Subproceso de fondo
         new  LoadAllUsers().execute();

        // Obtener una lista
        ListView lv = getListView();

        //al seleccionar un usuario individual // al iniciar la pantalla Editar usuario
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // obteniendo valores del ListItem seleccionado
                String uid = ((TextView) view.findViewById(R.id.uid)).getText()
                        .toString();

                // Iniciando una nueva intención
                Intent in = new Intent(getApplicationContext(),
                        editarUsarios.class);
                // enviando lid a la siguiente actividad
                in.putExtra(TAG_UID, uid);
                System.out.println("AQUIIIIIII: "+uid);

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
     * Tarea de fondo Async para cargar a todos los usuarios haciendo una solicitud HTTP
     * */
      class LoadAllUsers extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
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
         * obteniendo todos los pcs de url
         * */
        protected String doInBackground(String... args) {
            // Parámetros de construcción
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            // obteniendo cadena JSON de URL
            JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);

            // Verifique su log cat para obtener una respuesta JSON

            Log.d("All Users: ", json.toString());





            try {
                // Comprobando la etiqueta de ÉXITO
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
// reserva encontrados
                    // Obtener matriz de usuarios
                    users = json.getJSONArray(TAG_users);

                    // pasando por todos los usuarios
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Almacenar cada elemento json en variable
                        String id = c.getString(TAG_UID);
                        String usuario = c.getString(TAG_USER);

                        // creando un nuevo HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // agregando cada nodo secundario a la clave HashMap => valor
                        map.put(TAG_UID, id);
                        map.put(TAG_USER, usuario);

                        // adding HashList to ArrayList
                        userssList.add(map);
                    }
                } else {
// No se encontraron usuarios
// Lanzamiento Agregar nueva actividad de usuarios
                    Intent i = new Intent(getApplicationContext(),
                            RegistroUsuariosActivity.class);
                    //  Cerrando todas las actividades previas
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
            //descartar el diálogo después de obtener todos los labs
            pDialog.dismiss();
            // actualizar la interfaz de usuario desde el hilo de fondo
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Actualización de datos JSON analizados en ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            VerUsuarios.this, userssList,
                            R.layout.list_item, new String[] { TAG_UID,
                            TAG_USER},
                            new int[] { R.id.uid, R.id.usuario });
                    // actualización lista vista
                    setListAdapter(adapter);
                }
            });

        }

    }


}
