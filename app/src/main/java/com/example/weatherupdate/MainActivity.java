package com.example.weatherupdate;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText etCity, etCountry;
    TextView tvResult;
    private final String url ="https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "Generate your personal API key"; //create a personal token
    DecimalFormat df = new DecimalFormat( "#.##");

    Log d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvResult = findViewById(R.id.tvResult);
    }

    public void getWeatherInfo(View view) {
        String tempurl ="";
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("City field cannot be empty!");
        }else{
            if(!country.equals("")){
                tempurl = url + "?q=" + city + "," + country + "&appid=" + appid;
            }else{
                tempurl = url + "?q=" + city + "," + "&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("reponse", response);
                    String output = "";
                    try {
                        //create a JSON object using the string received from the API
                        JSONObject jsonResponse = new JSONObject(response);

                        //from the jsonOject saving the response from the API, get the param weather.
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0); //there is a JSON param called 0, is not an index.
                        String description = jsonObjectWeather.getString("description");  //this is the output for the weather description


                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main"); //get all elements from param main
                        //extract the variables from the main param
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;  //all temp in Kelvin, subtract to convert to Celsius
                        double feelslike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");  //hpa
                        int humidity = jsonObjectMain.getInt("humidity");  //percentage


                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        int wind = jsonObjectWind.getInt("speed");

                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");  //percentage of clouding

                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");  //names in json
                        String countryName = jsonObjectSys.getString("country");
                        //String cityName = jsonObjectSys.getString("name");  //these strings are how the api calls the variables, cannot be changed.

                        JSONObject jsonObjectCoord = jsonResponse.getJSONObject("coord");  //bring the coordinates, this one is mine
                        double lon = jsonObjectCoord.getDouble("lon");
                        double lat = jsonObjectCoord.getDouble("lat");


                        //create an output link concatenating all variables.
                        output += "Current weather in " + city +  " (" + countryName + ")"
                                + "\nLocated in Longitude " + df.format(lon) + " and Latitude " + df.format(lat) +","
                                + "\nTemperature: " + df.format(temp) + " °C"  // ASCII Alt + 0176 for degrees
                                + "\nFeels like : " + df.format(feelslike) + " °C"
                                + "\nHumidity: " + humidity + "%"
                                + "\nPressure: " + pressure + "hPa"
                                + "\nDescription: " + description
                                + "\nWind Speed: " + wind + "m/s (meters per second)"
                                + "\nCloudiness: " + clouds +"%";
                        tvResult.setText(output);
                        Log.i("API info", output);


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}