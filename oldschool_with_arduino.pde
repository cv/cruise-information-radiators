import ddf.minim.*;
import processing.core.*;
import fullscreen.*;
import processing.xml.*;
import processing.serial.*;
import cc.arduino.*;

String PROJECT_NAME = "1c2f";
String URL = "http://localhost:3333/projects/" + PROJECT_NAME + ".rss";

String BIG_FONT_NAME = "SynchroLET-128.vlw";
String SMALL_FONT_NAME = "SynchroLET-32.vlw";

// Arduino pins
int GREEN = 9;
int RED = 11;

int ARDUINO_PORT = 0;

Arduino arduino;
PFont bigFont, smallFont;
AudioPlayer success, failure;
boolean successfulBuildLastTime = false;

void setup() {
  setupArduino();
  setupScreen();
  setupSound();
}

void draw() {
  String[] elements = parseRss();
  String title = elements[0];
  String description = elements[1];
  
  paintBuildColour(title);
  drawTitle(title);
  drawDescription(description);
  delay(2000);
}

void stop() {
  success.close();
  failure.close();
  super.stop();
}

void setupArduino() {
  arduino = new Arduino(this, Arduino.list()[ARDUINO_PORT], 57600);
}

void setupScreen() {
  size(1280, 1024);
  new FullScreen(this).enter();
  bigFont = loadFont(BIG_FONT_NAME);
  smallFont = loadFont(SMALL_FONT_NAME);
}

void setupSound() {
  Minim.start(this);
  success = Minim.loadFile("success.mp3", 512);
  failure = Minim.loadFile("failure.mp3", 512);  
}

String[] parseRss() {
  XMLElement rss = new XMLElement(this, URL);
  XMLElement title = rss.getChildren("channel/item/title")[0];
  XMLElement description = rss.getChildren("channel/item/description")[0];
  return new String[] { title.getContent(), description.getContent() };
}

// TODO clean this up, too messy
void paintBuildColour(String title) {
  if(title.indexOf("success") > 0) {
    success.play();
    successfulBuildLastTime = true;
    background(0, 0, 0);
    fill(0, 255, 0);
    arduino.analogWrite(GREEN, 255);
    arduino.analogWrite(RED, 0);
  }
  else {
    failure.play();
    successfulBuildLastTime = false;
    background(0,0,0);    
    fill(255, 0, 0);
    arduino.analogWrite(GREEN, 0);
    arduino.analogWrite(RED, 255);
  } 
}

void drawTitle(String title) {
  textFont(bigFont);
  text(title.replaceAll(PROJECT_NAME + " ", ""), 32, 128);  
}

void drawDescription(String description) {  
  textFont(smallFont);
  text(description.replaceAll("<\\/?pre>", ""), 32, 256);
}

