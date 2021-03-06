package com.example.successful;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Heat extends AppCompatActivity{
    private String host = "tcp://lbsmqtt.airm2m.com:1884";
    private Switch heat1;
    private Switch heat2;
    private Switch heat3;
    private String mqtt_id = "HeatActivity";
    private String userName = "HeatActivity";
    private String passWord = "HeatActivity";
    private MqttClient client;
    private Handler handler;
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private String mqtt_pub_topic ="YPJSTF_control";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);
        heat1=findViewById(R.id.heat_one);
        heat2=findViewById(R.id.heat_two);
        heat3=findViewById(R.id.heat_three);
        Mqtt_init();
        Mqtt_connect();

        heat1.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (heat1.isChecked()){
                            try {
                                publishMessage(mqtt_pub_topic,"heat_1:1");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                publishMessage(mqtt_pub_topic,"heat_1:0");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        heat2.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (heat2.isChecked()){
                            try {
                                publishMessage(mqtt_pub_topic,"heat_2:1");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                publishMessage(mqtt_pub_topic,"heat_2:0");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        heat3.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (heat3.isChecked()){
                            try {
                                publishMessage(mqtt_pub_topic,"heat_3:1");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                publishMessage(mqtt_pub_topic,"heat_3:0");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                switch (msg.what) {
                    case 1: //????????????????????????
                        break;
                    case 2:  // ????????????

                        break;
                    case 3:  //MQTT ??????????????????   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(FunctionActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 30:  //????????????
                        Toast.makeText(Heat.this, "????????????", Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //????????????
                        Toast.makeText(Heat.this, "????????????", Toast.LENGTH_SHORT).show();
//                    try {
//                        client.subscribe(mqtt_pub_topic, "");//????????????
//
//                    } catch (MqttException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                    default:
                        break;
                }
            }
        };
    }

    private void publishMessage(String topic, String message2) {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
        }
        MqttException e = null;
        e.printStackTrace();
    }


    private void Mqtt_init() {
        try {
            //host???????????????test???clientid?????????MQTT????????????ID?????????????????????????????????????????????MemoryPersistence??????clientid??????????????????????????????????????????
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT???????????????
            options = new MqttConnectOptions();
            //??????????????????session,?????????????????????false??????????????????????????????????????????????????????????????????true??????????????????????????????????????????????????????
            options.setCleanSession(false);
            //????????????????????????
            options.setUserName(userName);
            //?????????????????????
            options.setPassword(passWord.toCharArray());
            // ?????????????????? ????????????
            options.setConnectionTimeout(10);
            // ???????????????????????? ???????????? ??????????????????1.5*20????????????????????????????????????????????????????????????????????????????????????????????????????????????
            options.setKeepAliveInterval(20);
            //????????????
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //????????????????????????????????????????????????
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish?????????????????????
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe???????????????????????????????????????
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //?????????????????????
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);    // hander ??????
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!(client.isConnected()))  //??????????????????
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
}
