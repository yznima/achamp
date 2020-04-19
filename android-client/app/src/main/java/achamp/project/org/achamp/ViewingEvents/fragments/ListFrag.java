package achamp.project.org.achamp.ViewingEvents.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import achamp.project.org.achamp.EventPage;
import achamp.project.org.achamp.R;
import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Task;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;



public class ListFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EventEntryAdapter adapter;

    private ListView list;
    private SwipeRefreshLayout refreshList;
    private Context context;

    private SeekBar seekBar;
    private int miles;
    private ArrayList<AChampEvent> temp;
    private int pos;
    private Location location;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFrag newInstance(String param1, String param2) {
        ListFrag fragment = new ListFrag();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ListFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity().getApplicationContext();
        location = new Location("");
        if(mListener.getCurrLoc() != null){
            setCurrLoc(mListener.getCurrLoc());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setProgress(21);
        miles = 10;

        refreshList = (SwipeRefreshLayout) view.findViewById(R.id.refresh_list);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<String> idArray = new ArrayList<String>();
                                for(AChampEvent event: temp)
                                    {
                                                if(inRange(event)) idArray.add(event.get_id());
                                }
                                mListener.onRefreshRequested(idArray);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                miles *= miles;
                miles /= 10000;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        temp = mListener.getEvents();

        //linking  java side with the xml side for the ListView
        list = (ListView) view.findViewById(R.id.list);
        adapter = new EventEntryAdapter(getActivity().getApplicationContext(), temp);

        list.setAdapter(adapter);
        // Inflate the layout for this fragment

        //AChampEvent event = new AChampEvent("title", "discription", "Address", "Date", "time", null);
        //temp.add(event);
        //addEvents();

        return view;
    }

    private boolean inRange(AChampEvent event) {
        Log.d("range", "in inrange");
        Location temp = new Location("");
        if(temp != null && event.getAddressLoc() != null) {
            temp.setLatitude(event.getAddressLoc().getLatitude());
            temp.setLongitude(event.getAddressLoc().getLongitude());
            Log.d("rangeOf", "current location = " + location);
            Log.d("rangeOf", "passed location = " + temp);
            Log.d("rangeOf", "distance between is = " + temp.distanceTo(location));
            if (temp.distanceTo(location) < miles) return true;
        }
        return false;
    }

    /*private void addEvents() {

        for(int x = 0; x< temp.size(); x++){
            AChampEvent curr = temp.get(x);
            ViewGroup v = (ViewGroup) list.getParent();
            Context c = list.getContext();
            LayoutInflater inflater = (LayoutInflater)c.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.event_list, v, false);
        }
    }

    */

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        //if (mListener != null) {
        //    mListener.onFragmentInteraction(uri);
        // }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume(){

        super.onResume();
        if(mListener.getCurrLoc() != null){
            setCurrLoc(mListener.getCurrLoc());
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void addNewData(ViewEvents_Task.EventsData data) {
        ArrayList<AChampEvent> entries = data.entries;
                if (entries != null) {
                    for (AChampEvent event : entries) {
                        if (!temp.contains(event)) {
                            temp.add(0, event);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    list.smoothScrollByOffset(0);
                    //list.invalidate();
                }
        refreshList.setRefreshing(false);
    }

    /*public class EventEntry{

        public String eventName;
        public String eventDate;
        public String time;
        public String address;
        public LatLng loc;

        public EventEntry(String eventName, String eventDate, String time, String address) {

            this.eventDate = eventDate;
            this.eventName = eventName;
            this.time = time;
            this.address = address;
        }

    }*/

    private class EventEntryAdapter extends ArrayAdapter<AChampEvent> {
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



            pos = position;
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", values.get(pos).getTitle());
//                    bundle.putString("address", values.get(pos).getAddress());
//                    bundle.putString("date", values.get(pos).getBeginingDate());
//                    bundle.putString("time", values.get(pos).getBeginingTime());
//                    bundle.putString("description", values.get(pos).getDescription());
//                    bundle.putString("bitmap", BitMapToString(values.get(pos).getPicture()));
//
//                    //bundle.putString("picture", currEvent.getPicture().toString());
//                    Intent i = new Intent(getActivity().getApplicationContext(), EventPage.class);
//                    i.putExtras(bundle);
//                    Log.d("bundleerr", "bundle title is " + bundle.get("title"));
//                    startActivity(i);
//                }
//            });5


            // The code below sets tags to your buttons so that you can detect which one was pressed

            return rowView;
        }



    }

    private String BitMapToString(Bitmap bitmap){
        if(bitmap == null)
        {
            return "";
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onRefreshRequested(ArrayList<String> idArray);
        public ArrayList<AChampEvent> getEvents();
        public LatLng getCurrLoc();
    }

    public void setCurrLoc(LatLng lg){
        location.setLatitude(lg.latitude);
        location.setLongitude(lg.longitude);
    }

}
