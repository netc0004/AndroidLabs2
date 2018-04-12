package com.example.svetlana.androidlabs2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class WeatherForecast extends AppCompatActivity {

    TextView temperatureCurrent;
    TextView temperatureMin;
    TextView temperatureMax;
    TextView windSpeed;
    ImageView CurrentWeatherImage;

    public static final String LOCAL_IMAGE = "local";
    public static final String DOWNLOAD_IMAGE = "download";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        temperatureCurrent = (TextView) findViewById(R.id.temp_textView);
        temperatureMin = (TextView) findViewById(R.id.temp_min_textView);
        temperatureMax = (TextView) findViewById(R.id.temp_max_textView);

        windSpeed = (TextView)findViewById(R.id.wind_speed_textView);
        CurrentWeatherImage = (ImageView) findViewById(R.id.weather_imageView);

        String weatherURL =
                "http://api.openweathermap.org/data/2.5/weather?q=ottawa," +
                        "ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";
        ForecastQuery forecast_Query = new ForecastQuery();
        forecast_Query.execute(weatherURL);
    }

    public static Bitmap getImage(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        String strTemperatureCurrent;
        String strTemperatureMin;
        String strTemperatureMax;
        String strWindSpeed;
        String iconName;
        private String fileName;
        Bitmap image;
        ProgressBar prBar = (ProgressBar)findViewById(R.id.normProgBar);

        @Override
        protected String doInBackground(String... params) {
            String weather_URL = params[0];
            try {
                URL url = new URL(weather_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(conn.getInputStream(), null);
                    parser.nextTag();

                    while (parser.next() != XmlPullParser.END_DOCUMENT) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String name = parser.getName();

                        if (name.equals("temperature")) {
                            strTemperatureCurrent = parser.getAttributeValue(null, "value");
                            SystemClock.sleep(1000);
                            publishProgress(20);
                            strTemperatureMin = parser.getAttributeValue(null, "min");
                            SystemClock.sleep(1000);
                            publishProgress(40);
                            strTemperatureMax = parser.getAttributeValue(null, "max");
                            SystemClock.sleep(1000);
                            publishProgress(60);
                        }

                        if(name.equals("speed")) {
                            strWindSpeed = parser.getAttributeValue(null, "value");
                            SystemClock.sleep(1000);
                            publishProgress(80);
                        }
                        if (name.equals("weather")) {
                            iconName = parser.getAttributeValue(null, "icon");
                            String imageURL_Str = "http://openweathermap.org/img/w/" + iconName + ".png";
                            fileName = iconName + ".png";

                            File file = getBaseContext().getFileStreamPath(fileName);
                            if (file.exists()) {
                                FileInputStream fis = null;

                                try {
                                    fis = new FileInputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                image = BitmapFactory.decodeStream(fis);
                                Log.i(LOCAL_IMAGE, "Found the image locally");

                            } else {
                                image = getImage(new URL(imageURL_Str));
                                Log.i(DOWNLOAD_IMAGE, "Can't find image locally, download it");
                                FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }

                            if (image != null) {
                                publishProgress(100);
                            }
                        }
                    }
                } catch (XmlPullParserException ex) {
                    return null;
                }
                return (strTemperatureCurrent + " " + strTemperatureMin + " " + strTemperatureMax);

            } catch (IOException ioe) {
                Log.i("IOException", "Error");
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            prBar.setVisibility(View.VISIBLE);
            super.onProgressUpdate(progress);
            prBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            StringTokenizer text = new StringTokenizer(result);
            CurrentWeatherImage.setImageBitmap(image);
            prBar.setVisibility(View.INVISIBLE);
            temperatureCurrent.setText("Temperature " + text.nextElement() + "\u00b0");
            temperatureMin.setText("Minimum temperature " + text.nextElement() +"\u00b0");
            temperatureMax.setText("Maximum temperature " + text.nextElement() + "\u00b0");
            windSpeed.setText("Wind speed " + strWindSpeed);
        }
    }

}





