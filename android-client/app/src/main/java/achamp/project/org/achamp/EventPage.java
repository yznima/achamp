package achamp.project.org.achamp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class EventPage extends Activity {

    private String title;
    private String address;
    private String date;
    private String time;
    private String description;
    private String bitmapString;
    private Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        title = getIntent().getExtras().getString("title");
        address = getIntent().getExtras().getString("address");
        date = getIntent().getExtras().getString("date");
        time = getIntent().getExtras().getString("time");
        description = getIntent().getExtras().getString("description");
        bitmapString = getIntent().getExtras().getString("bitmap");
        b = StringToBitMap(bitmapString);

        Log.d("shazam", "title is: " + title);

        TextView viewTitle = (TextView) findViewById(R.id.the_title);
        TextView viewAddress = (TextView) findViewById(R.id.the_address);
        TextView viewDate = (TextView) findViewById(R.id.the_date);
        TextView viewTime = (TextView) findViewById(R.id.the_time);
        TextView viewDescription = (TextView) findViewById(R.id.the_description);
        ImageView pic = (ImageView) findViewById(R.id.pic);

        viewTitle.setText(title);
        viewAddress.setText(address);
        viewDate.setText(date);
        viewTime.setText(time);
        viewDescription.setText(description);
        pic.setImageBitmap(b);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_page, menu);
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
}
