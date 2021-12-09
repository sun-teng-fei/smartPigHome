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
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传

                        break;
                    case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(FunctionActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 30:  //连接失败
                        Toast.makeText(Heat.this, "连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(Heat.this, "连接成功", Toast.LENGTH_SHORT).show();
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
}
