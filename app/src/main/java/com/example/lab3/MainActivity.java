package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EditText editText;
    //TypicodeService typicodeSerice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText = findViewById(R.id.idPelicula);

        binding.irContadorPrimos.setOnClickListener(view -> {
            Intent intent = new Intent(this, CounterPrimesActivity.class);
            startActivity(intent);

        });

        binding.irVentanaPeliculas.setOnClickListener(view -> {
            Intent intent = new Intent(this, MoviesActivity.class);
            String texto = editText.getText().toString();
            intent.putExtra("idPelicula", texto);
            startActivity(intent);
        });
    }
}