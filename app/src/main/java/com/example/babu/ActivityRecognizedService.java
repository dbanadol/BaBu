package com.example.babu;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognizedService extends IntentService {

    public static ActivityRecognitionResult result;
    public static DetectedActivity mostProbableActivity;
    //Handler mHandler = new Handler();

    public ActivityRecognizedService() { super("ActivityRecognizedService"); }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            result = ActivityRecognitionResult.extractResult(intent);
            mostProbableActivity = result.getMostProbableActivity();
            Log.d("activityDetect",mostProbableActivity.toString());
            //Toast.makeText(ActivityRecognizedService.this, mostProbableActivity.toString() , Toast.LENGTH_LONG).show();
            /*mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ActivityRecognizedService.this, mostProbableActivity.toString() , Toast.LENGTH_LONG).show();
                }
            });*/

            sendBroadcast(true);
        }
    }

    private void sendBroadcast (boolean success){
        Intent intent = new Intent ("message"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("MostProbableActivity", this.mostProbableActivity.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}

