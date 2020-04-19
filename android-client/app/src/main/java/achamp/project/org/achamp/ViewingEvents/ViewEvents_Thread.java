package achamp.project.org.achamp.ViewingEvents;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import achamp.project.org.achamp.ViewingEvents.fragments.ViewEvents_RetainedFragment;

/**
 * Created by Nima on 8/10/2015.
 */
public class ViewEvents_Thread extends Thread {

    Handler handler;
    ViewEvents_RetainedFragment viewEvents;
    String ID;

    public ViewEvents_Thread(ViewEvents_RetainedFragment viewEvents, String ID) {
        this.ID = ID;
        this.viewEvents = viewEvents;
    }


    @Override
    public void run() {


        try {
            Looper.prepare();

            handler = new Handler();

            Looper.loop();
            Log.d("HW3", "Thread is Created");
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

//    public synchronized void enqueueMessageTask(final MessageTask mtask)
//    {
//        Log.d("HW3:MessageThread", "enquingMessageTask");
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mtask.run();
//                } catch (Exception e) {
//
//                    Log.e("RefreshContentThread", "task execution error");
//                }
//                onUpdateMessages(mtask);
//            }
//        });
//    }

    public synchronized void enqueueViewEventsTask(final ViewEvents_Task task)
    {
        Log.d("HW3:MessageThread", "enquingRefreshProgressTask");
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {

                    Log.e("RefreshContentThread", "task execution error");
                }
                onUpdateProgress(task.getEventsData());
            }
        });
    }

    private void onUpdateProgress(ViewEvents_Task.EventsData data)
    {
        Log.d("HW3:MassegeThread","updatingProgress");
        viewEvents.onUpdateEvents(data);
    }
}

