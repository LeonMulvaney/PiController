package ie.ncirl.student.x14445618.picontroller;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    WebView webView;
    Boolean webViewVisible = false;

    JSONObject dweetJsonObj;

    //String Declarations - to define what action the sensor does (true/false = publish/don't publish)
    String publishTemperature = "false";
    String publishHumid = "false";
    String publishDistance = "false";
    String publishButton = "false";
    String publishLed = "false";


    //Switch Declarations
    Switch tempSw;
    Switch humidSw;
    Switch distanceSw;
    Switch buttonSw;
    Switch ledSw;


    //Textviews which display the status of the sensor (ON/OFF)
    TextView temperatureStatusTv;
    TextView humidityStatusTv;
    TextView distanceStatusTv;
    TextView buttonStatusTv;
    TextView ledStatusTv;

    //Edit Text Fields which allow user to enter the sensor interval time
    EditText tempSampleIntervalEt;
    EditText humiditySampleIntervalEt;
    EditText distanceSampleIntervalEt;
    EditText buttonSampleIntervalEt;

    //Int which stores the values parsed interval times from the EditText Field
    int tempSampleInterval =5; //Default the time Intervals sent from the Application to 5 seconds
    int humidityInterval =5;
    int distanceInterval  = 5;
    int buttonInterval = 5;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hiding/Showing Views From: https://stackoverflow.com/questions/5756136/how-to-hide-a-view-programmatically
        webView =  findViewById(R.id.dweetWebView);//Target WebView
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://dweet.io/follow/leonsrpi");



        dweetJsonObj = new JSONObject();

        temperatureStatusTv = findViewById(R.id.temperatureStatusTv);
        humidityStatusTv = findViewById(R.id.humidityStatusTv);
        distanceStatusTv = findViewById(R.id.distanceStatusTv);
        buttonStatusTv = findViewById(R.id.buttonStatusTv);
        ledStatusTv = findViewById(R.id.ledStatusTv);




        tempSampleIntervalEt = findViewById(R.id.temperatureSampleIntervalEt);
        humiditySampleIntervalEt = findViewById(R.id.humiditySampleIntervalEt);
        distanceSampleIntervalEt = findViewById(R.id.distanceSampleIntervalEt);
        buttonSampleIntervalEt = findViewById(R.id.buttonSampleIntervalEt);

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

                    /*if(tempSampleInterval<=2){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
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

                    /*if(humiditySampleInterval<=2){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
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

        distanceSw = findViewById(R.id.distanceSwitch);
        distanceSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Parse String from EditText to Int Resolving Issues From: https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
                try{
                    distanceInterval  = Integer.parseInt(distanceSampleIntervalEt.getText().toString());
                }
                catch(NumberFormatException nfe) {
                    System.out.println(nfe);
                };

                if(isChecked ==true){

                    /*if(humiditySampleInterval<=2){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
                        Toast.makeText(MainActivity.this, "Please enter a valid value...", Toast.LENGTH_SHORT).show();
                        distanceSw.setChecked(false);
                    }*/
                    //else{
                    publishDistance = "true";//Turn Sensor On
                    distanceStatusTv.setText("Distance Sensor is ON \n " + "Interval: " + distanceInterval + " sec");
                    distanceSampleIntervalEt.setText("");
                    sendJsonToDweet();//Update the Dweet.io API with the specified values

                    // }

                }
                else{
                    publishDistance = "false";//Turn Sensor Off
                    distanceStatusTv.setText("Distance Sensor is OFF");
                    distanceSampleIntervalEt.setText("");
                    sendJsonToDweet();
                }
            }
        });

        buttonSw= findViewById(R.id.buttonSwitch);
        buttonSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Parse String from EditText to Int Resolving Issues From: https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
                try{
                    buttonInterval = Integer.parseInt(buttonSampleIntervalEt.getText().toString());
                }
                catch(NumberFormatException nfe) {
                    System.out.println(nfe);
                };

                if(isChecked ==true){

                    /*if(tempSampleInterval<=2){ //Make sure the text is not blank and that or number is no less than 1 (Dweet limits api to 1 sec)
                        Toast.makeText(MainActivity.this, "Please enter a valid value...", Toast.LENGTH_SHORT).show();
                        buttonSw.setChecked(false);
                    }*/
                    //else{
                    publishButton = "true";//Turn Sensor On
                    buttonStatusTv.setText("Button Actuator is ON \n " + "Interval: " + buttonInterval + " sec");
                    buttonSampleIntervalEt.setText("");
                    sendJsonToDweet();//Update the Dweet.io API with the specified values
                    //}

                }
                else{
                    publishButton = "false";//Turn Sensor Off
                    buttonStatusTv.setText("Button Actuator is OFF");
                    buttonSampleIntervalEt.setText("");
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
            dweetJsonObj.put("publishDistance",publishDistance);
            dweetJsonObj.put("publishButton",publishButton);
            dweetJsonObj.put("publishLed",publishLed);

            dweetJsonObj.put("tempTime",tempSampleInterval);
            dweetJsonObj.put("humidTime",humidityInterval);
            dweetJsonObj.put("distanceTime",distanceInterval);
            dweetJsonObj.put("buttonTime",buttonInterval);

            //dweetJsonObj.put("publishLed",publishLed);
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
        System.out.println("ERROR CATCH ----------");

    }

    @Override
    public void onResponse(Object response) {
        Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();
        resultTv.setText(response.toString());
    }

    //Adding Share Button to TitleBar From : http://android.xsoftlab.net/training/basics/actionbar/adding-buttons.html
    //And also From: https://developer.android.com/training/appbar/actions.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareRecipe:
                webView();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //WebView From: https://www.tutorialspoint.com/android/android_webview_layout.htm
    //And From: https://developer.android.com/reference/android/webkit/WebView.html
    //Hiding Views Programmatically From: https://stackoverflow.com/questions/5756136/how-to-hide-a-view-programmatically
    //Fading Views From: https://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
    public void webView(){
        if(webViewVisible == false){
            //webView.animate().alpha(1.0f);
            webView.setVisibility(View.VISIBLE);
            webViewVisible = true;
        }

        else{
            webView.setVisibility(View.INVISIBLE);
            webView.animate().alpha(0.0f);
            webViewVisible = false;
        }

}

}
