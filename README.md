![screenshot_2015-04-05-23-06-30](https://cloud.githubusercontent.com/assets/12403337/8718356/70377568-2bd6-11e5-8a0a-ae2b8e8e6e96.jpg)

這個APP是要模仿一般的遙控器，左邊是油門和Yaw，右邊是 Pitch and Roll，手指頭碰到搖桿時顏色會變深，手指在螢幕上滑動進行操作。  
在藍牙完成配對之後，以文字的方式傳送共8組資料，每組3個字元，加上最後3個字元是checksum，共27個都是數字。結尾加上一個分號，各代表：  
1~3     throttle  
4~6     yaw  
7~9     pitch  
10~12 roll  
13~15 aux1  
16~18 aux2  
19~21 aux3  
22~24 aux4  
25~27 checksum 把前面24的數字相加。  
cd   
會傳出的資料範圍都是0~100。  
  
  
如果用Arduino來接收，可以參考以下的程式：  
程式以可讀性優先，沒有特別去追求效能，還算堪用。  
 
```c 
#include <Servo.h>  
  
int throttlePin = 5;  
int rollPin = 6;  
int pitchPin = 7;  
int yawPin = 8;  
int modePin = 9;  
  
Servo servoMode, servoThro, servoYaw, servoRoll, servoPitch;  
void setup()  
{  
  Serial.begin(57600);  
  pinMode(modePin, OUTPUT);  
  pinMode(throttlePin, OUTPUT);  
  pinMode(rollPin, OUTPUT);  
  pinMode(pitchPin, OUTPUT);  
  pinMode(yawPin, OUTPUT);  
  
  servoMode.attach(modePin);  
  servoThro.attach(throttlePin);  
  servoYaw.attach(yawPin);  
  servoRoll.attach(rollPin);  
  servoPitch.attach(pitchPin);  
}  
int channelValue[9];  
String message;  
void loop()  
{  
  while (Serial.available())  
  {  
    char c = Serial.read();  
    if (c != ';') {  
      message += c;  
    } else {  
      if (message != "")  
      {  
        boolean checkSumIsValid = parseChannelValues(message);  
        if (checkSumIsValid) {  
          writeToServo();  
        }  
        message = "";  
      }  
    }  
  }  
}  
  
void writeToServo() {  
  servoMode.writeMicroseconds(channelValue[4]*10+1000);  
  servoThro.writeMicroseconds(channelValue[0]*10+1000);  
  servoYaw.writeMicroseconds(channelValue[1]*10+1000);  
  servoRoll.writeMicroseconds(channelValue[3]*10+1000);  
  servoPitch.writeMicroseconds(channelValue[2]*10+1000);  
}  
  
boolean parseChannelValues(String message) {  
  int channelCounter = 0;  
  int checkSum = 0;  
  for (int i = 0; i < 27; i += 3) {  
    int value = 0;  
    value += (message[i] - 48) * 100;  
    value += (message[i + 1] - 48) * 10;  
    value += (message[i + 2] - 48);  
    channelValue[channelCounter++] = value;  
  }  
  int checkSumValue  = 0;  
  for (int i = 0; i < 24; i++) {  
    checkSumValue += (message[i] - 48);  
  }  
  
  int checkSumField = channelValue[8];  
  if (checkSumValue !=  checkSumField) {  
    return false;  
  }  
  return true;  
}  
```
