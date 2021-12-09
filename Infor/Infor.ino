/**
 * 作者：孙腾飞
 * 日期：2021.11.2
 * 用途：esp8向服务器端发送数据
 * 2021.11.8更改：添加向LCD1602显示,明天试一下;
 * 2021.11.16更改: 改用OLED显示,失败,改用Show端展示信息
* */
  
#include <LiquidCrystal_I2C.h> 
#include <PubSubClient.h>
#include <ESP8266WiFi.h>
#include <WiFiManager.h>
#include <Ticker.h> 
#include<dht11.h>
#include <Wire.h>
#include<math.h>

/*定义WiFi配网信息*/
#define WiFiName "setWifiForESP"
#define WiFiPassword "123456789"

/* 设置mqtt服务器信息*/
const char* mqttServer = "lbsmqtt.airm2m.com";
const int mqttPort = 1884;

/*重要变量定义*/ 
Ticker ticker;
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

int count;                  //Ticker计数变量

/*定义重要引脚*/
#define dhtPin    16        //dht11引脚
#define BH1750_SDA 14       //光照传感器数据引脚
#define BH1750_SCL 12       //光照传感器控制引脚
#define LCD1602_ADDR 0x27   //LCD1602地址
#define LCD1602_COL 16      //LCD1602列数
#define LCD1602_ROW 2       //LCD1602行数

/*光照传感器相关定义*/
int BH1750address = 0x23;//BH1750 I2C地址
byte buff[2];

/*LCD1602*/
LiquidCrystal_I2C lcd(LCD1602_ADDR,LCD1602_COL,LCD1602_ROW);  

/*读取信息暂存变量*/
dht11 *dht = new dht11();   //温湿度变量
double lightIntensity;      //光照强度

void connectMQTTserver();

void setup()
{
    /*设置波特率*/
    Serial.begin(9600);

    /*设置引脚模式*/
    Wire.begin(BH1750_SDA,BH1750_SCL);
    
	  //初始化LCD
	  lcd.init(); 
    lcd.backlight();

    /*wifiAP模式实现手机配网*/
    WiFiManager wifiByPhone;
    wifiByPhone.autoConnect(WiFiName, WiFiPassword);

    /*如果WiFi已经连接好,设置mqtt服务器和回调函数*/
    if(WiFi.status() == WL_CONNECTED){
        WiFi.mode(WIFI_STA);
        mqttClient.setServer(mqttServer,mqttPort);
    }
    connectMQTTserver();
    ticker.attach(1, tickerCount);  
}

void loop() {
     displayLCD();
    /* 如已连服务器,保持心跳并更新数据*/
    if (mqttClient.connected()) { 
        mqttClient.loop();
        dht->read(dhtPin);
        lightIntensity = BH1750();
    } 
    /* 如未连服务器,重连服务器*/
    else {                               
        connectMQTTserver();
    }
}

/*定时中断函数*/
void tickerCount(){
    count++;
    /*每隔5秒发布一次消息，更新lcd*/
    if (count >= 6)
    {
        pubMQTTmsg();
        displayLCD();
        count = 0;
    }
}

// 连接MQTT服务器
void connectMQTTserver(){
  String clientId = "esp8266-" + WiFi.macAddress();
  // 连接MQTT服务器
  if (mqttClient.connect(clientId.c_str())) { 
    Serial.println("MQTT Server Connected.");
    Serial.println("Server Address:");
    Serial.println(mqttServer);
    Serial.println("ClientId: ");
    Serial.println(clientId);
  } else {
    Serial.print("MQTT Server Connect Failed. Client State:");
    Serial.println(mqttClient.state());
    delay(5000);
  }   
}

// 发布信息
void pubMQTTmsg(){
  static int value; // 客户端发布信息用数字

  // 建立发布主题
  String topicString = "YPJSTF_infor";
  char publishTopic[topicString.length() + 1];  
  strcpy(publishTopic, topicString.c_str());

  String messageString = String(dht->temperature)+ " " + String(dht->humidity) + " " + String(lightIntensity); 
  char publishMsg[messageString.length() + 1];   
  strcpy(publishMsg, messageString.c_str());
  
  // 实现ESP8266向主题发布信息
  if(mqttClient.publish(publishTopic, publishMsg)){
    Serial.println("Publish Topic:");Serial.println(publishTopic);
    Serial.println("Publish message:");Serial.println(publishMsg);    
  } else {
    Serial.println("Message Publish Failed."); 
  }
}

double BH1750() //BH1750设备操作
{
  int i=0;
  double  val=0;
  //开始I2C读写操作
  Wire.beginTransmission(BH1750address);
  Wire.write(0x10);//1lx reolution 120ms//发送命令
  Wire.endTransmission();  
  
  delay(200);
  //读取数据
  Wire.beginTransmission(BH1750address);
  Wire.requestFrom(BH1750address, 2);
  while(Wire.available()) //
  {
    buff[i] = Wire.read();  // receive one byte
    i++;
  }
  Wire.endTransmission();
  if(2==i)
  {
   val=((buff[0]<<8)|buff[1])/1.2;
  }
  return val;
}

//LCD 显示信息
void displayLCD(){
     lcd.setCursor(0,0);
     lcd.print("TEM:" + String(dht->temperature) + " " + "HUM:" + String(dht->humidity));
     lcd.setCursor(0,1);
     lcd.print("light:" + String(lightIntensity));
}
