package com.example.lab3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityMainBinding;
import com.example.lab3.databinding.ActivityMoviesBinding;
import com.example.lab3.dto.Movies;
import com.example.lab3.dto.Numbers;
import com.example.lab3.dto.Ratings;
import com.example.lab3.services.MoviesService;
import com.example.lab3.services.PrimeNumbers;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesActivity extends AppCompatActivity {

    private ActivityMoviesBinding binding;
    private MoviesService moviesService;
    private String apikey="bf81d461";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String id = getIntent().getStringExtra("idPelicula");

        if (tengoInternet()) {
            moviesService = new Retrofit.Builder()
                    .baseUrl("https://www.omdbapi.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MoviesService.class);


            fetchMoviesFromWs(id);
        } else {
            // Mostrar mensaje de error si no hay conexión a Internet
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }

        binding.regresar.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    public void fetchMoviesFromWs(String id) {
        moviesService.getMovies(id,apikey).enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if (response.isSuccessful()) {
                    Movies movies = response.body();
                    List<Ratings> ratingsList = (List<Ratings>) movies.getRatings();
                    binding.tituloPeli.setText(movies.getTitle());
                    binding.director.setText(movies.getDirector());
                    binding.actor.setText(movies.getActors());
                    binding.fecha.setText(movies.getReleased());
                    binding.genero.setText(movies.getGenre());
                    binding.escritor.setText(movies.getWriter());
                    binding.sinopsis.setText(movies.getPlot());
                    for (Ratings rating : ratingsList) {
                        if ("Internet Movie Database".equals(rating.getSource())) {
                            String imdbRating = rating.getValue();
                            binding.movieDB.setText(imdbRating);
                        }if("Rotten Tomatoes".equals(rating.getSource())){
                            String RottenRating = rating.getValue();
                            binding.rotten.setText(RottenRating);
                        }if("Metacritic".equals(rating.getSource())){
                            String MetaRating = rating.getValue();
                            binding.metacritic.setText(MetaRating);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}