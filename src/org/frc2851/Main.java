package org.frc2851;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main extends Application
{
    private Font textFont = Font.font("Roboto", FontWeight.BOLD, 15);
    private Font textFieldFont = new Font("Courier New", 15);
    private Font buttonFont = new Font("Roboto", 15);

    private TextField hostIPField = new TextField(),
            sendPortField = new TextField(),
            receivePortField = new TextField(),
            lowHueField = new TextField(),
            lowSaturationField = new TextField(),
            lowValueField = new TextField(),
            highHueField = new TextField(),
            highSaturationField = new TextField(),
            highValueField = new TextField(),
            valueField = new TextField();

    private Button transmitData = new Button("                                   Transmit Data                                   "),
            toggleStream = new Button("                                   Toggle Stream                                  "),
            updateValues = new Button("                                   Update Values                                   "),
            rebootButton = new Button("                                         Reboot                                         ");


    private HashMap<String, HashMap<String, String>> categories = new HashMap<>();

    private ComboBox<String> categoriesList = new ComboBox<>(),
            settingsList = new ComboBox<>();

    private UDPHandler udpHandler;
    private int timeout = 1000;

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
        Text categoryText = new Text("Category");
        categoryText.setFont(textFont);
        root.add(categoryText, 9, 0);
        Text settingText = new Text("Setting");
        settingText.setFont(textFont);
        root.add(settingText, 9, 3);
        Text valueText = new Text("Value");
        valueText.setFont(textFont);
        root.add(valueText, 9, 6);

        hostIPField.setFont(textFieldFont);
        root.add(hostIPField, 1, 1);
        sendPortField.setFont(textFieldFont);
        root.add(sendPortField, 1, 4);
        receivePortField.setFont(textFieldFont);
        root.add(receivePortField, 1, 7);
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
        valueField.setFont(textFieldFont);
        root.add(valueField, 9, 7);

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
                case "send-port":
                    sendPortField.setText(value);
                    break;
                case "receive-port":
                    receivePortField.setText(value);
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

        root.add(categoriesList, 9, 1);
        root.add(settingsList, 9, 4);

        udpHandler = new UDPHandler(hostIPField.getText(), Integer.parseInt(sendPortField.getText()), Integer.parseInt(receivePortField.getText()));

        transmitData.setFont(buttonFont);
        root.add(transmitData, 0, 26, 15, 1);
        updateValues.setFont(buttonFont);
        root.add(updateValues, 0, 27, 15, 1);
        toggleStream.setFont(buttonFont);
        root.add(toggleStream, 0, 28, 15, 1);
        rebootButton.setFont(buttonFont);
        root.add(rebootButton, 0, 29, 15, 1);

        lowHueField.setOnKeyTyped(e ->
                fieldUpdateHelper(lowHueField, lowHueSlider, "low-hue"));
        lowSaturationField.setOnKeyTyped(e ->
                fieldUpdateHelper(lowSaturationField, lowSaturationSlider, "low-saturation"));
        lowValueField.setOnKeyTyped(e ->
                fieldUpdateHelper(lowValueField, lowValueSlider, "low-value"));

        highHueField.setOnKeyTyped(e ->
                fieldUpdateHelper(highHueField, highHueSlider, "high-hue"));
        highSaturationField.setOnKeyTyped(e ->
                fieldUpdateHelper(highSaturationField, highSaturationSlider, "high-saturation"));
        highValueField.setOnKeyTyped(e ->
                fieldUpdateHelper(highValueField, highValueSlider, "high-value"));

        lowHueSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(lowHueSlider, lowHueField, "low-hue"));
        lowSaturationSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(lowSaturationSlider, lowSaturationField, "low-saturation"));
        lowValueSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(lowValueSlider, lowValueField, "low-value"));

        highHueSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(highHueSlider, highHueField, "high-hue"));
        highSaturationSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(highSaturationSlider, highHueField, "high-hue"));
        highValueSlider.setOnMouseReleased(e ->
                sliderUpdateHelper(highValueSlider, highValueField, "high-value"));

        valueField.setOnKeyTyped(e ->
        {
            switch (valueField.getText())
            {
                case "low-hue":
                    settingUpdateHelper("low-hue", lowHueField, lowHueSlider);
                    break;
                case "low-saturation":
                    settingUpdateHelper("low-saturation", lowSaturationField, lowSaturationSlider);
                    break;
                case "low-value":
                    settingUpdateHelper("low-value", lowValueField, lowValueSlider);
                    break;
                case "high-hue":
                    settingUpdateHelper("high-hue", highHueField, highHueSlider);
                    break;
                case "high-saturation":
                    settingUpdateHelper("high-saturation", highSaturationField, highSaturationSlider);
                    break;
                case "high-value":
                    settingUpdateHelper("high-value", highValueField, highValueSlider);
                    break;
            }
        });

        transmitData.setOnAction(e ->
                udpHandler.send(getSendProfile(), timeout));
        updateValues.setOnAction(e ->
                udpHandler.send("get config", timeout));
        toggleStream.setOnAction(e ->
                udpHandler.send("switch camera", timeout));
        rebootButton.setOnAction(e ->
                udpHandler.send("reboot", timeout));

        categoriesList.valueProperty().addListener((observableValue, oldSelection, newSelection) ->
                settingsList.setItems(FXCollections.observableList(new ArrayList<>(categories.get(newSelection).keySet()))));

        settingsList.valueProperty().addListener((observableValue, oldSelection, newSelection) ->
        {
            for (String settingLabel : categories.get(categoriesList.getSelectionModel().getSelectedItem()).keySet())
            {
                if (settingLabel.equals(oldSelection))
                {
                    if (valueField.getText() != null && !valueField.getText().isEmpty())
                        categories.get(categoriesList.getSelectionModel().getSelectedItem()).put(settingLabel, valueField.getText());
                }
            }
            valueField.setText(categories.get(categoriesList.getSelectionModel().getSelectedItem()).get(settingsList.getSelectionModel().getSelectedItem()));
        });

        final Timeline udpUpdater = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                {
                    String configLabel = "CONFIGS:";

                    if (udpHandler.getMessage().isEmpty())
                        return;

                    if (udpHandler.getMessage().contains(configLabel))
                    {
                        categories.clear();
                        String message = udpHandler.getMessage().substring(udpHandler.getMessage().indexOf(configLabel) + configLabel.length());
                        String[] lines = message.split("\n");

                        for (String line : lines)
                        {
                            String category = line.split(":")[0];
                            if (category.isEmpty())
                                continue;

                            String settingsString = line.split(":")[1];
                            if (settingsString.isEmpty())
                                continue;

                            HashMap<String, String> settings = new HashMap<>();
                            for (String settingPair : settingsString.split(";"))
                            {
                                if (settingPair.isEmpty())
                                    continue;
                                if (settingPair.split("=")[0].isEmpty() || settingPair.split("=")[1].isEmpty())
                                    continue;

                                settings.put(settingPair.split("=")[0], settingPair.split("=")[1]);
                            }
                            categories.put(category, settings);
                        }
                    }

                    categoriesList.setItems(FXCollections.observableArrayList(categories.keySet()));
                    udpHandler.clearMessage();
                }),
                new KeyFrame(Duration.millis(100))
        );
        udpUpdater.setCycleCount(Timeline.INDEFINITE);

        stage.setOnCloseRequest(event ->
        {
            try
            {
                PrintWriter writer = new PrintWriter("profile.txt", StandardCharsets.UTF_8);
                writer.println(getSaveProfile());
                writer.close();

                System.exit(0);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        stage.setTitle("Vision Communicator");
        stage.setScene(new Scene(root, 485, 750));
        stage.show();

        udpUpdater.play();
    }

    private String getSaveProfile()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("host-ip:").append(hostIPField.getText())
                .append(";send-port:").append(sendPortField.getText())
                .append(";receive-port:").append(receivePortField.getText())
                .append(";low-hue:").append(lowHueField.getText())
                .append(";low-saturation:").append(lowSaturationField.getText())
                .append(";low-value:").append(lowValueField.getText())
                .append(";high-hue:").append(highHueField.getText())
                .append(";high-saturation:").append(highSaturationField.getText())
                .append(";high-value:").append(highValueField.getText());
        return builder.toString();
    }

    private String getSendProfile()
    {
        StringBuilder builder = new StringBuilder("CONFIGS:\n");
        for (String category : categories.keySet())
        {
            builder.append(category)
                    .append(":");
            for (String settingLabel : categories.get(category).keySet())
            {
                builder.append(settingLabel)
                        .append("=")
                        .append(categories.get(category).get(settingLabel))
                        .append(";");
            }
        }
        return builder.toString();
    }

    private void fieldUpdateHelper(TextField field, Slider slider, String setting)
    {
        if (!field.getText().isEmpty())
            slider.setValue(Double.parseDouble(field.getText()));

        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    categories.get(category).put(setting, field.getText());
                }
            }
        }

        transmitData.fire();
    }

    private void sliderUpdateHelper(Slider slider, TextField field, String setting)
    {
        field.setText(String.valueOf(slider.getValue()));

        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    categories.get(category).put(setting, String.valueOf(slider.getValue()));
                }
            }
        }

        transmitData.fire();
    }

    private void settingUpdateHelper(String setting, TextField field, Slider slider)
    {
        double newValue = 0;
        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    newValue = Double.parseDouble(categories.get(category).get(setting));
                }
            }
        }

        field.setText(String.valueOf(newValue));
        slider.setValue(newValue);

        transmitData.fire();
    }
}

/*
TODO:
    -Figure out a way to monitor for these changes on the Pi without using too many resources
 */