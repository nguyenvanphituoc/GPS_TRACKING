/**
   ESP8266
*/
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Ticker.h>
#include <TimeLib.h>
#include <time.h>

#define PIN_LED 16
#define PIN_BUTTON 0

#define LED_ON() digitalWrite(PIN_LED, HIGH)
#define LED_OFF() digitalWrite(PIN_LED, LOW)
#define LED_TOGGLE() digitalWrite(PIN_LED, digitalRead(PIN_LED) ^ 0x01)

Ticker ticker;

/*
   GPS
*/
#include <SoftwareSerial.h>
#include <TinyGPS.h>
TinyGPS gps;
SoftwareSerial ss(12, 13);
int timeoffset = 7;

static long print_date(TinyGPS &gps);
/*
   Firebase
*/
#include <FirebaseArduino.h>
#define FIREBASE_HOST "https://gpsarduino-14110434.firebaseio.com/" //Thay bằng địa chỉ firebase của bạn
#define FIREBASE_AUTH "puQ2rE3ALeRdWEPfQJ7We3Scy90nG4cJYgUrj2TC" //Không dùng xác thực nên không đổi

struct unix {
  long get(int y, int m = 0, int d = 0, int h = 0, int i = 0, int s = 0) {
    setTime(h, i, s, d, m, y);
    adjustTime(-25200); // +3
    return now();
  }
} unix;
float flat2, flon2;
String uid;
String deid = "gps_1";

bool longPress()
{
  static int lastPress = 0;
  if (millis() - lastPress > 3000 && digitalRead(PIN_BUTTON) == 0) {
    return true;
  } else if (digitalRead(PIN_BUTTON) == 1) {
    lastPress = millis();
  }
  return false;
}

void tick()
{
  //toggle state
  int state = digitalRead(PIN_LED);  // get the current state of GPIO1 pin
  digitalWrite(PIN_LED, !state);     // set pin to the opposite state
}

bool in_smartconfig = false;
void enter_smartconfig()
{
  //Mode wifi là station
  WiFi.mode(WIFI_STA);
  if (in_smartconfig == false) {
    in_smartconfig = true;
    ticker.attach(0.1, tick);
    WiFi.beginSmartConfig();
  }
}

void exit_smart()
{
  ticker.detach();
  LED_ON();
  in_smartconfig = false;
}
void event(const char * payload, size_t length) {
  Serial.printf("got message: %s\n", payload);
}
void setup() {
  Serial.begin(115200);
  ss.begin(9600);
  Serial.setDebugOutput(true);
  //Mode wifi là station
  WiFi.mode(WIFI_STA);
  pinMode(PIN_LED, OUTPUT);
  pinMode(PIN_BUTTON, INPUT);
  ticker.attach(1, tick);
  Serial.println("Setup done");

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);


}

void loop() {
  bool newData = false;
  unsigned long chars;
  unsigned short sentences, failed;

  if (longPress()) {
    enter_smartconfig();
    Serial.println("Enter smartconfig");
  }
  if (WiFi.status() == WL_CONNECTED && in_smartconfig && WiFi.smartConfigDone()) {
    exit_smart();
    Serial.println("Connected, Exit smartconfig");
  }

  if (WiFi.status() == WL_CONNECTED) {
    String path = "Devices/" + deid;
    FirebaseObject object = Firebase.get(path);
    Serial.println("qqqqqqqqqqqqqq");
    Serial.println(object.getString("uid"));
    uid = object.getString("uid");
    
    for (unsigned long start = millis(); millis() - start < 3000;)
    {
      while (ss.available())
      {
        char c = ss.read();
        //Serial.write(c); // uncomment this line if you want to see the GPS data flowing
        if (gps.encode(c)) // Did a new valid sentence come in?
          newData = true;
      }
    }
    if (newData)
    {
      float flat, flon;
      unsigned long age;
      gps.f_get_position(&flat, &flon, &age);

      if ((flat <= flat2 - 0.000020 || flat >= flat2 + 0.000020) || (flon <= flon2 - 0.000020 || flon >= flon2 + 0.000020)) {

        StaticJsonBuffer<200> jsonBuffer;

        JsonObject& root2 = jsonBuffer.createObject();
        JsonArray& data = root2.createNestedArray("data");
        data.add(flat, 15);
        data.add(flon, 15);

        JsonObject& root = jsonBuffer.createObject();
        root["time"] = print_date(gps);
        root["flat"] = root2["data"][0];
        root["flon"] = root2["data"][1];

        root.printTo(Serial);
        Serial.println();

        root.prettyPrintTo(Serial);
        String path = "Users/" + uid + "/" + deid;
        Firebase.push(path, root);
        // Kiểm tra lỗi
        if (Firebase.failed()) {
          Serial.print("setting /flater failed:");
          Serial.println(Firebase.error());
          return;
        }

      }
      flat2 = flat;
      flon2 = flon;
    }

  }

}
const int TimeZone = 7;
int DSTbegin[] = { //DST 2013 - 2025 in Canada and US
  310, 309, 308, 313, 312, 311, 310, 308, 314, 313, 312, 310, 309
};
int DSTend[] = { //DST 2013 - 2025 in Canada and US
  1103, 1102, 1101, 1106, 1105, 1104, 1103, 1101, 1107, 1106, 1105, 1103, 1102
};
int DaysAMonth[] = { //number of days a month
  31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
};

static long print_date(TinyGPS &gps)
{
  int year;
  byte month, day, hour, minute, second, hundredths;
  unsigned long date, time;
  unsigned long age;
  gps.crack_datetime(&year, &month, &day, &hour, &minute, &second, &hundredths, &age);
  if (year % 4 == 0) DaysAMonth[1] = 29; //leap year check

  //Time zone adjustment
  hour += TimeZone;
  //DST adjustment
  if (month * 100 + day >= DSTbegin[year - 13] &&
      month * 100 + day < DSTend[year - 13]) hour += 1;
  if (hour < 0)
  {
    hour += 24;
    day -= 1;
    if (day < 1)
    {
      if (month == 1)
      {
        month = 12;
        year -= 1;
      }
      else
      {
        month -= 1;
      }
      day = DaysAMonth[month - 1];
    }
  }
  if (hour >= 24)
  {
    hour -= 24;
    day += 1;
    if (day > DaysAMonth[month - 1])
    {
      day = 1;
      month += 1;
      if (month > 12) year += 1;
    }
  }

  if (age == TinyGPS::GPS_INVALID_AGE)
    return 0;
  else
  {
    char sz[32];
    sprintf(sz, "%02d/%02d/%02d %02d:%02d:%02d",
            month, day, year, hour, minute, second);
    return unix.get(year, month, day, hour, minute, second);
  }
}

