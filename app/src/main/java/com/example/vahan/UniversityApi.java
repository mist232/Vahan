package com.example.vahan;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UniversityApi {
    @GET("search")
    Call<List<University>> getUniversities();
}
