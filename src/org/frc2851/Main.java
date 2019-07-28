package org.frc2851;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main extends Application
{
    private Font textFont = Font.font("Roboto", FontWeight.BOLD, 15);
    private Font textFieldFont = new Font("Courier New", 15);
    private Font buttonFont = new Font("Roboto", 15);

    private TextField hostIPField = new TextField(),
            sendPortField = new TextField(),
            receivePortField = new TextField(),
            streamWidthField = new TextField(),
            streamHeightField = new TextField(),
            streamFPSField = new TextField(),
            lowHueField = new TextField(),
            lowSaturationField = new TextField(),
            lowValueField = new TextField(),
            highHueField = new TextField(),
            highSaturationField = new TextField(),
            highValueField = new TextField();

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(5);
        root.setPadding(new Insets(10, 10, 0, 10));

        Text hostIPText = new Text("Host IP");
        hostIPText.setFont(textFont);
        root.add(hostIPText, 1, 0);
        Text sendPortText = new Text("Send Port");
        sendPortText.setFont(textFont);
        root.add(sendPortText, 1, 3);
        Text receivePortText = new Text("Receive Port");
        receivePortText.setFont(textFont);
        root.add(receivePortText, 1, 6);
        Text streamWidthText = new Text("Stream Width");
        streamWidthText.setFont(textFont);
        root.add(streamWidthText, 12, 0);
        Text streamHeightText = new Text("Stream Height");
        streamHeightText.setFont(textFont);
        root.add(streamHeightText, 12, 3);
        Text streamFPSText = new Text("Stream FPS");
        streamFPSText.setFont(textFont);
        root.add(streamFPSText, 12, 6);
        Text lowHueText = new Text("Low Hue");
        lowHueText.setFont(textFont);
        root.add(lowHueText, 1, 8);
        Text highHueText = new Text("High Hue");
        highHueText.setFont(textFont);
        root.add(highHueText, 1, 11);
        Text lowSaturationText = new Text("Low Saturation");
        lowSaturationText.setFont(textFont);
        root.add(lowSaturationText, 1, 14);
        Text highSaturationText = new Text("High Saturation");
        highSaturationText.setFont(textFont);
        root.add(highSaturationText, 1, 17);
        Text lowValueText = new Text("Low Value");
        lowValueText.setFont(textFont);
        root.add(lowValueText, 1, 20);
        Text highValueText = new Text("High Value");
        highValueText.setFont(textFont);
        root.add(highValueText, 1, 23);

        hostIPField.setFont(textFieldFont);
        root.add(hostIPField, 1, 1);
        sendPortField.setFont(textFieldFont);
        root.add(sendPortField, 1, 4);
        receivePortField.setFont(textFieldFont);
        root.add(receivePortField, 1, 7);
        streamWidthField.setFont(textFieldFont);
        root.add(streamWidthField, 12, 1);
        streamHeightField.setFont(textFieldFont);
        root.add(streamHeightField, 12, 4);
        streamFPSField.setFont(textFieldFont);
        root.add(streamFPSField, 12, 7);
        lowHueField.setFont(textFieldFont);
        root.add(lowHueField, 1, 9);
        highHueField.setFont(textFieldFont);
        root.add(highHueField, 1, 12);
        lowSaturationField.setFont(textFieldFont);
        root.add(lowSaturationField, 1, 15);
        highSaturationField.setFont(textFieldFont);
        root.add(highSaturationField, 1, 18);
        lowValueField.setFont(textFieldFont);
        root.add(lowValueField, 1, 21);
        highValueField.setFont(textFieldFont);
        root.add(highValueField, 1, 24);

        Slider lowHueSlider = new Slider();
        lowHueSlider.setMin(0);
        lowHueSlider.setMax(255);
        lowHueSlider.setShowTickLabels(true);
        lowHueSlider.setMajorTickUnit(85);
        root.add(lowHueSlider, 5, 9, 8, 1);
        Slider highHueSlider = new Slider();
        highHueSlider.setMin(0);
        highHueSlider.setMax(255);
        highHueSlider.setShowTickLabels(true);
        highHueSlider.setMajorTickUnit(85);
        root.add(highHueSlider, 5, 12, 8, 1);
        Slider lowSaturationSlider = new Slider();
        lowSaturationSlider.setMin(0);
        lowSaturationSlider.setMax(255);
        lowSaturationSlider.setShowTickLabels(true);
        lowSaturationSlider.setMajorTickUnit(85);
        root.add(lowSaturationSlider, 5, 15, 8, 1);
        Slider highSaturationSlider = new Slider();
        highSaturationSlider.setMin(0);
        highSaturationSlider.setMax(255);
        highSaturationSlider.setShowTickLabels(true);
        highSaturationSlider.setMajorTickUnit(85);
        root.add(highSaturationSlider, 5, 18, 8, 1);
        Slider lowValueSlider = new Slider();
        lowValueSlider.setMin(0);
        lowValueSlider.setMax(180);
        lowValueSlider.setShowTickLabels(true);
        lowValueSlider.setMajorTickUnit(60);
        root.add(lowValueSlider, 5, 21, 8, 1);
        Slider highValueSlider = new Slider();
        highValueSlider.setMin(0);
        highValueSlider.setMax(180);
        highValueSlider.setShowTickLabels(true);
        highValueSlider.setMajorTickUnit(60);
        root.add(highValueSlider, 5, 24, 8, 1);

        Button toggleStream = new Button("                                Enable Streaming                                ");
        toggleStream.setFont(buttonFont);
        root.add(toggleStream, 0, 26, 15, 1);
        Button transmitData = new Button("                                   Transmit Data                                   ");
        transmitData.setFont(buttonFont);
        root.add(transmitData, 0, 27, 15, 1);

        Scanner in = new Scanner(new FileReader("profile.txt"));
        StringBuilder inBuilder = new StringBuilder();
        while (in.hasNext())
        {
            inBuilder.append(in.next());
        }
        in.close();
        String profile = inBuilder.toString();
        for (String settingPair : profile.split(";"))
        {
            if (settingPair.split(":").length == 1)
                continue;
            String name = settingPair.split(":")[0];
            String value = settingPair.split(":")[1];
            switch (name)
            {
                case "host-ip":
                    hostIPField.setText(value);
                    break;
                case "host-port":
                    sendPortField.setText(value);
                    break;
                case "receive-port":
                    receivePortField.setText(value);
                    break;
                case "stream-width":
                    streamWidthField.setText(value);
                    break;
                case "stream-height":
                    streamHeightField.setText(value);
                    break;
                case "stream-fps":
                    streamFPSField.setText(value);
                    break;
                case "low-hue":
                    lowHueField.setText(value);
                    lowHueSlider.setValue(Double.parseDouble(value));
                    break;
                case "low-saturation":
                    lowSaturationField.setText(value);
                    lowSaturationSlider.setValue(Double.parseDouble(value));
                    break;
                case "low-value":
                    lowValueField.setText(value);
                    lowValueSlider.setValue(Double.parseDouble(value));
                    break;
                case "high-hue":
                    highHueField.setText(value);
                    highHueSlider.setValue(Double.parseDouble(value));
                    break;
                case "high-saturation":
                    highSaturationField.setText(value);
                    highSaturationSlider.setValue(Double.parseDouble(value));
                    break;
                case "high-value":
                    highValueField.setText(value);
                    highValueSlider.setValue(Double.parseDouble(value));
                    break;
            }
        }

        lowHueField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!lowHueField.getText().isEmpty())
                lowHueSlider.setValue(Double.parseDouble(lowHueField.getText()));
        });
        lowSaturationField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!lowSaturationField.getText().isEmpty())
                lowSaturationSlider.setValue(Double.parseDouble(lowSaturationField.getText()));
        });
        lowValueField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!lowValueField.getText().isEmpty())
                lowValueSlider.setValue(Double.parseDouble(lowValueField.getText()));
        });

        highHueField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!highHueField.getText().isEmpty())
                lowHueSlider.setValue(Double.parseDouble(highHueField.getText()));
        });
        highSaturationField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!highSaturationField.getText().isEmpty())
                highSaturationSlider.setValue(Double.parseDouble(highSaturationField.getText()));
        });
        highValueField.setOnKeyTyped((KeyEvent e) ->
        {
            if (!highValueField.getText().isEmpty())
                highValueSlider.setValue(Double.parseDouble(highValueField.getText()));
        });

        lowHueSlider.setOnMouseReleased((MouseEvent e) ->
        {
            lowHueSlider.setValue((int) lowHueSlider.getValue());
            lowHueField.setText(String.valueOf(lowHueSlider.getValue()));
        });
        lowSaturationSlider.setOnMouseReleased((MouseEvent e) ->
        {
            lowSaturationSlider.setValue((int) lowSaturationSlider.getValue());
            lowSaturationField.setText(String.valueOf(lowSaturationSlider.getValue()));
        });
        lowValueSlider.setOnMouseReleased((MouseEvent e) ->
        {
            lowValueSlider.setValue((int) lowValueSlider.getValue());
            lowValueField.setText(String.valueOf(lowValueSlider.getValue()));
        });

        highHueSlider.setOnMouseReleased((MouseEvent e) ->
        {
            highHueSlider.setValue((int) highHueSlider.getValue());
            highHueField.setText(String.valueOf(highHueSlider.getValue()));
        });
        highSaturationSlider.setOnMouseReleased((MouseEvent e) ->
        {
            highSaturationSlider.setValue((int) highSaturationSlider.getValue());
            highSaturationField.setText(String.valueOf(highSaturationSlider.getValue()));
        });
        highValueSlider.setOnMouseReleased((MouseEvent e) ->
        {
            highValueSlider.setValue((int) highValueSlider.getValue());
            highValueField.setText(String.valueOf(highValueSlider.getValue()));
        });

        toggleStream.setOnAction((ActionEvent e) ->
        {

        });

        UDPHandler udpHandler = new UDPHandler(hostIPField.getText(), Integer.parseInt(sendPortField.getText()), Integer.parseInt(receivePortField.getText()));
        transmitData.setOnAction((ActionEvent e) ->
        {
            udpHandler.send(getProfile());
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            if (udpHandler.getMessage().equals("received"))
            {
                System.out.println("Target received message");
                udpHandler.clearMessage();
            }
            else
            {
                System.out.println("Timed out waiting for target to send confirmation of reception");
            }
        });

        stage.setOnCloseRequest(event ->
        {
            try
            {
                try
                {
                    PrintWriter writer = new PrintWriter("profile.txt", StandardCharsets.UTF_8);
                    writer.println(getProfile());
                    writer.close();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }

                stop();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        stage.setTitle("Vision Communicator");
        stage.setScene(new Scene(root, 485, 690));
        stage.show();
    }

    private String getProfile()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("host-ip:").append(hostIPField.getText())
                .append(";host-port:").append(sendPortField.getText())
                .append(";receive-port:").append(receivePortField.getText())
                .append(";stream-width:").append(streamWidthField.getText())
                .append(";stream-height:").append(streamHeightField.getText())
                .append(";stream-fps:").append(streamFPSField.getText())
                .append(";low-hue:").append(lowHueField.getText())
                .append(";low-saturation:").append(lowSaturationField.getText())
                .append(";low-value:").append(lowValueField.getText())
                .append(";high-hue:").append(highHueField.getText())
                .append(";high-saturation:").append(highSaturationField.getText())
                .append(";high-value:").append(highValueField.getText());
        return builder.toString();
    }
}

/*
TODO:
    -Enable/disable video streaming button(s?)
    -Send button
    -Load profile at beginning
    -Check out http://www.raspberry-pi-geek.com/Archive/2016/15/Using-the-RPi-Cam-Web-Interface
    -Handle bad user input
    -Figure out a way to monitor for these changes on the Pi without using too many resources
 */
