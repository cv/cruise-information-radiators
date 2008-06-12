import processing.core.*; import ddf.minim.*; import fullscreen.*; import cc.arduino.*; import processing.core.*; import processing.xml.*; import processing.serial.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; import javax.sound.midi.*; import javax.sound.midi.spi.*; import javax.sound.sampled.*; import javax.sound.sampled.spi.*; import java.util.regex.*; import javax.xml.parsers.*; import javax.xml.transform.*; import javax.xml.transform.dom.*; import javax.xml.transform.sax.*; import javax.xml.transform.stream.*; import org.xml.sax.*; import org.xml.sax.ext.*; import org.xml.sax.helpers.*; public class oldschool_with_arduino extends PApplet {  // http://code.compartmental.net/tools/minim/
 // http://www.superduper.org/processing/fullscreen_api/
 // http://www.arduino.cc/playground/Interfacing/Processing (you need an Arduino with Firmata running on it)





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

public void setup() {
  setupArduino();
  setupScreen();
  setupSound();
}

public void draw() {
  String[] elements = parseRss();
  String title = elements[0];
  String description = elements[1];
  
  paintBuildColour(title);
  drawTitle(title);
  drawDescription(description);
  delay(2000);
}

public void stop() {
  success.close();
  failure.close();
  super.stop();
}

public void setupArduino() {
  arduino = new Arduino(this, Arduino.list()[ARDUINO_PORT], 57600);
}

public void setupScreen() {
  size(1280, 1024);
  new FullScreen(this).enter();
  bigFont = loadFont(BIG_FONT_NAME);
  smallFont = loadFont(SMALL_FONT_NAME);
}

public void setupSound() {
  Minim.start(this);
  success = Minim.loadFile("success.mp3", 512);
  failure = Minim.loadFile("failure.mp3", 512);  
}

public String[] parseRss() {
  XMLElement rss = new XMLElement(this, URL);
  XMLElement title = rss.getChildren("channel/item/title")[0];
  XMLElement description = rss.getChildren("channel/item/description")[0];
  return new String[] { title.getContent(), description.getContent() };
}

// TODO clean this up, too messy
public void paintBuildColour(String title) {
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

public void drawTitle(String title) {
  textFont(bigFont);
  text(title.replaceAll(PROJECT_NAME + " ", ""), 32, 128);  
}

public void drawDescription(String description) {  
  textFont(smallFont);
  text(description.replaceAll("<\\/?pre>", ""), 32, 256);
}


  static public void main(String args[]) {     PApplet.main(new String[] { "oldschool_with_arduino" });  }}