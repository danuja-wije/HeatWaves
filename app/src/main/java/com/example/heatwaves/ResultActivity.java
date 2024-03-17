package com.example.heatwaves;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class WeatherData {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getGeneralRecommendation() {
        return generalRecommendation;
    }

    public void setGeneralRecommendation(String generalRecommendation) {
        this.generalRecommendation = generalRecommendation;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getHeatIndex() {
        return heatIndex;
    }

    public void setHeatIndex(String heatIndex) {
        this.heatIndex = heatIndex;
    }

    private String date;
    private String temperature;
    private String humidity;
    private String heatIndex;
    private String alertLevel;
    private String generalRecommendation;

    // Constructor, getters, and setters
}

// Custom adapter for RecyclerView
class WeatherDataAdapter extends RecyclerView.Adapter<WeatherDataAdapter.ViewHolder> {
    private List<WeatherData> weatherDataList;

    public WeatherDataAdapter(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_data_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherData data = weatherDataList.get(position);
        holder.dateTextView.setText(data.getDate());
        holder.temperatureTextView.setText(String.valueOf(data.getTemperature()) + " °C");
        holder.humidityTextView.setText(String.valueOf(data.getHumidity()) + " %");
        holder.heatIndexTextView.setText(String.valueOf(data.getHeatIndex()) + " °C");
        holder.alertLevelTextView.setText(data.getAlertLevel());
        holder.recommendationTextView.setText(data.getGeneralRecommendation());
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, temperatureTextView, humidityTextView, heatIndexTextView, alertLevelTextView, recommendationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
            heatIndexTextView = itemView.findViewById(R.id.heatIndexTextView);
            alertLevelTextView = itemView.findViewById(R.id.alertLevelTextView);
            recommendationTextView = itemView.findViewById(R.id.recommendationTextView);
        }
    }
}

public class ResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WeatherDataAdapter adapter;
    private List<WeatherData> weatherDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        TextView resultTextView = findViewById(R.id.resultTextView);
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the result data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("resultData")) {
            String result = intent.getStringExtra("resultData");
            try {
                // Load JSON from local file storage
                FileInputStream fis = openFileInput("data.json");
                InputStreamReader isr = new InputStreamReader(fis);
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = isr.read()) != -1) {
                    builder.append((char) ch);
                }
                isr.close();

                // Parse JSON data
                JSONArray jsonArray = new JSONArray(builder.toString());
                Log.d("ResultActivity_come", "Parsed " +jsonArray );
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WeatherData data = new WeatherData();
                    data.setDate(jsonObject.getString("Date"));
                    data.setTemperature(jsonObject.getString("Temperature (°C)"));
                    data.setHumidity(jsonObject.getString("Humidity (%)"));
                    data.setHeatIndex(jsonObject.getString("Heat Index (°C)"));
                    data.setAlertLevel(jsonObject.getString("Alert Level"));
                    data.setGeneralRecommendation(jsonObject.getString("General Recommendation"));
                    weatherDataList.add(data);
                }

                // Set up RecyclerView with the parsed data
                adapter = new WeatherDataAdapter(weatherDataList);
                recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


//            resultTextView.setText(result);
        }
    }
//    private List<WeatherData> parseJsonData(String jsonData) {
//        List<WeatherData> dataList = new ArrayList<>();
//        try {
//            Log.d("ResultActivity_input", "Received JSON: " + jsonData);
//
//            JSONArray jsonArray = new JSONArray(jsonData);
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                WeatherData data = new WeatherData();
//                data.setDate(jsonObject.getString("Date"));
//                data.setTemperature(jsonObject.getString("Temperature"));
//                data.setHumidity(jsonObject.getString("Humidity"));
//                data.setHeatIndex(jsonObject.getString("Heat Index"));
//                data.setAlertLevel(jsonObject.getString("Alert Level"));
//                data.setGeneralRecommendation(jsonObject.getString("General Recommendation"));
//                dataList.add(data);
//            }
//            Log.d("ResultActivity_come", "Parsed " + dataList.size() + " items from JSON.");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("ResultActivity_error", "Error parsing JSON", e);
//        }
//        return dataList;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}