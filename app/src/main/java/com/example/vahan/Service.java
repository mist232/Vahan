package com.example.vahan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service extends android.app.Service {

    private static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "DataRefreshServiceChannel";
    private static final long REFRESH_INTERVAL_MS = 10 * 1000; // 10 seconds

    private Timer timer;
    private Handler handler;
    private UniversityAdapter universityAdapter; // Reference to the adapter
    private List<University> universityList=new ArrayList<>(); // The data to be refreshed


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();


        // Create a handler on the main thread for UI updates
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                // Update the UI or perform any necessary actions
                // Notify the RecyclerView of the data change
                if (universityAdapter != null) {
                    universityAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start a foreground service to keep it running even when the app is in the background
        startForeground(NOTIFICATION_ID, createNotification());

        // Retrieve the adapter from the intent
        List<University> universities = intent.getParcelableArrayListExtra("universities");

        // Create or recreate the UniversityAdapter
        if (universities != null) {
            universityList.clear();
            universityList.addAll(universities);
            if (universityAdapter == null) {
                universityAdapter = new UniversityAdapter(universityList, this);
            } else {
                universityAdapter.notifyDataSetChanged();
            }
        }


        // Schedule periodic data refresh
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Perform data refresh here
                fetchData();

                // Send a message to the handler for UI updates
                handler.sendEmptyMessage(0);
            }
        }, 0, REFRESH_INTERVAL_MS);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the timer when the service is destroyed
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Data Refresh Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Data Refresh Service")
                .setContentText("Refreshing data every 10 seconds")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    private void fetchData() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://universities.hipolabs.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UniversityApi universityApi = retrofit.create(UniversityApi.class);
        Call<List<University>> call = universityApi.getUniversities();

        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(@NonNull Call<List<University>> call, @NonNull Response<List<University>> response) {
                if (response.isSuccessful()) {
                    List<University> refreshedData = response.body();
                    if (universityAdapter != null) {
                        universityAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<University>> call, @NonNull Throwable t) {
                // Handle failure
                Toast.makeText(Service.this, "service not running", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
