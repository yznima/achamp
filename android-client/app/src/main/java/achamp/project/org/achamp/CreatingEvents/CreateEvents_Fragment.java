package achamp.project.org.achamp.CreatingEvents;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import achamp.project.org.achamp.AChampEvent;
import achamp.project.org.achamp.Login.LoginActivity;
import achamp.project.org.achamp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEvents_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateEvents_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEvents_Fragment extends Fragment implements View.OnClickListener {

    private static final int LOAD_IMAGE_REQUEST = 0;
    private static final int LOAD_IMAGE_FROM_GALLERY = 1;
    private Button datePicker;
    private Button timePicker;
    private Button camera;
    private Button gallery;
    private Button rotate;
    private Button post;

    private static EditText time;
    private static EditText date;

    private EditText title;
    private EditText description;

    private AutoCompleteTextView address;
    private ArrayAdapter<String> ad;

    private static Uri newUri;

    private ImageView aChampPicture;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
     * @return A new instance of fragment CreateEvents_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEvents_Fragment newInstance(String param1, String param2) {
        CreateEvents_Fragment fragment = new CreateEvents_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateEvents_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_events_, container, false);

        initializeFields(view);
        return view;
    }

    private void initializeFields(View view)
    {
        datePicker = (Button) view.findViewById(R.id.date_button);
        timePicker = (Button) view.findViewById((R.id.time_button));

        datePicker.setOnClickListener(this);
        timePicker.setOnClickListener(this);

        time = (EditText) view.findViewById(R.id.time);
        time.setOnClickListener(this);

        date = (EditText) view.findViewById(R.id.date);
        date.setOnClickListener(this);

        camera = (Button) view.findViewById(R.id.camera);
        camera.setOnClickListener(this);

        gallery = (Button) view.findViewById(R.id.gallery);
        gallery.setOnClickListener(this);

        rotate = (Button) view.findViewById(R.id.rotate);
        rotate.setOnClickListener(this);

        post = (Button) view.findViewById(R.id.achamp_post_event);
        post.setOnClickListener(this);

        aChampPicture = (ImageView) view.findViewById(R.id.event_pic);

        title = (EditText) view.findViewById(R.id.achamp_title);
        description = (EditText) view.findViewById(R.id.achamp_description);

        address = (AutoCompleteTextView) view.findViewById(R.id.achamp_address);
        setTheAdapter(new String[]{});
        setUpAddressSuggestion();
    }

    private void setUpAddressSuggestion()
    {
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Achamp_Ad", "Before text is" + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Achamp_Ad", "on change text is" + s + ", start = " + start + ", before = "+before + ", count = "+ count );
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0)
                mListener.onSuggestAddress(s.toString());
            }
        });
        String[] s  = {};
        ad = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, s);
        address.setAdapter(ad);
    }

    public void setTheAdapter(String[] s)
    {
        if(s.length >0) {
            ad = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    android.R.layout.simple_dropdown_item_1line, s);
            address.setAdapter(ad);
            address.showDropDown();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOAD_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            LoadImageForAChamp(newUri);
        }
        else if (requestCode == LOAD_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                Uri galaryImage = data.getData();
                LoadImageForAChamp(galaryImage);
        }
    }


    private void LoadImageForAChamp(Uri imageUri)
    {
        try {
            Bitmap captureBmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);//Uri.fromFile(file));
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(captureBmp , 0, 0, captureBmp .getWidth(), captureBmp .getHeight(), matrix, true);
            aChampPicture.setImageBitmap(rotatedBitmap);
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity().getApplication(), "A problem occured opening the image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplication(), "A problem occured loading the image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private File getTempFile() {
        //it will return /sdcard/image.tmp
        final File path = new File(Environment.getExternalStorageDirectory(), "/achamp/");
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, Calendar.getInstance().getTime() + ".jpg");
    }

    @Override
    public void onClick(View v) {
        if (v == datePicker || v == date) {
            DialogFragment newFragment = new CreateEvents_Fragment.DatePickerFragment();
            newFragment.show(getActivity().getFragmentManager(), "datePicker");
        }
        else if (v == timePicker || v == time) {
            DialogFragment newTimeFragment = new CreateEvents_Fragment.TimePickerFragment();
            newTimeFragment.show(getActivity().getFragmentManager(), "timePicker");
        }
        else if (v == camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            newUri = Uri.fromFile(getTempFile());
            //Log.e("ACHamp", "File saved in:" + Uri.fromFile(getTempFile(getActivity())));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, newUri);
            startActivityForResult(intent, LOAD_IMAGE_REQUEST);
        }
        else if (v == gallery) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, ""), LOAD_IMAGE_FROM_GALLERY);
        }
        if(v == rotate && aChampPicture.getDrawable() !=null)
        {
            Bitmap captureBmp = ((BitmapDrawable)aChampPicture.getDrawable()).getBitmap();
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(captureBmp, 0, 0, captureBmp.getWidth(), captureBmp.getHeight(), matrix, true);
            aChampPicture.setImageBitmap(rotatedBitmap);

        }
        else if(v == post)
        {
            mListener.uploadAchampEvent(readyToPost());
        }
    }

    private AChampEvent readyToPost()
    {
        Bitmap bitmap;
        if (aChampPicture.getDrawable() != null) {
            bitmap = ((BitmapDrawable) aChampPicture.getDrawable()).getBitmap();
        } else {
            bitmap = null;
        }
        return new AChampEvent(title.getText().toString(), description.getText().toString(),
                address.getText().toString(), date.getText().toString(), time.getText().toString(), bitmap);

    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void uploadAchampEvent(AChampEvent event);
        public String onSuggestAddress(String address);
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private static Calendar cal = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            cal = Calendar.getInstance();
            cal.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE  MM/dd/yy");


            date.setText(sdf.format(cal.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private static Calendar cal = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");

            time.setText(sdf.format(cal.getTime()));
        }
    }

}
