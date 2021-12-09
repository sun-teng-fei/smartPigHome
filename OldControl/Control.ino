/**
 * 作者：孙腾飞
 * 日期：2021.11.3 
 * 功能：接收服务器发来的信息，完成对继电器的控制
 * 2021.11.6：待完善，将控制信息写入EEPROM，这样在断电重启之后依然能够恢复断电之前的状态
 * 2021.11.8：待完善，控制步进电机
*/
#include <PubSubClient.h>
#include <ESP8266WiFi.h>
#include <WiFiManager.h>
#include <EEPROM.h>

/*设置引脚信息(这里定义的是GPIO，而不是nodeMCU上面的Dx)*/
/*USB左边*/
#define pinFanRelay1 16
#define pinFanRelay2 5
#define pinFanRelay3 4
#define pin
/*设置WiFi配网信息*/
#define WiFiName "setWifiForESPControl"
#define WiFiPassword "123456789"

/* 设置mqtt信息*/
String needSubTopicString = "YPJSTF_control";
const char *mqttServer = "lbsmqtt.airm2m.com";
const int mqttPort = 1884;

/*重要变量定义*/
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
WiFiManager wifiManager;

/*重要函数定义*/
void allClose();
void initEEPROM();
void readPinFromEEPROM();
void writePinToEEPROM();
void connectMQTTserver();
void receiveCallback(char *topic, byte *payload, unsigned int length);

void setup() {
    /*设置波特率*/
    Serial.begin(9600);

    /*设置输入输出模式以及调用allClose()函数,读取写入EEPROM中的数据*/
    pinMode(LED_BUILTIN, OUTPUT);
    pinMode(pinFanRelay1, OUTPUT);
    pinMode(pinFanRelay2, OUTPUT);
    pinMode(pinFanRelay3, OUTPUT);
    allClose();

    /*wifiAP模式实现手机配网*/
    WiFiManager wifiByPhone;
    wifiByPhone.autoConnect(WiFiName, WiFiPassword);

    /*如果WiFi已经连接好,设置mqtt服务器和回调函数*/
    if (WiFi.status() == WL_CONNECTED) {
        WiFi.mode(WIFI_STA);
        mqttClient.setServer(mqttServer, mqttPort);
        mqttClient.setCallback(receiveCallback);
    }
    connectMQTTserver();
}

void loop() {
    /* 如已连服务器,保持心跳*/
    if (mqttClient.connected()) {
        mqttClient.loop();
    }
    /* 如未连服务器,重连服务器*/
    else {
        connectMQTTserver();
    }
}

/*本函数使得所有拓展全部属于关闭状态*/
void allClose(){
    digitalWrite(LED_BUILTIN, LOW);
    digitalWrite(pinFanRelay1, HIGH);
    digitalWrite(pinFanRelay2, HIGH);
    digitalWrite(pinFanRelay3, HIGH);
}

// 连接MQTT服务器并订阅信息
void connectMQTTserver() {
    // 根据ESP8266的MAC地址生成客户端ID（避免与其它ESP8266的客户端ID重名）
    String clientId = "Esp8266Control" + WiFi.macAddress();

    // 连接MQTT服务器
    if (mqttClient.connect(clientId.c_str())) {
        Serial.println("MQTT Server Connected.");
        Serial.println("Server Address:");
        Serial.println(mqttServer);
        Serial.println("ClientId: ");
        Serial.println(clientId);
        subscribeTopic(); // 订阅指定主题
    } else {
        Serial.print("MQTT Server Connect Failed. Client State:");
        Serial.println(mqttClient.state());
        delay(5000);
    }
}

// 收到信息后的回调函数
void receiveCallback(char *topic, byte *payload, unsigned int length) {
    Serial.print("Message Received [");
    Serial.print(topic);
    Serial.print("] ");
    for (int i = 0; i < length; i++) {
        Serial.print((char)payload[i]);
    }
    Serial.println("");
    Serial.print("Message Length(Bytes) ");
    Serial.println(length);

    /************开始对收到的信息进行解析*******/
    /**解析第1个风扇的控制信息**/
    if ((char)payload[0] == '1') {
        digitalWrite(pinFanRelay1, LOW);
    } 
    else if((char)payload[0] == '0'){
        digitalWrite(pinFanRelay1, HIGH);
    }
    else{
        Serial.println("Error");
    }

    /**解析第2个风扇的控制信息**/
    if ((char)payload[1] == '1') {
        digitalWrite(pinFanRelay2, LOW);
    } 
    else if((char)payload[1] == '0'){
        digitalWrite(pinFanRelay2, HIGH);
    }
    else{
        Serial.println("Error");
    }

    /**解析第3个风扇的控制信息**/
    if ((char)payload[2] == '1') {
        digitalWrite(pinFanRelay3, LOW);
    } 
    else if((char)payload[2] == '0'){
        digitalWrite(pinFanRelay3, HIGH);
    }
    else{
        Serial.println("Error");
    }

}

// 订阅指定主题
void subscribeTopic() {
    char subTopic[needSubTopicString.length() + 1];
    strcpy(subTopic, needSubTopicString.c_str());

    // 通过串口监视器输出是否成功订阅主题1以及订阅的主题1名称
    if (mqttClient.subscribe(subTopic)) {
        Serial.println("Subscrib Topic:");
        Serial.println(subTopic);
    } else {
        Serial.print("Subscribe Fail");
    }
}