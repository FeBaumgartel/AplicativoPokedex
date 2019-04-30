package com.example.apppokedex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class list_complex_item extends AppCompatActivity {

    private TextView tvNome;
    private TextView tvNum;
    private TextView tvPeso;
    private TextView tvAltura;
    private ImageView ivAppImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_complex_item);

        this.tvNome = findViewById(R.id.tvNome);
        this.tvAltura = findViewById(R.id.tvAltura);
        this.tvPeso = findViewById(R.id.tvPeso);
        this.tvNum = findViewById(R.id.tvNum);
        this.ivAppImg = findViewById(R.id.ivAppImg);

        Intent intent=getIntent();
        String frase = intent.getStringExtra("pokemon");
        String array[] = new String[5];
        array = frase.split(";");

        tvNome.setText(array[1]);
        tvNum.setText(array[0]);
        tvAltura.setText(array[3]);
        tvPeso.setText(array[4]);
        try {
            new DownloadImageTask(ivAppImg).execute(array[2].replace("http", "https")).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
