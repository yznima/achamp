package achamp.project.org.achamp.ViewingEvents.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.R;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Task;
import achamp.project.org.achamp.ViewingEvents.ViewEvents_Thread;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEvents_RetainedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewEvents_RetainedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewEvents_RetainedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<AChampEvent> allAchamp;

    private ViewEvents_Thread thread;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewEvents_RetainedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewEvents_RetainedFragment newInstance(String param1, String param2) {
        ViewEvents_RetainedFragment fragment = new ViewEvents_RetainedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewEvents_RetainedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("FindErr", "ViewEvents_RetainedFragment.OnActivityCreated");
        super.onActivityCreated(savedInstanceState);



        thread = new ViewEvents_Thread(ViewEvents_RetainedFragment.this, "1");
        thread.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("FindErr", "ViewEvents_RetainedFragment.OnCreate");
        super.onCreate(savedInstanceState);
        super.setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        allAchamp = new ArrayList<AChampEvent>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onUpdateEvents(ViewEvents_Task.EventsData data) {

        if(data == null || data.entries == null)
        {
            return;
        }
        Log.d("findErr", "Arraylist passed to onUpdateEvents= " + data.entries);
        for(AChampEvent e: (ArrayList<AChampEvent>) data.entries){

            Log.d("findErr", "current Event = " + e.getTitle());
            if(!allAchamp.contains(e)) {
                allAchamp.add(e);
            }
        }
        mListener.eventsUpdated(data);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void eventsUpdated(ViewEvents_Task.EventsData data);
    }

    public void onRefreshEvents(String cookie, String user, ArrayList<String> array)
    {
        Log.d("Achamp", "onRefreshEvents");
        if(thread.isAlive()) {
            Log.d("Achamp:RetainedFragment", "IsAlive");
            thread.enqueueViewEventsTask(new ViewEvents_Task(array, user, getActivity()));
            //thread.enqueueMessageTask(new MessageTask("message", new Handler()));
        }
    }

    public ArrayList<AChampEvent> getEvents(){

        Log.d("findErr", "arrayList= " + allAchamp);
        return allAchamp;
    }

}
