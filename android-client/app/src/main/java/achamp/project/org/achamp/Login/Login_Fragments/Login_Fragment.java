package achamp.project.org.achamp.Login.Login_Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import achamp.project.org.achamp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button signup;

    private Button login;   // The login Button in mainActivity
    private Button exit;    // The exit Button in mainActivity
    private EditText username;  // the EditText for userName in mainActivity
    private EditText password;  // th3 EditText for password in mainActivity


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Login_Fragment newInstance(String param1, String param2) {
        Login_Fragment fragment = new Login_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Login_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_login_, container, false);

        login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(this);
        exit = (Button) view.findViewById(R.id.exit);
        exit.setOnClickListener(this);

        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);

        signup = (Button) view.findViewById(R.id.SignUp);
        signup.setOnClickListener(this);

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
            throw new ClassCastException(activity.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            mListener.startSignUpFragmentActivity();
        } else if (v == login) {
            if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                Toast.makeText(getActivity().getApplication(), "Enter Username and Password",
                        Toast.LENGTH_LONG).show();
                return;
            }
            mListener.onLoggingIn(username.getText().toString(), password.getText().toString());
        } else if (v == exit) {
            getActivity().setResult(Activity.RESULT_CANCELED);
            Toast.makeText(getActivity().getBaseContext(), "Exiting FitX", Toast.LENGTH_LONG).show();
            getActivity().finish();
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

        public void startSignUpFragmentActivity();

        public void onLoggingIn(String username, String password);
    }

}