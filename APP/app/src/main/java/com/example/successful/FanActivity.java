package com.example.successful;

import android.annotation.SuppressLint;
import android.content.Intent;
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


public class FanActivity extends AppCompatActivity{
    private String host = "tcp://lbsmqtt.airm2m.com:1884";
    private String mqtt_id = "FanActivity";
    private String userName = "FanActivity";
    private String passWord = "FanActivity";
    private Switch fan_one;
    private Switch fan_two;
    private Switch fan_three;
    private MqttClient client;
    private Handler handler;
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private String mqtt_pub_topic = "YPJSTF_control";
    private String Status;
    private static String fans_1_status="0";
    private static String fans_2_status="0";
    private static String fans_3_status="0";
    private static String fans_status = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Intent intent = getIntent();
        Status = intent.getStringExtra("Status");
        Toast.makeText(FanActivity.this, Status, Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);
        fan_one =   findViewById(R.id.fan_one);
        fan_two =   findViewById(R.id.fan_two);
        fan_three = findViewById(R.id.fan_three);
        Mqtt_init();
        Mqtt_connect();
        publishMessage(mqtt_pub_topic,Status);
        if(Status!=null){
            char []arrays=Status.toCharArray();
            fan_one.setChecked(arrays[0]=='1');
            fan_two.setChecked(arrays[1]=='1');
            fan_three.setChecked(arrays[2]=='1');
        }else{
            fan_one.setChecked(false);
            fan_two.setChecked(false);
            fan_three.setChecked(false);
        }
        fan_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fan_one.isChecked()) {
                    try {
                        fans_1_status="1";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        fans_1_status="0";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        fan_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果是true
                if (fan_two.isChecked()) {
                    try {
                        fans_2_status="1";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        fans_2_status="0";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        fan_three.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fan_three.isChecked()) {
                    try {
                        fans_3_status="1";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        fans_3_status="0";
                        fans_status=fans_1_status+fans_2_status+fans_3_status+Status;
                        publishMessage(mqtt_pub_topic, fans_status);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 1: //开机校验更新回传
                    break;
                case 2:  // 反馈回传

                    break;
                case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                    //Toast.makeText(FunctionActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 30:  //连接失败
                    Toast.makeText(FanActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 31:   //连接成功
                    Toast.makeText(FanActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                    try {
//                        client.subscribe(mqtt_pub_topic, "");//订阅主题
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
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //收到消息标志位
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);    // hander 回传
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
                    if (!(client.isConnected()))  //如果还未连接
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
    private String Analysis(String heats_status,String light_status,String fans){
        String result=null;
        Intent intent = getIntent();
        String heats=intent.getStringExtra(heats_status);
        String lights=intent.getStringExtra(light_status);
        result = fans+heats+lights;
        return result;
    }

    //返回键以键值对的方式传递参数给FunctionActivity
    @Override
    public void onBackPressed() {
        fans_status=fans_1_status+fans_2_status+fans_3_status;
        Toast.makeText(FanActivity.this, fans_status, Toast.LENGTH_SHORT).show();
        Intent backIntent = new Intent();
        backIntent.putExtra("fans_status",fans_status);
        setResult(RESULT_OK,backIntent);
        FanActivity.this.finish();
    }
}

