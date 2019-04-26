package com.example.apppokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JsonListAdapter extends ArrayAdapter {
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<JsonEntry> aplicativos;

    public JsonListAdapter(Context context, int resource, List<JsonEntry> aplicativos) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.aplicativos = aplicativos;
    }

    @Override
    public int getCount() {
        return aplicativos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JsonEntry appAtual = aplicativos.get(position);

        viewHolder.tvNome.setText(appAtual.getNome());
        viewHolder.tvAltura.setText(appAtual.getHeight());
        viewHolder.tvPeso.setText(appAtual.getWeight());
        viewHolder.tvNum.setText(appAtual.getNum());

        try {
            new DownloadImageTask(viewHolder.ivAppImg).execute(appAtual.getImgUrl().replace("http", "https")).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        final TextView tvNome;
        final TextView tvAltura;
        final TextView tvPeso;
        final ImageView ivAppImg;
        final TextView tvNum;

        ViewHolder(View v) {
            this.tvNome = v.findViewById(R.id.tvNome);
            this.tvAltura = v.findViewById(R.id.tvAltura);
            this.tvPeso = v.findViewById(R.id.tvPeso);
            this.tvNum = v.findViewById(R.id.tvNum);
            this.ivAppImg = v.findViewById(R.id.ivAppImg);
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