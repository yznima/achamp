package achamp.project.org.achamp.Login;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import achamp.project.org.achamp.Login.Login_Fragments.Login_Fragment;
import achamp.project.org.achamp.Login.Login_Fragments.Signup_Fragment;
import achamp.project.org.achamp.MainActivity;
import achamp.project.org.achamp.R;

public class LoginActivity extends FragmentActivity implements Login_Fragment.OnFragmentInteractionListener,
        Signup_Fragment.OnFragmentInteractionListener {

    private final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG";
    private final String SIGNUP_FRAGMENT_TAG = "SIGNUP_FRAGMENT_TAG";

    private FragmentManager fm;

    private Login_Fragment loginf;
    private Signup_Fragment signupf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fm = getSupportFragmentManager();
        initLoginFrag();
    }

    private void initLoginFrag() {
        loginf = (Login_Fragment) fm.findFragmentByTag(LOGIN_FRAGMENT_TAG);


        if (loginf == null) {
            Log.d("Achamp", "loginf" + loginf);
            loginf = Login_Fragment.newInstance("login", "new");
            fm.beginTransaction().replace(R.id.login_container, loginf, LOGIN_FRAGMENT_TAG).
                    setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).commit();
        } else {
            fm.beginTransaction().replace(R.id.login_container, loginf, LOGIN_FRAGMENT_TAG).
                    setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).commit();
        }
    }


    private void startSignUpFragment() {

        signupf = (Signup_Fragment) fm.findFragmentByTag(SIGNUP_FRAGMENT_TAG);

        if (signupf == null) {
            Log.d("Achamp", "signUp" + signupf);
            signupf = Signup_Fragment.newInstance("signup", "2");
            fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.login_container, signupf, SIGNUP_FRAGMENT_TAG).commit();
        } else {
            fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.login_container, signupf, SIGNUP_FRAGMENT_TAG).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void startSignUpFragmentActivity() {
        startSignUpFragment();
    }


    @Override
    public void startLoginFragmentActivity() {
        initLoginFrag();
    }

    @Override
    public void signUpUser(String name, String lastname, String email, String username, String password) {

        new SignUpTask().execute(name, lastname, email, username, password);
    }

    @Override
    public void onLoggingIn(String username, String password) {

        new LoginTask().execute(username, password);

    }

    //perform login
    private String login(String username, String password) throws IOException, JSONException {

        InputStream is = null;
        String cookie = "Not Logged In";
        try {
            HttpURLConnection conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/login").openConnection()));
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.connect();


            JSONObject cred = new JSONObject();
            cred.put("username", username);
            cred.put("password", password);
            Log.d("Achamp", "this is what gets sent JSON:" + cred.toString());
            // posting it
            Writer wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(cred.toString());
            wr.flush();
            wr.close();
            Log.d("Achamp", " response from the server is" + conn.getResponseCode());
            // handling the response
            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                Log.d("Achamp", "cookies: Login HTTP_OK");

                cookie = "Logged In Successfully";
                //                Map<String, List<String>> headerFields = conn.getHeaderFields();
                //                //COOKIES_HEADER
                //                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                //
                //                //  for (String s : cookiesHeader) {
                //
                //                Log.d("hw4", "cookies: "  cookiesHeader.get(0).substring(0, cookiesHeader.get(0).indexOf(";")));
                //
                //                cookie = cookiesHeader.get(0).substring(0, cookiesHeader.get(0).indexOf(";"));
                //

            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

            Log.d("vt", " and the exception is " + e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return cookie;
    }

    //perform login
    private String signUp(String name, String lastname, String email, String username, String password) throws IOException, JSONException {

        InputStream is = null;
        String cookie = "not Signed Up";
        try {
            HttpURLConnection conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/signup").openConnection()));
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.connect();


            JSONObject newUser = new JSONObject();
            newUser.put("name", name);
            newUser.put("lastname", lastname);
            newUser.put("email", email);
            newUser.put("username", username);
            newUser.put("password", password);
            Log.d("Achamp", "this is what gets sent JSON:" + newUser.toString());
            // posting it
            Writer wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(newUser.toString());
            wr.flush();
            wr.close();
            Log.d("Achamp", " response from the server is" + conn.getResponseCode());
            // handling the response
            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();


            if (HttpResult == 200) {
                cookie = "Signed up sucessfully";
            }
            //                Log.d("Achamp", "cookies: Login HTTP_OK");
            //                Map<String, List<String>> headerFields = conn.getHeaderFields();
            //                //COOKIES_HEADER
            //                List<String> cookiesHeader = headerFields.get("Set-Cookie");
            //
            //                //  for (String s : cookiesHeader) {
            //
            //                Log.d("hw4", "cookies: "  cookiesHeader.get(0).substring(0, cookiesHeader.get(0).indexOf(";")));
            //
            //                cookie = cookiesHeader.get(0).substring(0, cookiesHeader.get(0).indexOf(";"));
            //
            //
            //            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

            Log.d("Achamp", " and the exception is " + e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return cookie;
    }

    private class LoginTask extends AsyncTask<String, String, String> {


        //not gui thread
        @Override
        protected String doInBackground(String... params) {
            String tempCookie = null;
            try {
                Thread.sleep(2000);
                tempCookie = login(params[0], params[1]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (tempCookie == "Logged In Successfully") {
                    SharedPreferences.Editor editor =
                            getSharedPreferences("usersession", MODE_PRIVATE).edit();
                    editor.putString("username", params[0]);
                    editor.putString("password", params[1]);
                    editor.commit();
                }
            }
            return tempCookie;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Not Logged In")) {
                Toast.makeText(getApplication().getApplicationContext(), "Couldn't Login!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication().getApplicationContext(), "Login Successful.", Toast.LENGTH_LONG).show();
                LoginActivity.this.setResult(RESULT_OK);
                LoginActivity.this.finish();
            }
        }
    }

    private class SignUpTask extends AsyncTask<String, String, String> {


        //not gui thread
        @Override
        protected String doInBackground(String... params) {
            String tempCookie = null;
            try {
                tempCookie = signUp(params[0], params[1], params[2], params[3], params[4]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (!tempCookie.equals("not Signed Up")) {
                    SharedPreferences.Editor editor =
                            getSharedPreferences("usersession", MODE_PRIVATE).edit();
                    editor.putString("username", params[3]);
                    editor.putString("password", params[4]);
                    editor.commit();
                }
            }
            return tempCookie;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("not Signed Up")) {
                Toast.makeText(getApplication().getApplicationContext(), "Couldn't Sign Up!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication().getApplicationContext(), "Welcome to Achamp.", Toast.LENGTH_LONG).show();
                LoginActivity.this.setResult(RESULT_OK);
                LoginActivity.this.finish();
            }
        }
    }


}