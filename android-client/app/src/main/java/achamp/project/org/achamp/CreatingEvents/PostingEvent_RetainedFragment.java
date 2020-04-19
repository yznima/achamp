package achamp.project.org.achamp.CreatingEvents;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import achamp.project.org.achamp.AChampEvent;

/**
 * Created by Nima on 8/4/2015.
 */
public class PostingEvent_RetainedFragment extends Fragment {

    private PostingEvent_Thread thread;

    private OnFragmentInteractionListener mListener;

    public PostingEvent_RetainedFragment()
    {
        // Required empty constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        thread = new PostingEvent_Thread(PostingEvent_RetainedFragment.this, "1");
        thread.start();
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

    public interface OnFragmentInteractionListener{
        public void ToastUploadingResult(boolean result);
    }

    public void PostTheEvent(AChampEvent event)
    {
        thread.uploadAChampEvent(event);
    }

    public void OnResult( boolean result)
    {
        mListener.ToastUploadingResult(result);
    }


}
