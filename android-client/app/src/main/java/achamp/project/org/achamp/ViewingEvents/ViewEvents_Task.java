package achamp.project.org.achamp.ViewingEvents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import achamp.project.org.achamp.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.MainActivity;

/**
 * Created by Nima on 8/6/2015.
 */
public class ViewEvents_Task implements Runnable{

    private ArrayList entries;
    private EventsData pdata;
    private String user;
    private ArrayList<String> idArray;
    private Context context;
    private static boolean alreadyDownLoaded = false;

    public ViewEvents_Task(ArrayList<String> idArray, String user, Context context)
    {
        this.user = user;
        this.context = context;
        this.idArray = idArray;
    }


    private Boolean loadUserProgress(String user) throws IOException, JSONException {


        InputStream is = null;

        try {
            HttpURLConnection conn;
            if(alreadyDownLoaded == false) {
                conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/givefuture").openConnection()));
            }
            else{
                conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/giveupdated").openConnection()));
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();

            JSONArray jsonArray = new JSONArray();
            //            int number = 0;
            //            for(String id:idArray) {
            //                JSONObject idJson = new JSONObject();
            //                idJson.put("_id", id);
            //                jsonArray.put(number,idJson);
            //                number;
            //            }
            //            Log.d("Achamp", "this is what gets sent JSON:"  jsonArray.toString());
            //            // posting it
            //            Writer wr = new OutputStreamWriter(conn.getOutputStream());
            //
            //            wr.write(jsonArray.toString());
            //            wr.flush();
            //            wr.close();
                                                                                                                            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.user);
            Writer wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonObject.toString());
            wr.flush();
            wr.close();
            

            // handling the response
            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();



            //handle response
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            entries = new ArrayList<>();
            reader.beginArray();
            int count = 0;

            while (reader.hasNext()) {
                alreadyDownLoaded = true;
                entries.add(convertToEvents(reader));
            }

            reader.endArray();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

            Log.d("vt", " and the exception is " + e);
        } finally {
            if (is != null) {
                is.close();
            }
        }


        return false;
    }

    //converter method
    private AChampEvent convertToEvents(JsonReader reader) throws IOException {
        String _id = null;
        String title = "";
        String description = "";
        String beginingDate = "";
        String beginingTime = "";
        String address = "";
        String picture = "";
        Address addr = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(AChampEvent.EXTRA_TITLE)) {
                title = reader.nextString();
            } else if (name.equals(AChampEvent.EXTRA_ADDRRESS)) {
                address = reader.nextString();
                if(!address.equals("")){
                    addr = convertToAddresss(address);

                }
            } else if (name.equals(AChampEvent.EXTRA_DESCRIPTION)) {
                description = reader.nextString();
            } else if (name.equals(AChampEvent.EXTRA_BEGININGDATE)) {
                beginingDate = reader.nextString();
            } else if (name.equals("_id")) {
                _id = reader.nextString();
            } else if (name.equals(AChampEvent.EXTRA_BEGININGTIME)) {
                beginingTime = reader.nextString();
            }   else if(name.equals(AChampEvent.EXTRA_PICTURE)){
                picture = reader.nextString();
            }
            else{
                reader.skipValue();
            }

        }
        reader.endObject();
        if (_id != null) {
            return new AChampEvent(title,description,address,beginingDate,beginingTime,StringToBitMap(picture), _id, addr);
        }
        return null;
    }

    private Address convertToAddresss(String string) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> gotAddresses = null;
        try {
            gotAddresses = geocoder.getFromLocationName(string, 1);
            Log.d("finderror", "gotAddresses: " + gotAddresses);
            if(gotAddresses.size() > 0 && gotAddresses.get(0) != null) {
                Log.d("finderror", "gotAddresses: " + gotAddresses.get(0));
                return gotAddresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap StringToBitMap(String encodedString){
                try{
                        byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
                        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        return bitmap;
                    }catch(Exception e){
                        e.getMessage();
                        return null;
                    }
            }

    @Override
    public void run() {
        try{
            loadUserProgress("");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            pdata = new EventsData("NewEvents", entries);
        }
    }

    public EventsData getEventsData() {
        return pdata;
    }

    public class EventsData{
        public String s;
        public ArrayList entries;

        public EventsData (String s, ArrayList entries)
        {
            this.s = s;
            this.entries = entries;
        }
    }
}

