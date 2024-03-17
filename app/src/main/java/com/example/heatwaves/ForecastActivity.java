package com.example.heatwaves;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ForecastActivity extends AppCompatActivity implements AsyncTaskCallback{

    private EditText fullName, dateOfBirth, currentWeight, healthConditions,
            allergies, diureticsDosage, betaBlockersDosage, otherMedications, workDay, waterIntake,
            effectOnHydration, feelWhenDehydrated, severeDehydrationExperience, hydrationHabits, monitoringTools;
    private Spinner gender, generalHealth;
    private CheckBox diureticsMedication, betaBlockersMedication, medicalRecordsConsent;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Initialize all your UI components
        fullName = findViewById(R.id.fullName);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        gender = findViewById(R.id.gender);
        currentWeight = findViewById(R.id.currentWeight);
        healthConditions = findViewById(R.id.healthConditions);
        allergies = findViewById(R.id.allergies);
        generalHealth = findViewById(R.id.general_health);
        diureticsMedication = findViewById(R.id.diuretics);
        diureticsDosage = findViewById(R.id.diuretics_dosage);
        betaBlockersMedication = findViewById(R.id.beta_blockers);
        betaBlockersDosage = findViewById(R.id.beta_blockers_dosage);
        otherMedications = findViewById(R.id.other_dosage);
        medicalRecordsConsent = findViewById(R.id.medicalRecordsConsent);
        workDay = findViewById(R.id.work_day);
        waterIntake = findViewById(R.id.water_intake);
        effectOnHydration = findViewById(R.id.effect_dehydration);
        feelWhenDehydrated = findViewById(R.id.feel_dehydrated);
        severeDehydrationExperience = findViewById(R.id.severe_dehydration);
        hydrationHabits = findViewById(R.id.dehydration_habbits);
        monitoringTools = findViewById(R.id.tools);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        // Here, you'd collect the data from the form. This is a simplistic representation.
        // You might want to validate the inputs before proceeding.
        String name = fullName.getText().toString();
        String dob = dateOfBirth.getText().toString();
        // Repeat for other fields...

        // For CheckBoxes
        boolean isDiureticsMedicated = diureticsMedication.isChecked();
        // Repeat for other CheckBox fields...

        // For Spinner (Dropdown)
        String selectedGender = gender.getSelectedItem().toString();
        String healthCondition = generalHealth.getSelectedItem().toString();
        // And so on...

        // After collecting, you can now proceed to send this data to your server or perform some action with it.
        // For example, showing a Toast message:
        Toast.makeText(this, "Form Submitted", Toast.LENGTH_SHORT).show();

        Map<String, String> inputData = new HashMap<>();
        inputData.put("fullName", fullName.getText().toString());
        inputData.put("dateOfBirth", dateOfBirth.getText().toString());
        inputData.put("currentWeight", currentWeight.getText().toString());
        inputData.put("healthConditions", healthConditions.getText().toString());
        inputData.put("allergies", allergies.getText().toString());
        inputData.put("gender", gender.getSelectedItem().toString());
        new SendDataAsyncTask(this, inputData).execute();
        // Add your network request or other logic here
    }
    @Override
    public void onTaskComplete(String result) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("resultData", result);
        startActivity(intent);
    }

    private static class SendDataAsyncTask extends AsyncTask<Void, Void, String> {
        private WeakReference<AsyncTaskCallback> callbackReference;
        private Map<String, String> inputData;

        SendDataAsyncTask(AsyncTaskCallback callback, Map<String, String> inputData) {
            this.callbackReference = new WeakReference<>(callback);
            this.inputData = inputData;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://10.0.2.2:5000/predict");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                JSONObject jsonInput = new JSONObject(inputData);
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            AsyncTaskCallback callback = callbackReference.get();
            if (callback != null) {
                callback.onTaskComplete(result);
            }
        }
    }
}