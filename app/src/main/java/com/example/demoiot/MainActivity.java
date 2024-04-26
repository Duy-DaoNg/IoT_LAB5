package com.example.demoiot;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    SwitchCompat swFan, swLight, swTV;
    TextView txtTemp, txtHumid, txtLux;
    boolean enableSendSwFanData = true;
    boolean enableSendSwTVData = true;
    boolean enableSendSwLightData = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        swFan = findViewById(R.id.swFan);
        swLight = findViewById(R.id.swLight);
        swTV = findViewById(R.id.swTV);
        txtTemp = findViewById(R.id.currentTemperature);
        txtHumid = findViewById(R.id.currentHumid);
        txtLux = findViewById(R.id.currentLumi);
        startMQTT();
        controlDevices();
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST",topic + " message: " + message.toString());
                if (topic.contains("s-temperature")) {
                    txtTemp.setText(message.toString());
                } else if (topic.contains("s-humid")) {
                    txtHumid.setText(message.toString());
                } else if (topic.contains("s-lumi")) {
                    txtLux.setText(message.toString());
                }else if (topic.contains("btn-light")) {
                    boolean result = message.toString().equals("1");
                    if(swLight.isChecked() ^ result) {
                        enableSendSwLightData = false;
                        swLight.setChecked(result);
                        enableSendSwLightData = true;
                    }
                } else if (topic.contains("btn-fan")) {
                    boolean result = message.toString().equals("1");
                    if(swFan.isChecked() ^ result) {
                        enableSendSwFanData = false;
                        swFan.setChecked(result);
                        enableSendSwFanData = true;
                    }
                } else if (topic.contains("btn-tv")) {
                    boolean result = message.toString().equals("1");
                    if(swTV.isChecked() ^ result) {
                        enableSendSwTVData = false;
                        swTV.setChecked(result);
                        enableSendSwTVData = true;
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    public void controlDevices(){
        swLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (enableSendSwLightData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-light", "1");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (enableSendSwLightData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-light", "0");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        swFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (enableSendSwFanData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-fan", "1");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (enableSendSwFanData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-fan", "0");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        swTV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (enableSendSwTVData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-tv", "1");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (enableSendSwTVData) {
                        try {
                            sendDataMQTT("duydao0604/feeds/btn-tv", "0");
                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

    }
    public void sendDataMQTT(String topic, String value) throws MqttException {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        mqttHelper.mqttAndroidClient.publish(topic, msg);

    }
}