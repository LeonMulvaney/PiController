package ie.ncirl.student.x14445618.picontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Volley sample code is adapted from a tutorial @ http://www.truiton.com/2015/02/android-volley-example/
// This example is simple and easy to follow, and it is all we need for a simple HTTP request and callback through Volley

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener, Response.ErrorListener {

    private RequestQueue mQueue;
    private TextView resultTv;
    Map<String,String>  myMap = new HashMap<>();
    JSONObject dweetJsonObj = new JSONObject();

    //Declare Strings to define what action the sensor does
    String publishTemperature = "false";
    String publishHumid = "false";
    String publishLed = "false";

    int tempSampleInterval; //Default the temperature sensor to take readings every 5 seconds

    Switch tempSw;
    Switch humidSw;
    Switch ledSw;

    TextView temperatureStatusTv;
    TextView humidityStatusTv;
    TextView ledStatusTv;

    EditText tempSampleIntervalEt;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureStatusTv = findViewById(R.id.temperatureStatusTv);
        humidityStatusTv = findViewById(R.id.humidityStatusTv);
        ledStatusTv = findViewById(R.id.ledStatusTv);

        tempSampleIntervalEt = findViewById(R.id.temperatureSampleIntervalEt);

        tempSw= findViewById(R.id.temperatureSwitch);
        tempSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked ==true){
                    tempSampleInterval = Integer.parseInt(tempSampleIntervalEt.getText().toString());

                    if(tempSampleInterval<=1){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
                        Toast.makeText(MainActivity.this, "Please enter a valid time...", Toast.LENGTH_SHORT).show();
                        tempSw.setChecked(false);
                    }
                    else{
                        publishTemperature = "true";
                        temperatureStatusTv.setText("Temperature Sensor is ON");
                        sendJsonToDweet();
                    }

                }
                else{
                    publishTemperature = "false";
                    temperatureStatusTv.setText("Temperature Sensor is OFF");
                    sendJsonToDweet();
                }
            }
        });

        humidSw = findViewById(R.id.humiditySwitch);
        humidSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked ==true){
                    publishHumid = "true";
                    humidityStatusTv.setText("Humidity Sensor is ON");
                    sendJsonToDweet();
                }
                else{
                    publishHumid = "false";
                    humidityStatusTv.setText("Humidity Sensor is OFF");
                    sendJsonToDweet();
                }
            }
        });

        ledSw = findViewById(R.id.ledSwitch);
        ledSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked ==true){
                    publishLed = "true";
                    ledStatusTv.setText("LED is ON");
                    sendJsonToDweet();
                }
                else{
                    publishLed = "false";
                    ledStatusTv.setText("LED is OFF");
                    sendJsonToDweet();
                }
            }
        });


        resultTv = findViewById(R.id.resultTv);
        mQueue = CustomQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        myMap.put("Sending","True");
    }



    public void sendJsonToDweet() {
        try {
            dweetJsonObj.put("publish_temp",publishTemperature);
            dweetJsonObj.put("temperature_sample_interval",tempSampleInterval);
            dweetJsonObj.put("publish_humid",publishHumid);
            dweetJsonObj.put("publish_led",publishLed);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "https://dweet.io/dweet/for/leonsAndroid";
        final CustomJSONRequest jsonRequest = new CustomJSONRequest(Request.Method.POST, url,
                dweetJsonObj, this, this);
        jsonRequest.setTag("test");
        mQueue.add(jsonRequest);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {
        Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();
        resultTv.setText(response.toString());
    }


}
