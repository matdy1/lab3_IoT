package com.example.lab3;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3.databinding.ActivityCounterPrimesBinding;
import com.example.lab3.dto.Numbers;
import com.example.lab3.services.PrimeNumbers;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CounterPrimesActivity extends AppCompatActivity {

    private ActivityCounterPrimesBinding binding;
    private PrimeNumbers primeNumbers;
    private List<Numbers> primeNumbersList;
    private int currentIndex = 0;
    private boolean isPaused = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAscendente = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCounterPrimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Verificar conectividad a Internet
        if (tengoInternet()) {
            // Inicializar Retrofit y configurar la interfaz PrimeNumbers
            primeNumbers = new Retrofit.Builder()
                    .baseUrl("https://prime-number-api.onrender.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PrimeNumbers.class);

            // Obtener números primos desde la API
            fetchNumbersFromWs();
        } else {
            // Mostrar mensaje de error si no hay conexión a Internet
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }

        binding.regresar1.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        binding.pausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    resumeCounter();
                    binding.pausa.setText("Pausa");
                    binding.ascDes.setVisibility(View.VISIBLE);
                } else {
                    pauseCounter();
                    binding.pausa.setText("Reinicio");
                    binding.ascDes.setVisibility(View.GONE);
                }
            }
        });

        binding.ascDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.removeCallbacksAndMessages(null);
                toggleDirection(); // Cambiar la dirección del conteo
                // Actualizar el texto del botón según la nueva dirección del conteo
                binding.modoConteo.setText(isAscendente ? "Cuenta Asccendente" : "Cuenta Descendente");
                // Lógica adicional según la nueva dirección del conteo (si es necesario)
                if(isAscendente){
                    showNextPrime();
                } else {
                    showPreviousPrime();
                }
            }
        });

        binding.buscarPrimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orderToSearch = Integer.parseInt(binding.ordenPrimo.getText().toString());
                searchAndStartFromPrime(orderToSearch);
            }
        });


    }

    public void fetchNumbersFromWs() {
        primeNumbers.getNumbers().enqueue(new Callback<List<Numbers>>() {
            @Override
            public void onResponse(Call<List<Numbers>> call, Response<List<Numbers>> response) {
                if (response.isSuccessful()) {
                    primeNumbersList = response.body();
                    // Mostrar el primer número primo
                    showNextPrime();
                }
            }
            @Override
            public void onFailure(Call<List<Numbers>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showNextPrime() {
        if (currentIndex < primeNumbersList.size() && !isPaused) {
            Numbers numbers = primeNumbersList.get(currentIndex);
            binding.contadorPrimo.setText(String.valueOf(numbers.getNumber()));

            currentIndex++;

            // Delay para mostrar el siguiente número primo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNextPrime();
                }
            }, TimeUnit.SECONDS.toMillis(1)); // Esperar 1 segundo antes de mostrar el siguiente número primo
        }
    }

    private void showPreviousPrime() {
        if (currentIndex > 0 && !isPaused) {
            currentIndex--; // Decrementar el índice para obtener el número primo anterior
            Numbers numbers = primeNumbersList.get(currentIndex);
            binding.contadorPrimo.setText(String.valueOf(numbers.getNumber()));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPreviousPrime();
                }
            }, TimeUnit.SECONDS.toMillis(1)); // Esperar 1 segundo antes de mostrar el siguiente número primo
        }
    }


    private void searchAndStartFromPrime(int order) {
        for (Numbers numbers : primeNumbersList) {
            if (numbers.getOrder() == order) {
                currentIndex = primeNumbersList.indexOf(numbers);
                // Mostrar el número encontrado
                binding.contadorPrimo.setText(String.valueOf(numbers.getNumber()));
                // Esperar 1 segundo antes de continuar el contador
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNextPrime(); // Comenzar a mostrar números primos a partir del número encontrado
                    }
                }, TimeUnit.SECONDS.toMillis(1));
                return;
            }
        }
        // Si no se encuentra el número primo con el orden especificado, mostrar un mensaje de error
        Toast.makeText(this, "No se encontró ningún número primo con el orden especificado", Toast.LENGTH_SHORT).show();
    }



    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Método para pausar el contador
    private void pauseCounter() {
        isPaused = true;
        handler.removeCallbacksAndMessages(null); // Detener el manejador para detener el conteo
    }

    // Método para reanudar el contador
    private void resumeCounter() {
        isPaused = false;
        // Reanudar el contador desde el último número mostrado, manteniendo la dirección
        if (isAscendente) {
            showNextPrime();
        } else {
            showPreviousPrime();
        }
    }

    private void toggleDirection() {
        isAscendente = !isAscendente; // Cambiar el estado de ascendente a descendente o viceversa
    }


}
