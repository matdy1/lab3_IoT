package com.example.lab3.services;

import com.example.lab3.dto.Movies;
import com.example.lab3.dto.Numbers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesService {

    // de la forma /?apikey=bf81d461&i=tt389619
    @GET("/")
    Call<Movies> getMovies(@Query("i") String i,
                           @Query("apikey") String apikey);
}
