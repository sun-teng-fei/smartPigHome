package com.example.successful;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

public class SunShade extends AppCompatActivity implements View.OnClickListener {
    private Message msg;
    private MqttClient client;
    private String mqtt_sub_topic = "YPJSTF_infor" ; //订阅主题
    private String mqtt_pub_topic = "YPJSTF_control";//发布主题
    private Handler handler;
    private MqttConnectOptions options;
    //private String mqtt_sub_topic = "温度";
    private String host = "tcp://lbsmqtt.airm2m.com:1884";
    private ScheduledExecutorService scheduler;
    private String mqtt_id = "Sunshade";
    private String userName = "Sunshade";
    private String passWord = "Sunshade";
    private  int change_number=0;
    private Button button_0;
    private Button button_25;
    private Button button_50;
    private Button button_75;
    private Button button_100;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_shade);
        Mqtt_init();
        Mqtt_connect();
        button_0=findViewById(R.id.open_0);
        button_25=findViewById(R.id.open_25);
        button_50=findViewById(R.id.open_50);
        button_75=findViewById(R.id.open_75);
        button_100=findViewById(R.id.open_100);
        button_0.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            publishMessage(mqtt_pub_topic,"Shade:0");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        button_25.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            publishMessage(mqtt_pub_topic,"Shade:25");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        button_50.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            publishMessage(mqtt_pub_topic,"Shade:50");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        button_75.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            publishMessage(mqtt_pub_topic,"Shade:75");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        button_100.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            publishMessage(mqtt_pub_topic,"Shade:100");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );

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
                    Toast.makeText(SunShade.this, "连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 31:   //连接成功
                    Toast.makeText(SunShade.this, "连接成功", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        publishMessage(mqtt_pub_topic,"Sun:"+change_number);
    }
}
