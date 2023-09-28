package com.example.vahan;

import static com.example.vahan.Service.CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private UniversityAdapter adapter;
    private List<University> universityList = new ArrayList<>();

    private static final String BASE_URL = "http://universities.hipolabs.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UniversityAdapter(universityList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UniversityApi universityApi = retrofit.create(UniversityApi.class);
        Call<List<University>> call = universityApi.getUniversities();

        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(@NonNull Call<List<University>> call, @NonNull Response<List<University>> response) {
                if (response.isSuccessful()) {
                    universityList.addAll(response.body());
                    for (University university : universityList) {
                        List<String> webPages = university.getWebpages();
                        if (webPages != null && !webPages.isEmpty()) {
                            university.setWebsite(webPages.get(0));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<University>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        startDataRefreshService();


    }
    private void startDataRefreshService() {
        createNotificationChannel();
        Intent serviceIntent = new Intent(this, Service.class);

        ArrayList<University> universityArrayList = new ArrayList<>(universityList);

        serviceIntent.putParcelableArrayListExtra("universities", universityArrayList);
        ContextCompat.startForegroundService(this, serviceIntent);
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


}
