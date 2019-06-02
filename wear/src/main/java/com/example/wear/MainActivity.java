package com.example.wear;

        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.wearable.activity.WearableActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.gms.wearable.DataEvent;
        import com.google.android.gms.wearable.DataEventBuffer;
        import com.google.android.gms.wearable.DataItem;
        import com.google.android.gms.wearable.DataMapItem;
        import com.google.android.gms.wearable.PutDataMapRequest;
        import com.google.android.gms.wearable.PutDataRequest;
        import com.google.android.gms.wearable.Wearable;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import com.google.android.gms.wearable.DataClient;

public class MainActivity extends WearableActivity implements SensorEventListener,
        DataClient.OnDataChangedListener {

    private Button startBtn;
    private Button stopBtn;
    private TextView heartRate;
    private TextView nowDisplay;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;

    private final static String TAG = "WearTAG";
    String datapath = "/data_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // dynamically request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BODY_SENSORS}, 5268);
        }

        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        heartRate = findViewById(R.id.heartRate);
        nowDisplay = findViewById(R.id.nowDisplay);

        // initial display
        nowDisplay.setText("");
        heartRate.setText("Heart Rate");

        // create sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // create heart rate sensor
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        // set start button click listener
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if sensor is registered
                startButtonAction();
            }
        });


        // set stop button click listener
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // unregister sensor
                stopButtonAction();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
    /**
     * Sends the data, note this is a broadcast, so we will get the message as well.
     */

    public void startButtonAction(){
        boolean sensorRegistered = sensorManager.registerListener(MainActivity.this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        if (sensorRegistered == true) {
            nowDisplay.setText("Started");
        }
    }

    public void stopButtonAction(){
        sendData("-1");
        sensorManager.unregisterListener(MainActivity.this);
        nowDisplay.setText("Stopped");
    }

    private void sendData(String message) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(datapath);
        dataMap.getDataMap().putString("message", message);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "Sending message was successful: " + dataItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                })
        ;
    }

    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(MainActivity.this);
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void  onSensorChanged(SensorEvent event) {
        // get heart rate
        int rate = Math.round(event.values[0]);
        String hr = Integer.toString(rate);
        heartRate.setText(hr);
        sendData(hr);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //receive data from the path.
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged: " + dataEventBuffer);

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    //Log.v(TAG, "Wear activity received message: " + message);
                    // Display message in UI
                    if(message.equalsIgnoreCase("start")){
                        //nowDisplay.setText(message);
                        startBtn.performClick();
                    }
                    else if(message.equalsIgnoreCase("stop")){
                        //nowDisplay.setText("stopmus");
                        stopBtn.performClick();
                    }
                    else{
                        Log.v(TAG, "Wear activity received message but does not recognize: " + message);
                    }

                } else {
                    Log.e(TAG, "Unrecognized path: " + path);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "Data deleted : " + event.getDataItem().toString());
            } else {
                Log.e(TAG, "Unknown data event Type = " + event.getType());
            }
        }
    }
}