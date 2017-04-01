package com.developers.rapidmedic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HospitalMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    CameraUpdate cu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        new NearHospital().execute();
    }
    private class NearHospital extends AsyncTask<Void,Void,Integer>{
        int res=0;
        ProgressDialog progressDialog;
        String name;
        String lat,lon;
        ArrayList<String> latList=new ArrayList<>();
        ArrayList<String> lonList=new ArrayList<>();
        List<Marker> markersList = new ArrayList<Marker>();
        LatLngBounds.Builder builder=new LatLngBounds.Builder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(HospitalMap.this);
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection httpURLConnection;
            BufferedReader bufferedReader;
            try {
                String response="";
                URL url=new URL("https://api.foursquare.com/v2/venues/search?ll=28.6091%2C77.0351&query=%27hospital%27&limit=5&oauth_token=DIPJICDGH2SDXJY2VBAGWCDDXBVPUZ02WOB3GMZCMOLOP4VJ&v=20170401");
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream=httpURLConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();
                String line;
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line+"/n");
                }
                response=buffer.toString();
                if(response.length()>0){
                    parseDetails(response);
                    res=1;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return res;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            for(int j=0;j<latList.size();j++){
                double latitude=Double.parseDouble(latList.get(j));
                double longitude=Double.parseDouble(lonList.get(j));
                LatLng location=new LatLng(latitude,longitude);
                Marker marker =mMap.addMarker(new MarkerOptions().position(location));
                markersList.add(marker);
            }
            for(Marker m:markersList){
                builder.include(m.getPosition());
            }
            int padding = 50;
            /**create the bounds from latlngBuilder to set into map camera*/
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    /**set animated zoom camera into map*/
                    mMap.animateCamera(cu);

                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent=new Intent(HospitalMap.this,MainActivity.class);
                    startActivity(intent);
                    return false;
                }
            });
            progressDialog.cancel();
        }
        private void parseDetails(String response){
            try{
                JSONObject result=new JSONObject(response);
                JSONObject hosresult=result.optJSONObject("response");
                JSONArray hospitalArr=hosresult.getJSONArray("venues");
                for(int i=0;i<hospitalArr.length();i++){
                    JSONObject hospitalObj=hospitalArr.getJSONObject(i);
                    name=hospitalObj.getString("name");
                    JSONObject locationObj=hospitalObj.getJSONObject("location");
                    lat=locationObj.getString("lat");
                    lon=locationObj.getString("lng");
                    Log.d("HospitalMap","Name: "+name+"lat "+lat+" lon: "+lon);
                    latList.add(lat);
                    lonList.add(lon);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
