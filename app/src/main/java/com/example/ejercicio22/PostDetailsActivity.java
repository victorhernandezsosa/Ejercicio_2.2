package com.example.ejercicio22;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewBody = findViewById(R.id.textViewBody);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("postId")) {
            int postId = intent.getIntExtra("postId", -1);
            if (postId != -1) {
                // Realizar una consulta al API 2 usando el ID del post
                // y mostrar los detalles del post en los TextView correspondientes.
                new FetchPostDetailsTask().execute(postId);
            }
        }
    }

    private class FetchPostDetailsTask extends AsyncTask<Integer, Void, String> {
        private static final String API_URL = "https://jsonplaceholder.typicode.com/posts/";

        @Override
        protected String doInBackground(Integer... params) {
            int postId = params[0];
            try {
                URL url = new URL(API_URL + postId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    inputStream.close();
                    return stringBuilder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String title = jsonObject.getString("title");
                    String body = jsonObject.getString("body");
                    textViewTitle.setText(title);
                    textViewBody.setText(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PostDetailsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
