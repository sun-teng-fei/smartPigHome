package com.example.successful;
/**
 * 风扇+灯+加热板
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import java.util.concurrent.ScheduledExecutorService;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private Button bLg;//定义一个按钮
    private TextView tv;//定义一个编辑文本框
    private EditText et1, et2;//定义一个编辑文本框
    private Intent intent;//定义一个跳转
    //借鉴
    private MqttConnectOptions options;
    private String mqtt_id = "211765908";//定义自己的QQ号，唯一ID
    private Handler handler;
    //private String mqtt_sub_topic = "温度";
    private String host = "test.ranye-iot.net";
    private ScheduledExecutorService scheduler;
    private String userName = "A";
    private String passWord = "A";

    //己做
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找到控件
        bLg = findViewById(R.id.btn_login);//获取登录按钮Id
        et1 = findViewById(R.id.User_1);//获取到用户Id的内容
        et2 = findViewById(R.id.password_1);//获取到密码Id的内容
        //bLg.setOnClickListener((this));//开启监听
        String Username = et1.getText().toString();//获取用户名字
        String password = et2.getText().toString();//获取用户密码

       /* bLg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "正在登录中", Toast.LENGTH_SHORT).show();
                Intent showChoose = new Intent(MainActivity.this, FunctionActivity.class);
                startActivity(showChoose);
            }
        });*/
        bLg.setOnClickListener(this);


    }

    public void onClick(View v) {
        String Username = et1.getText().toString();//获取用户名字
        String password = et2.getText().toString();//获取用户密码
        intent = null;
        String ok = "登陆成功！";//设置成功弹窗文字内容
        String fail = "登陆失败，请重新登录！";//设置失败弹窗文字内容
        if (Username.equals("abc") && password.equals("123456")) {
            intent = new Intent(MainActivity.this, FunctionActivity.class);
            //从当前页面跳转到功能页面
            startActivity(intent);//开启跳转
            ////普通版的弹窗
            Toast.makeText(MainActivity.this, ok, Toast.LENGTH_SHORT).show();

        } else {
            //升级版Toast弹窗
            Toast toastcenter = Toast.makeText(MainActivity.this, fail, Toast.LENGTH_SHORT);
            //Toast.LENGTH_SHORT是短文字输出
            toastcenter.setGravity(Gravity.CENTER, 0, 0);//设置居中显示的位置
            toastcenter.show();//展示弹窗
        }
    }

}