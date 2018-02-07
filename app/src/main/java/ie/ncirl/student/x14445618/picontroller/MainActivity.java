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

    //String Declarations - to define what action the sensor does (true/false = publish/don't publish)
    String publishTemperature = "false";
    String publishHumid = "false";
    String publishLed = "false";

    //Switch Declarations
    Switch tempSw;
    Switch humidSw;
    Switch ledSw;

    //Textviews which display the status of the sensor (ON/OFF)
    TextView temperatureStatusTv;
    TextView humidityStatusTv;
    TextView ledStatusTv;

    //Edit Text Fields which allow user to enter the sensor interval time
    EditText tempSampleIntervalEt;
    EditText humiditySampleIntervalEt;

    //Int which stores the values parsed interval times from the EditText Field
    int tempSampleInterval =5; //Default the time Intervals sent from the Application to 5 seconds
    int humidityInterval =5;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureStatusTv = findViewById(R.id.temperatureStatusTv);
        humidityStatusTv = findViewById(R.id.humidityStatusTv);
        ledStatusTv = findViewById(R.id.ledStatusTv);


        tempSampleIntervalEt = findViewById(R.id.temperatureSampleIntervalEt);
        humiditySampleIntervalEt = findViewById(R.id.humiditySampleIntervalEt);

        //Declare switch and logic
        tempSw= findViewById(R.id.temperatureSwitch);
        tempSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Parse String from EditText to Int Resolving Issues From: https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
                try{
                    tempSampleInterval = Integer.parseInt(tempSampleIntervalEt.getText().toString());
                }
                catch(NumberFormatException nfe) {
                    System.out.println(nfe);
                };

                if(isChecked ==true){

                    /*if(tempSampleInterval<=1){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
                        Toast.makeText(MainActivity.this, "Please enter a valid value...", Toast.LENGTH_SHORT).show();
                        tempSw.setChecked(false);
                    }*/
                    //else{
                        publishTemperature = "true";//Turn Sensor On
                        temperatureStatusTv.setText("Temperature Sensor is ON \n " + "Interval: " + tempSampleInterval + " sec");
                        tempSampleIntervalEt.setText("");
                        sendJsonToDweet();//Update the Dweet.io API with the specified values
                    //}

                }
                else{
                    publishTemperature = "false";//Turn Sensor Off
                    temperatureStatusTv.setText("Temperature Sensor is OFF");
                    tempSampleIntervalEt.setText("");
                    sendJsonToDweet();
                }
            }
        });

        humidSw = findViewById(R.id.humiditySwitch);
        humidSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Parse String from EditText to Int Resolving Issues From: https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
                try{
                    humidityInterval  = Integer.parseInt(humiditySampleIntervalEt.getText().toString());
                }
                catch(NumberFormatException nfe) {
                    System.out.println(nfe);
                };

                if(isChecked ==true){

                    /*if(humiditySampleInterval<=1){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
                        Toast.makeText(MainActivity.this, "Please enter a valid value...", Toast.LENGTH_SHORT).show();
                        humidSw.setChecked(false);
                    }*/
                    //else{
                        publishHumid = "true";//Turn Sensor On
                        humidityStatusTv.setText("Humidity Sensor is ON \n " + "Interval: " + humidityInterval + " sec");
                        humiditySampleIntervalEt.setText("");
                        sendJsonToDweet();//Update the Dweet.io API with the specified values

                   // }

                }
                else{
                    publishHumid = "false";//Turn Sensor Off
                    humidityStatusTv.setText("Humidity Sensor is OFF");
                    humiditySampleIntervalEt.setText("");
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
            dweetJsonObj.put("publishTemp",publishTemperature);
            dweetJsonObj.put("publishHumid",publishHumid);
            dweetJsonObj.put("tempTime",tempSampleInterval);
            //dweetJsonObj.put("humidityTime",humidityInterval);
            //dweetJsonObj.put("publishLed",publishLed);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "https://dweet.io/dweet/for/leonsPhone9";
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
