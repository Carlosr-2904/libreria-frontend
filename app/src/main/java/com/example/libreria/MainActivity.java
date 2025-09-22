package com.example.libreria;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private static final String API_BASE = "http://10.0.2.2:8080";
    private static final String LIBROS_URL = API_BASE + "/api/libros";

    private Button btnLoad;
    private ProgressBar progress;
    private TextView txtResult;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = findViewById(R.id.btnLoad);
        progress = findViewById(R.id.progress);
        txtResult = findViewById(R.id.txtResult);

        btnLoad.setOnClickListener(v -> fetchLibros());
    }

    private void fetchLibros() {
        setLoading(true);
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(LIBROS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Accept", "application/json");

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                String body = readAll(is);

                if (code >= 200 && code < 300) {
                    String pretty = parseLibros(body);
                    runOnUiThread(() -> {
                        txtResult.setText(pretty.isEmpty() ? "Sin resultados." : pretty);
                        setLoading(false);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "HTTP " + code, Toast.LENGTH_SHORT).show();
                        txtResult.setText("Error: " + body);
                        setLoading(false);
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error de red: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    txtResult.setText("No se pudo conectar con el servidor.");
                    setLoading(false);
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLoad.setEnabled(!loading);
    }

    private static String readAll(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String parseLibros(String json) {
        StringBuilder out = new StringBuilder();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o == null) continue;
                long id = o.optLong("id", -1);
                String titulo = o.optString("titulo", "(sin título)");
                String categoria = o.optString("categoria", "");
                long autorId = o.optLong("autorId", -1);
                long cant = o.optLong("cantidadDisponible", 0);

                out.append("• ID: ").append(id)
                        .append("\n  Título: ").append(titulo)
                        .append("\n  Categoría: ").append(categoria)
                        .append("\n  Autor ID: ").append(autorId)
                        .append("\n  Disponible: ").append(cant)
                        .append("\n\n");
            }
        } catch (JSONException e) {
            // Si no es un array (p.ej., el backend envió {}), muéstralo crudo
            out.append("Respuesta no esperada:\n").append(json);
        }
        return out.toString().trim();
    }
}