package whats_the_weather.nikita.com.whats_the_weather;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;
import com.loopj.android.http.AsyncHttpClient;
import org.apache.http.Header;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Calendar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private LocationManager manager;
    SharedPreferences mySharedPreferences;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    double convertTemp(double t)
    {
        return (5.0 / 9.0) * (t - 32.0);
    }

    private final String TAG = "MainActivity";
    private final String PrefName = "default";

    private boolean moreInfoState = false;

    Double Latit = 59.972805, Longit = 30.3032058;

    private void setWeatherIcon(ImageView v, String cond)
    {
        switch (cond)
        {
            case "clear-day":{v.setImageResource(R.drawable.sun); break;}
            case "clear-night":{v.setImageResource(R.drawable.clear_night); break;}
            case "rain":{v.setImageResource(R.drawable.rain); break;}
            case "snow":{v.setImageResource(R.drawable.snow); break;}
            case "sleet":{v.setImageResource(R.drawable.sleet); break;}
            case "wind":{v.setImageResource(R.drawable.wind); break;}
            case "fog":{v.setImageResource(R.drawable.fog); break;}
            case "cloudy":{v.setImageResource(R.drawable.cloudy); break;}
            case "partly-cloudy-day":{v.setImageResource(R.drawable.partly_cloudy_day); break;}
            case "partly-cloudy-night":{v.setImageResource(R.drawable.cloudy); break;}
            default: {v.setImageResource(R.drawable.cloudy); break;}
        }
    }

    private String setWeatherStatus(String cond)
    {
        switch (cond)
        {
            case "clear-day":{return "Clear day";}
            case "clear-night":{return "Clear night";}
            case "rain":{return "Rain";}
            case "snow":{return "Snow";}
            case "sleet":{return "Sleet";}
            case "wind":{return "Wind";}
            case "fog":{return "Fog";}
            case "cloudy":{return "Cloudy";}
            case "partly-cloudy-day":{return "Partly-cloudy";}
            case "partly-cloudy-night":{return "Partly-cloudy";}
            default: {return "Cloudy";}
        }
    }

    private String setDate(int m)
    {
        String Month="";
        switch (m)
        {
            case 0:{Month="January";break;}
            case 1:{Month="February";break;}
            case 2:{Month="March";break;}
            case 3:{Month="April";break;}
            case 4:{Month="May";break;}
            case 5:{Month="June";break;}
            case 6:{Month="July";break;}
            case 7:{Month="August";break;}
            case 8:{Month="September";break;}
            case 9:{Month="October";break;}
            case 10:{Month="November";break;}
            case 11:{Month="December";break;}
            default:{Month="January";break;}

        }
        return Month;
    }

    private void getForecast()
    {
        RequestBuilder weather = new RequestBuilder();

        Request request = new Request();

        request.setLat(Double.toString(Latit)); //Latitude - ширина
        request.setLng(Double.toString(Longit)); //Longitude - долгота
        request.setUnits(Request.Units.US);
        request.setLanguage(Request.Language.PIG_LATIN);
        request.addExcludeBlock(Request.Block.CURRENTLY);

        weather.getWeather(request,new Callback<WeatherResponse>()
        {
            @Override
            public void success(WeatherResponse weatherResponse, Response response)
            {



                //weatherResponse.getDaily().getData().get(0).
                Log.w(TAG, Double.toString(weatherResponse.getHourly().getData().get(0).getTemperature()));

                /*************************************** Weather today*************************************************/
                TextView todayTemperature = (TextView) findViewById(R.id.todayTemperature);
                TextView nowTemperature = (TextView) findViewById(R.id.nowTemperature);
                TextView todayStatus = (TextView) findViewById(R.id.todayStatus);
                ImageView mainImage = (ImageView) findViewById(R.id.mainImage);

                Double temptmin = convertTemp(weatherResponse.getDaily().getData().get(0).getTemperatureMin());
                Double temptmax = convertTemp(weatherResponse.getDaily().getData().get(0).getTemperatureMax());
                Double nowTmp = convertTemp(weatherResponse.getHourly().getData().get(0).getTemperature());

                todayTemperature.setText(new DecimalFormat("#0.00").format(temptmin).concat("..").concat(new DecimalFormat("#0.00").format(temptmax).concat(" °C")));
                nowTemperature.setText(new DecimalFormat("#0.00").format(nowTmp).concat(" °C"));
                todayStatus.setText(setWeatherStatus(weatherResponse.getHourly().getData().get(0).getIcon()));
                setWeatherIcon(mainImage, weatherResponse.getHourly().getData().get(0).getIcon());

               /*****************************************Next Day****************************************************/
                TextView dayTemperature1 = (TextView) findViewById(R.id.dayTemperature1);
                TextView dayStatus1=(TextView) findViewById(R.id.dayStatus1);
                ImageView dayImage1 = (ImageView) findViewById(R.id.dayImage1);
                TextView dayName1=(TextView) findViewById(R.id.dayName1);

                Calendar c = Calendar.getInstance();
                dayName1.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin1 = convertTemp(weatherResponse.getDaily().getData().get(1).getTemperatureMin());
                Double tempmax1 = convertTemp(weatherResponse.getDaily().getData().get(1).getTemperatureMax());

                dayTemperature1.setText(new DecimalFormat("#0.00").format(tempmin1).concat("..").concat(new DecimalFormat("#0.00").format(tempmax1).concat(" °C")));
                dayStatus1.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(1).getIcon()));
                setWeatherIcon(dayImage1, weatherResponse.getDaily().getData().get(1).getIcon());

                /*****************************************Second Day****************************************************/

                TextView dayTemperature2 = (TextView) findViewById(R.id.dayTemperature2);
                TextView dayStatus2=(TextView) findViewById(R.id.dayStatus2);
                ImageView dayImage2 = (ImageView) findViewById(R.id.dayImage2);
                TextView dayName2=(TextView) findViewById(R.id.dayName2);
                c.add(Calendar.DAY_OF_YEAR, 1);
                dayName2.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin2 = convertTemp(weatherResponse.getDaily().getData().get(2).getTemperatureMin());
                Double tempmax2 = convertTemp(weatherResponse.getDaily().getData().get(2).getTemperatureMax());

                dayTemperature2.setText(new DecimalFormat("#0.00").format(tempmin2).concat("..").concat(new DecimalFormat("#0.00").format(tempmax2).concat(" °C")));
                dayStatus2.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(2).getIcon()));
                setWeatherIcon(dayImage2, weatherResponse.getDaily().getData().get(2).getIcon());

                /*****************************************Third Day****************************************************/
                TextView dayTemperature3 = (TextView) findViewById(R.id.dayTemperature3);
                TextView dayStatus3=(TextView) findViewById(R.id.dayStatus3);
                ImageView dayImage3 = (ImageView) findViewById(R.id.dayImage3);
                TextView dayName3=(TextView) findViewById(R.id.dayName3);
                c.add(Calendar.DAY_OF_YEAR, 1);
                dayName3.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin3 = convertTemp(weatherResponse.getDaily().getData().get(3).getTemperatureMin());
                Double tempmax3 = convertTemp(weatherResponse.getDaily().getData().get(3).getTemperatureMax());

                dayTemperature3.setText(new DecimalFormat("#0.00").format(tempmin3).concat("..").concat(new DecimalFormat("#0.00").format(tempmax3).concat(" °C")));
                dayStatus3.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(3).getIcon()));
                setWeatherIcon(dayImage3, weatherResponse.getDaily().getData().get(3).getIcon());
                /*****************************************Fourth Day****************************************************/
                TextView dayTemperature4 = (TextView) findViewById(R.id.dayTemperature4);
                TextView dayStatus4=(TextView) findViewById(R.id.dayStatus4);
                ImageView dayImage4 = (ImageView) findViewById(R.id.dayImage4);
                TextView dayName4=(TextView) findViewById(R.id.dayName4);
                c.add(Calendar.DAY_OF_YEAR, 1);
                dayName4.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin4 = convertTemp(weatherResponse.getDaily().getData().get(4).getTemperatureMin());
                Double tempmax4 = convertTemp(weatherResponse.getDaily().getData().get(4).getTemperatureMax());

                dayTemperature4.setText(new DecimalFormat("#0.00").format(tempmin4).concat("..").concat(new DecimalFormat("#0.00").format(tempmax4).concat(" °C")));
                dayStatus4.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(4).getIcon()));
                setWeatherIcon(dayImage4, weatherResponse.getDaily().getData().get(4).getIcon());
                /*****************************************Fifth Day****************************************************/
                TextView dayTemperature5 = (TextView) findViewById(R.id.dayTemperature5);
                TextView dayStatus5=(TextView) findViewById(R.id.dayStatus5);
                ImageView dayImage5 = (ImageView) findViewById(R.id.dayImage5);
                TextView dayName5=(TextView) findViewById(R.id.dayName5);
                c.add(Calendar.DAY_OF_YEAR, 1);
                dayName5.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin5 = convertTemp(weatherResponse.getDaily().getData().get(5).getTemperatureMin());
                Double tempmax5 = convertTemp(weatherResponse.getDaily().getData().get(5).getTemperatureMax());

                dayTemperature5.setText(new DecimalFormat("#0.00").format(tempmin5).concat("..").concat(new DecimalFormat("#0.00").format(tempmax5).concat(" °C")));
                dayStatus5.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(5).getIcon()));
                Log.e(TAG,weatherResponse.getDaily().getData().get(5).getIcon());
                setWeatherIcon(dayImage5, weatherResponse.getDaily().getData().get(5).getIcon());
                /*****************************************Sixth Day****************************************************/
                TextView dayTemperature6 = (TextView) findViewById(R.id.dayTemperature6);
                TextView dayStatus6=(TextView) findViewById(R.id.dayStatus6);
                ImageView dayImage6 = (ImageView) findViewById(R.id.dayImage6);
                TextView dayName6=(TextView) findViewById(R.id.dayName6);
                c.add(Calendar.DAY_OF_YEAR, 1);
                dayName6.setText((c.get(c.DAY_OF_MONTH)+1)+" "+setDate(c.get(c.MONTH)));

                Double tempmin6 = convertTemp(weatherResponse.getDaily().getData().get(6).getTemperatureMin());
                Double tempmax6 = convertTemp(weatherResponse.getDaily().getData().get(6).getTemperatureMax());

                dayTemperature6.setText(new DecimalFormat("#0.00").format(tempmin6).concat("..").concat(new DecimalFormat("#0.00").format(tempmax6).concat(" °C")));
                dayStatus6.setText(setWeatherStatus(weatherResponse.getDaily().getData().get(6).getIcon()));
                Log.e(TAG,weatherResponse.getDaily().getData().get(6).getIcon());
                setWeatherIcon(dayImage6, weatherResponse.getDaily().getData().get(6).getIcon());

                /* More Info section */

                TextView moreInfoTemp6 = (TextView) findViewById(R.id.moreInfoTemp6);
                ImageView moreInfoIcon6 = (ImageView) findViewById(R.id.moreInfoIcon6);
                TextView moreInfoTemp12 = (TextView) findViewById(R.id.moreInfoTemp12);
                ImageView moreInfoIcon12 = (ImageView) findViewById(R.id.moreInfoIcon12);
                TextView moreInfoTemp18 = (TextView) findViewById(R.id.moreInfoTemp18);
                ImageView moreInfoIcon18 = (ImageView) findViewById(R.id.moreInfoIcon18);
                TextView moreInfoTemp24 = (TextView) findViewById(R.id.moreInfoTemp24);
                ImageView moreInfoIcon24 = (ImageView) findViewById(R.id.moreInfoIcon24);

                Double mTemp = convertTemp(weatherResponse.getHourly().getData().get(6).getTemperature());
                String mStatus = weatherResponse.getHourly().getData().get(6).getIcon();
                moreInfoTemp6.setText(new DecimalFormat("#0.00").format(mTemp).concat(" °C ").concat(setWeatherStatus(mStatus)));
                setWeatherIcon(moreInfoIcon6, mStatus);

                mTemp = convertTemp(weatherResponse.getHourly().getData().get(12).getTemperature());
                mStatus = weatherResponse.getHourly().getData().get(12).getIcon();
                moreInfoTemp12.setText(new DecimalFormat("#0.00").format(mTemp).concat(" °C ").concat(setWeatherStatus(mStatus)));
                setWeatherIcon(moreInfoIcon12, mStatus);

                mTemp = convertTemp(weatherResponse.getHourly().getData().get(18).getTemperature());
                mStatus = weatherResponse.getHourly().getData().get(18).getIcon();
                moreInfoTemp18.setText(new DecimalFormat("#0.00").format(mTemp).concat(" °C ").concat(setWeatherStatus(mStatus)));
                setWeatherIcon(moreInfoIcon18, mStatus);

                mTemp = convertTemp(weatherResponse.getHourly().getData().get(24).getTemperature());
                mStatus = weatherResponse.getHourly().getData().get(24).getIcon();
                moreInfoTemp24.setText(new DecimalFormat("#0.00").format(mTemp).concat(" °C ").concat(setWeatherStatus(mStatus)));
                setWeatherIcon(moreInfoIcon24, mStatus);

            }
            @Override
            public void failure (RetrofitError retrofitError)
            {
                Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                Log.e(TAG, retrofitError.getMessage());
                retrofitError.printStackTrace();
            }
        });
    }

    private void printCity() throws IOException
    {
        //

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://geocode-maps.yandex.ru/1.x/?kind=locality&format=json&lang=en_US&results=1&geocode="
                .concat(Double.toString(Longit))
                .concat(",").concat(Double.toString(Latit)),  new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String tStr = "";
                try {
                    tStr = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    Log.e(TAG, tStr);
                    JSONObject data = new JSONObject(tStr);
                    String city = data.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("GeoObject").getString("name");
                    TextView t = (TextView) findViewById(R.id.city);
                    t.setText(city);
                    Log.w(TAG,city);
                } catch (JSONException e) {
                   // e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private Location getCoords()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new Location("");
        }

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        Location loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        PrintLocation();
        return loc;
    }

    private void PrintLocation() {
        try {
            printCity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.w(TAG, "Coords: ".concat(Double.toString(Latit)).concat(" ").concat(Double.toString(Longit)));
    }

    public void moreInformation(View v)
    {
        LinearLayout moreInfo = (LinearLayout) findViewById(R.id.moreInfo);
        TextView knowMore = (TextView) findViewById(R.id.knowMore);

        if (moreInfoState)
        {
            knowMore.setText("Know details");
            moreInfo.setVisibility(View.GONE);
            moreInfoState = false;
        }
        else
        {
            knowMore.setText("Hide details");
            moreInfo.setVisibility(View.VISIBLE);
            moreInfoState = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_get_home:{
                Latit = (double) mySharedPreferences.getFloat("latitude", 0);
                Longit = (double) mySharedPreferences.getFloat("longitude", 0);
                PrintLocation();
                getForecast();
                break;
            }
            case R.id.action_set_home:{
                SharedPreferences.Editor mEditor = mySharedPreferences.edit();
                mEditor.putFloat("latitude", Latit.floatValue());
                mEditor.putFloat("longitude", Longit.floatValue());
                mEditor.apply();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ForecastApi.create("70313c1821a205657ab10ada62c04740");

        mySharedPreferences = getSharedPreferences(PrefName, Context.MODE_PRIVATE);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location loc = getCoords();
        if (loc != null) {
            Latit = loc.getLatitude();
            Longit = loc.getLongitude();
        }
        else
        {
            Log.e(TAG, "loc == NULL");
        }
        PrintLocation();
        getForecast();

    }
}