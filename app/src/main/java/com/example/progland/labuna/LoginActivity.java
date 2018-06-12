package com.example.progland.labuna;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Una pantalla de inicio de sesión que ofrece inicio de sesión a través de nombre de usuario / contraseña.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id para identificar la solicitud de permiso READ_CONTACTS.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Un almacén de autenticación ficticio que contiene nombres de usuario y contraseñas conocidos.
     * TODO: remove after connecting to a real authentication system.
     */
    ArrayList<HashMap<String, String>> userssList;

    public static String[] DUMMY_CREDENTIALS = new String[]{
           // "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Mantenga un registro de la tarea de inicio de sesión para asegurarse de que podamos cancelarla si así lo solicita.
     */
    private UserLoginTask mAuthTask = null;

    // Referencias de UI
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    //direccion del php en el servidor e linea
    private static String url_all_users = "http://www.cursoplataformasmoviles.com/labuna/tbl_usuarios/get_all_usuarios.php";


    //declaracion de variables TAG
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_users = "usuarios";
    private static final String TAG_UID = "uid";
    private static final String TAG_USER = "usuario";
    private static final String TAG_CONTRASENNA = "contraseña";
    private static final String TAG_TIPO = "tipo";


    String [] var;
    JSONArray users = null;
    Spinner inputPuesto;
    Spinner spinner;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    private String[] spinnerArray;
    VariablesGlobales vg = VariablesGlobales.getInstance();//declaracion para poder utilizar la calse de variables globales

    @Override
    //funcion al crear la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);//casteo

        userssList = new ArrayList<HashMap<String, String>>();//declariacion de la lista de usuarios



        Mensaje("usuario" + vg.getMitexto());//llamado del metodo menaje


       new  LoadAllUsers().execute();// ejecucion de la clase que carga los usuarios

        //populateAutoComplete();
        //Mensaje("Bienvenido al Login");

        mPasswordView = (EditText) findViewById(R.id.password);//casteo
        //metodo que escucha al escribir en el campo de contrasenna
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });




        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);//casteo
        //metodo que escucha i realizar la accion al precionar ingresar
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intento = new Intent(getApplicationContext(), MenuActivity.class);
//                startActivity(intento);
                attemptLogin();//llamado al metodo que verifica los datos de login


            }
        });

        //mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);//carga visual



    }



//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }

//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
//    public String [] llenarArregloConUuarios()
//    {
//       String [] local = new String[userssList.size()];
//        for (int i = 0; i < userssList.size() - 1; i++)
//        {
//            local[i] = userssList.get(i).get("usuario").toString() ;
////            System.out.println(var[i]);
//        }
//       // inputPuesto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, var));
//
//       return local;
//    }


//funcion que verifica los datos ingresados por el usuario con los datos que se encuetran en la lista llenada desde la bd
    private void attemptLogin() {
        if (mAuthTask != null) {//si es nulo no hace nada
            return;
        }

        // Reset errors.
        mEmailView.setError(null);//mensaje de error si es nulo
        mPasswordView.setError(null);//mensaje de error si es nulo

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();//optengo lo que tenga el campo te texto
        String password = mPasswordView.getText().toString();//optengo lo que tenga el campo te texto



        boolean cancel = false;
        View focusView = null;

        System.out.println(userssList.get(1).get("usuario") + userssList.get(1).get("contraseña") + userssList.get(1).get("tipo"));
//        vg.setMitexto(userssList.get(1).get("usuario"));
//        Mensaje("usuario" + vg.getMitexto());

//        for (int i = 0; i < userssList.size(); i++)
//        {
//            DUMMY_CREDENTIALS[0] += userssList.get(1).get("usuario") + ":" + userssList.get(1).get("contraseña");
//
//        }
//
//        System.out.println(DUMMY_CREDENTIALS[0]);
//

//en este for recorro la lista con los usuarios existentes en la bd comparandolos con lo que escribio
        //el usuario en los campos,, donde encuentra coincidencia enra al sistema sino
        //no entra y muestra el mensaje correspondiente **********************
        for (int i = 0; i < userssList.size(); i++)
        {
            if((userssList.get(i).get("usuario").trim().equalsIgnoreCase(email)) &&  (userssList.get(i).get("contraseña").trim().equalsIgnoreCase(password)) )
            {
                Intent intento = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intento);
                vg.setMitexto(userssList.get(i).get("usuario"));//le mando el dato a la clase variables globales
                vg.setMivalor(Integer.parseInt(userssList.get(i).get("uid")));//le mando el dato a la clase variables globales
                vg.setTipo(userssList.get(i).get("tipo"));//le mando el dato a la clase variables globales
                //Mensaje("usuario" + vg.getMitexto());
                finish();
                break;

            }
            else
            {
                Mensaje("usuario incorrecto");
            }


        }

        //********************************************************


    }
//funcion si el correo es invalido
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("a");
    }
//funcon si la contrasenna no cumple los requisitos
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Muestra la interfaz de usuario de progreso y oculta el formulario de inicio de sesión.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // En Honeycomb MR2 tenemos las API ViewPropertyAnimator, que permiten
        // para animaciones muy fáciles. Si está disponible, use estas API para desvanecerse
        // el progreso spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // Las API ViewPropertyAnimator no están disponibles, así que simplemente muestre
            // y ocultar los componentes de interfaz de usuario relevantes.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Recupere filas de datos para el contacto 'perfil' del usuario del dispositivo.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Seleccione solo direcciones de correo electrónico.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Mostrar primero las direcciones de correo electrónico principales. Tenga en cuenta que no habrá
                // una dirección de correo electrónico principal si el usuario no ha especificado una.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    //funcion cuando termina de cargar los datos
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
//funcion que autocompleta
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Crea un adaptador para decirle a AutoCompleteTextView qué mostrar en su lista desplegable.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Representa una tarea de inicio de sesión / registro asíncrona utilizada para autenticarse
           * el usuario.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simular el acceso a la red.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // La cuenta existe, devuelve verdadero si la contraseña coincide.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    class LoadAllUsers extends AsyncTask<String, String, String> {

        /**
         * Antes de iniciar el hilo de fondo Mostrar cuadro de diálogo de progreso
         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Loading users. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }

        /**
         * optener todos los usuarios por url
         */
        String va[];
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
                    //usuarios encontrados
// Obtener matriz de usuarios
                    users = json.getJSONArray(TAG_users);

                    // pasando por todos los usuarios
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        // Almacenar cada elemento json en variable

                        String id = c.getString(TAG_UID);
                        String usuario = c.getString(TAG_USER);

                        String contraseña = c.getString(TAG_CONTRASENNA);

                        String tipo = c.getString(TAG_TIPO);




                        // creando un nuevo HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // agregando cada nodo secundario a la clave HashMap => valor
                        map.put(TAG_UID, id);
                        map.put(TAG_USER, usuario);
                        map.put(TAG_CONTRASENNA, contraseña);
                        map.put(TAG_TIPO, tipo);

                        // agregar HashSet a ArrayList
                        userssList.add(map);
                       //va = llenarArregloConUuarios();

                    }
                } else {
                    // No se encontraron usuarios
// Lanzamiento Agregar nueva actividad de usuario
                    Intent i = new Intent(getApplicationContext(),
                            RegistroUsuariosActivity.class);
                    // Cerrando todas las actividades previas
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }



    }


    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

} //Fin de la Clase Actividad Login
