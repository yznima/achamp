package achamp.project.org.achamp.CreatingEvents;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.MainActivity;

/**
 * Created by Nima on 8/4/2015.
 */
public class PostingEvent_Thread extends Thread {
    private Handler handler;
    private PostingEvent_RetainedFragment postEvent;
    private String ID;

    public PostingEvent_Thread(PostingEvent_RetainedFragment postEvent, String ID)
    {
        this.postEvent = postEvent;
        this.ID = ID;
    }

    @Override
    public void run() {
        try {
            Looper.prepare();

            handler = new Handler();

            Looper.loop();
            Log.d("Achamp", "Thread is Created");
        } catch (Throwable t) {
            Log.e("threads", "TheThread halted due to an error", t);
        }
    }

    public synchronized void requestStop()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Looper.myLooper().quit();
            }
        });
    }

    public synchronized void uploadAChampEvent(final AChampEvent event){
        handler.post(new Runnable() {
            @Override
            public void run() {

                boolean sentOkey = false;
                try {
                    sentOkey = uploadToServer(event);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postEvent.OnResult(sentOkey);
            }
        });
    }

    public boolean uploadToServer(AChampEvent event) throws IOException, JSONException {

        HttpURLConnection conn = (HttpURLConnection) ((new URL(MainActivity.myurl + "/post").openConnection()));
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.connect();

        JSONObject entry = new JSONObject();
        entry.put(AChampEvent.EXTRA_TITLE, event.getTitle());
        entry.put(AChampEvent.EXTRA_ADDRRESS, event.getAddress());
        entry.put(AChampEvent.EXTRA_BEGININGDATE, event.getBeginingDate());
        entry.put(AChampEvent.EXTRA_BEGININGTIME, event.getBeginingTime());
        entry.put(AChampEvent.EXTRA_DESCRIPTION, event.getDescription());
        entry.put(AChampEvent.EXTRA_PICTURE, BitMapToString(event.getPicture()));
        Writer wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(entry.toString());
        wr.flush();
        wr.close();

        if (conn.getResponseCode() >= 400) {
            return false;

        } else {
            return true;
        }

    }

    private String BitMapToString(Bitmap bitmap){
                if(bitmap == null)
                    {
                                    return "";
                }
                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                return temp;
           }
}
