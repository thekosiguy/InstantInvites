package com.example.instantinvites;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private GoogleMap mMap;
    private String data;

    @Override
    protected String doInBackground(Object... params) {
        mMap = (GoogleMap)params[0];
        String url = (String) params[1];
        BufferedReader bufferedReader;

        try{
            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();
            //Receiving value in json format
            bufferedReader = new BufferedReader((new InputStreamReader(is)));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            //Storing json-type data in data
            data = stringBuilder.toString();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();;
        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        try{
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultsArray = parentObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++){
                JSONObject jsonObject = resultsArray.getJSONObject(i);
                JSONObject locationObject = jsonObject.getJSONObject("geometry")
                        .getJSONObject("location");
                String latitude = locationObject.getString("lat");
                String longitude = locationObject.getString("lng");

                JSONObject nameObject = resultsArray.getJSONObject(i);
                String name_object = nameObject.getString("name");
                String vicinity = nameObject.getString("vicinity");

                LatLng latLngCurrent = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name_object);
                markerOptions.snippet(vicinity);
                markerOptions.position(latLngCurrent);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(markerOptions);

            }
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

}
