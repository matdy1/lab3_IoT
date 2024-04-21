package com.example.lab3.services;

import com.example.lab3.dto.Numbers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PrimeNumbers {

    @GET("/primeNumbers?len=999&order=1")
    Call<List<Numbers>> getNumbers();
}
