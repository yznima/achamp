package achamp.project.org.achamp.AddingFriends;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.facebook.FacebookSdk;

import achamp.project.org.achamp.R;
import achamp.project.org.achamp.Services.Check_NewEvents_Service;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFriends_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFriends_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AddFriends_Fragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button signoff;
    private Switch blockNotif;
    private SharedPreferences server_status;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriends_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriends_Fragment newInstance(String param1, String param2) {
        AddFriends_Fragment fragment = new AddFriends_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddFriends_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friends_, container, false);
        signoff = (Button) view.findViewById(R.id.sign_off);
        signoff.setOnClickListener(this);

        blockNotif = (Switch) view.findViewById(R.id.block_notification);
        blockNotif.setOnCheckedChangeListener(this);

        server_status = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        blockNotif.setChecked(server_status.getBoolean(Check_NewEvents_Service.BLOCK_NOTIFICATION, false));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == blockNotif)
        {
            if(isChecked)
            {
                server_status.edit().putBoolean(Check_NewEvents_Service.BLOCK_NOTIFICATION, true).commit();
            }
            else
            {
                server_status.edit().putBoolean(Check_NewEvents_Service.BLOCK_NOTIFICATION, false).commit();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v == signoff)
        {
            SharedPreferences.Editor editor =
                    getActivity().getSharedPreferences("usersession", Activity.MODE_PRIVATE).edit();
            editor.putString("username", "");
            editor.putString("password", "");
            editor.commit();
            mListener.signoff();
        }

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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void signoff();
    }

}
