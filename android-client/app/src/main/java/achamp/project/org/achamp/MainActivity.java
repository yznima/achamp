package achamp.project.org.achamp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import achamp.project.org.achamp.AddingFriends.AddFriends_Fragment;
import achamp.project.org.achamp.CreatingEvents.CreateEvents_Fragment;
import achamp.project.org.achamp.CreatingEvents.PostingEvent_RetainedFragment;
import achamp.project.org.achamp.Login.LoginActivity;
import achamp.project.org.achamp.Services.Check_NewEvents_Service;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Fragment;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Task;
import achamp.project.org.achamp.ViewingEvents.fragments.ListFrag;
import achamp.project.org.achamp.ViewingEvents.fragments.ViewEvents_RetainedFragment;

public class MainActivity extends FragmentActivity implements AddFriends_Fragment.OnFragmentInteractionListener, ViewEvents_Fragment.OnFragmentInteractionListener,
        CreateEvents_Fragment.OnFragmentInteractionListener,PostingEvent_RetainedFragment.OnFragmentInteractionListener,
        ViewEvents_RetainedFragment.OnFragmentInteractionListener, ListFrag.OnFragmentInteractionListener {

    private static final String DEMO_TAG = "demo_tag";
    private static final String POST_EVENT_TAG = "post_event_tag";
    private static final String VIEW_EVENT_RF_TAG = "view_event_rf_tag";
    private Demo_Fragment demo;

    private static AddFriends_Fragment addFriend;
    private static CreateEvents_Fragment createEvents;
    private static ViewEvents_Fragment viewEvents;

    private PostingEvent_RetainedFragment postEvent;
    private ViewEvents_RetainedFragment viewEvents_RF;

    private android.app.FragmentManager fm;

    private FragmentAdapter fAdapter;
    private ViewPager vPager;
    private LatLng currLoc;

    private final Handler handler = new Handler();


    private static final int NUM_ITEMS = 3;

    private ATask loginTask;

    private AddressSugTask addsugTask;


    public static final String myurl = "http://" + "159.203.88.72" + ":3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("FindErr", "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getFragmentManager();


        initialViewPager();
        initialPostEvent_RF();
        initialViewEvent_RF();
        //initMainActivityFragment();

    }
    private void initialViewPager()
    {
        addFriend = AddFriends_Fragment.newInstance("a", "n");
        createEvents = CreateEvents_Fragment.newInstance("a", "n");
        viewEvents = ViewEvents_Fragment.newInstance("a", "n");

        fAdapter = new FragmentAdapter(getSupportFragmentManager());

        vPager = (ViewPager) findViewById(R.id.pager);
        vPager.setAdapter(fAdapter);
        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pager_tab_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
    }

    private void initialPostEvent_RF() {
        postEvent = (PostingEvent_RetainedFragment) getFragmentManager().findFragmentByTag(POST_EVENT_TAG);

        if (postEvent == null)
        {
            postEvent = new PostingEvent_RetainedFragment();
            getFragmentManager().beginTransaction().add(postEvent, POST_EVENT_TAG).commit();
        }
    }

    private void initialViewEvent_RF()
    {
        viewEvents_RF = (ViewEvents_RetainedFragment) getFragmentManager().findFragmentByTag(VIEW_EVENT_RF_TAG);

        if(viewEvents_RF == null)
        {
            viewEvents_RF = ViewEvents_RetainedFragment.newInstance("a","n");
            getFragmentManager().beginTransaction().add(viewEvents_RF, VIEW_EVENT_RF_TAG).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("loginAchamp", "opPause Called");
        if(loginTask != null && !loginTask.isCancelled())
        {
            loginTask.cancel(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("loginAchamp", "opStop Called");
        if(loginTask != null && !loginTask.isCancelled())
        {
            loginTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("loginAchamp", "opDestroy Called");
        if(loginTask != null && !loginTask.isCancelled())
        {
            Log.d("loginAchamp", "it is Canceling");
            loginTask.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("loginAchamp", "onBackPressed Called");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        LoadDemo();
        //initMainActivityFragment();
        super.onResume();
        Log.e("FindErr", "MainActivity.onResume");
        SharedPreferences prefs = getSharedPreferences("usersession", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        if(loginTask == null || loginTask.isCancelled())
        {
            loginTask =  new ATask();
            loginTask.execute(username, password);
        }

        // Logs 'install' and 'app activate' App Events.
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //getMenuInflater().inflate(R.menu.menu_main, menu);
    //return true;
    //    return false;
    //}


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void eventsUpdated(final ViewEvents_Task.EventsData data) {
        Toast.makeText(getApplication(), "number of events = " + data.entries.size(), Toast.LENGTH_LONG).show();


        if (data.entries == null) {
            Toast.makeText(getApplication(), "Couldn't refresh", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplication(), "number of events = " + data.entries.size(), Toast.LENGTH_LONG).show();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                ListFrag tempListFrag = (ListFrag) fm.findFragmentByTag("list");

                if (tempListFrag != null) {
                    tempListFrag.addNewData(data);
                }


                if (viewEvents != null) {

                   // viewEvents.addUpdate();
                }
            }

        });

    }

    @Override
    public ArrayList<AChampEvent> getEvents() {
        viewEvents_RF = (ViewEvents_RetainedFragment) fm.findFragmentByTag(VIEW_EVENT_RF_TAG);
        if (viewEvents_RF != null) {
            return viewEvents_RF.getEvents();

        }
        return null;
    }

    @Override
    public LatLng getCurrLoc() {
        return currLoc;
    }


    private class ATask extends AsyncTask<String, String, Boolean> {
         boolean isCancelled = false;


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            // TODO check to see if the credentials are correct, if yes then enter else have to enetr credentials
            try {
                Thread.sleep(2000);

                return login(params[0], params[1]).equals("Not Logged In");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b){
                Intent newIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(newIntent, 5);
            } else {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("FindErr", "AsynkTask");
                        Log.d("FindErr", "Initiated viewPager");
                        if (!isStarted()) {
                            MainActivity.this.startService(new Intent(MainActivity.this, Check_NewEvents_Service.class));
                        }
                    }
                });
                initMainActivityFragment();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("loginAchamp", "onCancelled Called");
            isCancelled = true;
            super.onCancelled();
        }

    }


    public void LoadDemo() {
        demo = Demo_Fragment.newInstance("a", "z");
        fm.beginTransaction().add(R.id.fragment_container, demo, DEMO_TAG).commit();
    }

    public void initMainActivityFragment() {

        Log.d("FindErr", "Removing Demo");
        demo = (Demo_Fragment) fm.findFragmentByTag(DEMO_TAG);

        if (demo != null) {
            fm.beginTransaction().remove(demo).commit();
        }

    }


    public static class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return viewEvents;
                case 1:
                    return createEvents;
                case 2:
                    return addFriend;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "View AChamp";
                case 1:
                    return "Creat AChamp";
                case 2:
                    return "Settings";
            }

            return null;
        }
    }


    @Override
    public void uploadAchampEvent(AChampEvent event) {
        postEvent.PostTheEvent(event);
    }

    @Override
    public String onSuggestAddress(String address) {
        if(addsugTask == null)
        {
            addsugTask = new AddressSugTask();
        }
        else if (!addsugTask.isCancelled())
        {
            addsugTask.cancel(true);
            while(!addsugTask.isCancelled()){}
            addsugTask = new AddressSugTask();
        }
        addsugTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address);
        return null;
    }

    @Override
    public void ToastUploadingResult(boolean result) {
        if(result == true) {
            Toast.makeText(getApplicationContext(), "The AChamp was uploaded", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "The AChamp couldn't be uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshRequested(ArrayList<String> array) {
        Log.d("Achamp", "onRefreshRequested()");
        SharedPreferences prefs = getSharedPreferences("usersession", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        viewEvents_RF.onRefreshEvents("", username, array);
    }

    @Override
    public void setCurrLoc(LatLng curr) {
        currLoc = curr;
    }

    private String login(String username, String password) throws IOException, JSONException {

        InputStream is = null;
        String cookie = "Not Logged In";
        try {
            Log.d("loginAchamp", myurl);
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
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return cookie;
    }

    @Override
    public Context getMainContext(){

        return getApplicationContext();
    }

    @Override
    public void signoff() {
        this.finish();
    }

    public boolean isStarted() {

        SharedPreferences server_status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean value = server_status.contains("started")
                && server_status.getBoolean("started",
                false);
        Log.d("ServiceDebug", "is running? " + value);
        return value;
    }

    public class AddressSugTask extends AsyncTask<String, List<Address>, List<Address>>
    {
        @Override
        protected void onPostExecute(List<Address> addresses) {
            if(addresses != null)
            {
                String[] addrarray= new String[Math.min(addresses.size(), 5)];
                for(int i = 0; i< Math.min(addresses.size(), 5); i++)
                {

                    String addressString = "";
                    for( int j = 0; j <= addresses.get(i).getMaxAddressLineIndex(); j++)
                    {
                            Log.d("Achamp_Ad", "j = " + j + " " + addresses.get(i).getAddressLine(j));
                            addressString += addresses.get(i).getAddressLine(j) + ", ";
                    }

                    addrarray[i] = addressString;
                Log.d("Achamp_Ad", i + " " + addressString);
                }
                createEvents.setTheAdapter(addrarray);
            }
            super.onPostExecute(addresses);
        }


        @Override
        protected List<Address> doInBackground(String... params) {
            List<Address> addr = getAddress(params[0]);
            //Log.d("Achamp_Ad", addr.get(0).toString());
            return addr;
        }
    }

    private List<Address> getAddress(String s) {
        Geocoder geocoder = new Geocoder(this.getBaseContext());
        List<Address> gotAddresses = null;
        try {
            gotAddresses = geocoder.getFromLocationName(s, 5);
            Log.d("finderror", "gotAddresses: " + gotAddresses);
            if(gotAddresses.size() > 0 && gotAddresses.get(0) != null) {
                for(int i = 0; i<gotAddresses.size(); i++) {
                    Log.d("finderror", "gotAddresses["+i + "] = " + gotAddresses.get(i));
                }
                return gotAddresses;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

