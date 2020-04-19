package achamp.project.org.achamp.ViewingEvents.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.MainActivity;
import achamp.project.org.achamp.R;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Task;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment {


    MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<AChampEvent> temp;
    private EventEntryAdapter adapter;

    public static MapFragment newInstance(){
        return new MapFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_location_info, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        adapter = new EventEntryAdapter(getActivity().getApplicationContext(), temp);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }


    public void addNewData(ViewEvents_Task.EventsData data) {
        if (data.entries != null) {
            adapter.clear();
            adapter.addAll((ArrayList<AChampEvent>)data.entries);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private class EventEntryAdapter extends ArrayAdapter<AChampEvent> implements
            View.OnClickListener {
        private final Context context;
        //values that will be displayed
        private final ArrayList<AChampEvent> values;


        public EventEntryAdapter(Context context, ArrayList<AChampEvent> values) {
            super(context, R.layout.event_list, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.event_list,
                    parent, false);

            TextView nameEntry = (TextView) rowView.findViewById(R.id.eventName);
            TextView dateEntry = (TextView) rowView.findViewById(R.id.date);
            TextView timeEntry = (TextView) rowView.findViewById(R.id.time);
            TextView addressEntry = (TextView) rowView.findViewById(R.id.address);
            ImageButton image = (ImageButton) rowView.findViewById(R.id.imageView);

            nameEntry.setText(values.get(position).getTitle());
            dateEntry.setText(values.get(position).getBeginingDate());
            timeEntry.setText(values.get(position).getBeginingTime());
            addressEntry.setText(values.get(position).getAddress());
            image.setImageBitmap(values.get(position).getPicture());


            // The code below sets tags to your buttons so that you can detect which one was pressed

            return rowView;
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
        public void onClick(View view) {
            // TODO Auto-generated method stub

            if (((String[]) view.getTag())[1] == "more") {


                //Intent i = new Intent(context, AChampEvent.class);
                //startActivity(i);
                //getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("list")).commit();

            }
        }

    }

}