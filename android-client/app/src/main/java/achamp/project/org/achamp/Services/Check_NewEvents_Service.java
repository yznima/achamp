package achamp.project.org.achamp.Services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import achamp.project.org.achamp.MainActivity;
import achamp.project.org.achamp.R;

public class Check_NewEvents_Service extends Service {

    private IBinder binder;
    private ServiceTask task;
    private SharedPreferences server_status;
    Handler handler = new Handler();

    public static final String BLOCK_NOTIFICATION = "BLOCK_NOTIFICATION";

    public Check_NewEvents_Service() {
    }

    // happens when someone binds to the service from outside
    @Override
    public IBinder onBind(Intent intent) {
        server_status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = server_status.edit();
        editor.putBoolean("bind", true);
        editor.commit();
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub

        server_status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = server_status.edit();
        editor.putBoolean("bind", false);
        editor.commit();

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("ServiceDebug", "service started!");
        server_status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = server_status.edit();
        editor.putBoolean("started", true);
        editor.commit();

        task = new ServiceTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        server_status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = server_status.edit();
        editor.putBoolean("started", false);
        editor.commit();
        if (task != null)
            task.onCancelled();
        super.onDestroy();

    }

    private class ServiceTask extends AsyncTask<Void, Void, Void> {
        volatile boolean run = true;
        volatile int lastnumber = 0;

        //not gui thread
        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            while (run) {
                while (!sharedPreferences.getBoolean(BLOCK_NOTIFICATION, false)) {
                    try {
                        Thread.sleep(20000);
                        int temp;
                        temp = numberOfNewEvents(getSharedPreferences("usersession",
                                Activity.MODE_PRIVATE).getString("username",""));
                        if (temp> 0 && temp!=lastnumber) {
                            sendNotification(temp);
                            lastnumber = temp;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }

        //gui thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // gui thread
        @Override
        protected void onPostExecute(Void integer) {
            super.onPostExecute(integer);


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onCancelled(Void integer) {
            run = false;
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            run = false;
            super.onCancelled();
        }
    }

    public void sendNotification(int number) {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500};
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.sunglass)
                        .setContentTitle("New AChamp Events")
                        .setAutoCancel(true)
                        .setContentText(number + " events are waiting for you!");
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent).setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(2, mBuilder.build());
    }

    private int numberOfNewEvents(String user) throws IOException, JSONException {

        InputStream is = null;
        int num = 0;

        try {
            HttpURLConnection conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/newevents").openConnection()));
            conn.setDoOutput(true);
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user);
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
            reader.beginObject();

            while (reader.hasNext()) {
                Log.d("servicedebug", "reader.hasNext() " + reader.hasNext());
                String name = reader.nextName();
                if (name.equals("number")) {
                    num = reader.nextInt();
                }
            }

            reader.endObject();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

            Log.d("vt", " and the exception is " + e);
        } finally {
            if (is != null) {
                is.close();
            }
            Log.d("servicedebug", "num is " + num);
        }
        return num;
    }


}
