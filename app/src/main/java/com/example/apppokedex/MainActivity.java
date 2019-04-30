package com.example.apppokedex;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listview;
    private List listaPokemon = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listView);

        DownloadDeDados downloadDeDados = new DownloadDeDados();
        downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent=new Intent(MainActivity.this, list_complex_item.class);
                ((Intent) intent).putExtra("pokemon",listaPokemon.get(position).toString());
                startActivity(intent);
            }
        });
    }


    private class DownloadDeDados extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonFeed = downloadJson(strings[0]);
            if (jsonFeed == null) {
                Toast.makeText(MainActivity.this, "DownloadError", Toast.LENGTH_SHORT).show();
            }
            return jsonFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONTokener jsonTokener = new JSONTokener(s);
            try {
                JSONObject json = new JSONObject(jsonTokener);
                JSONArray jsonArray = json.getJSONArray("pokemon");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JsonEntry jsonPokemon = new JsonEntry(
                            jsonArray.getJSONObject(i).getString("id"),
                            jsonArray.getJSONObject(i).getString("num"),
                            jsonArray.getJSONObject(i).getString("name"),
                            jsonArray.getJSONObject(i).getString("img"),
                            jsonArray.getJSONObject(i).getString("height"),
                            jsonArray.getJSONObject(i).getString("weight"));
                    listaPokemon.add(jsonPokemon);
                }

                JsonListAdapter rssListAdapter = new JsonListAdapter(MainActivity.this,
                        R.layout.list_complex_item, listaPokemon);
                listview.setAdapter(rssListAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadJson(String urlString) {
            StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int resposta = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                int charsLidos;
                char[] inputBuffer = new char[5000];
                while (true) {
                    charsLidos = reader.read(inputBuffer);
                    if (charsLidos < 0) {
                        break;
                    }
                    if (charsLidos > 0) {
                        json.append(
                                String.copyValueOf(inputBuffer, 0, charsLidos));
                    }
                }
                reader.close();
                return json.toString();

            } catch (MalformedURLException e) {
                Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "IO Error In Download", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

}
