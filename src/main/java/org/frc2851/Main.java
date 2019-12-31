package org.frc2851;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main extends Application
{
    private Font textFont = Font.font("Roboto", FontWeight.BOLD, 15);
    private Font textFieldFont = new Font("Courier New", 15);
    private Font buttonFont = new Font("Roboto", 15);

    private LinkedHashMap<String, LinkedHashMap<String, String>> categories = new LinkedHashMap<>();

    private UDPHandler udpHandler;
    private int timeout = 1000;

    @FXML
    private TabPane root;
    @FXML
    private SplitPane generalSplitPane;
    @FXML
    private TextField hostIPField;
    @FXML
    private TextField sendPortField;
    @FXML
    private TextField receivePortField;
    @FXML
    private TextField lowHueField;
    @FXML
    private TextField highHueField;
    @FXML
    private TextField lowSaturationField;
    @FXML
    private TextField highSaturationField;
    @FXML
    private TextField lowValueField;
    @FXML
    private TextField highValueField;
    @FXML
    private Slider lowHueSlider;
    @FXML
    private Slider highHueSlider;
    @FXML
    private Slider lowSaturationSlider;
    @FXML
    private Slider highSaturationSlider;
    @FXML
    private Slider lowValueSlider;
    @FXML
    private Slider highValueSlider;

    public static void main(String[] args)
    {
        launch(args);
    }

    @FXML
    private void initialize()
    {
        generalSplitPane.getItems().add(getButtons());

        double oldGeneralPaneDividerPosition = generalSplitPane.getDividers().get(0).getPosition();
        generalSplitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> generalSplitPane.getDividers().get(0).setPosition(oldGeneralPaneDividerPosition));

        lowHueField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "low-hue"));
        highHueField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "high-hue"));
        lowSaturationField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "low-saturation"));
        highSaturationField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "high-saturation"));
        lowValueField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "low-value"));
        highValueField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "high-value"));

        lowHueSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowHueSlider, lowHueField, "low-hue"));
        highHueSlider.setOnMouseReleased(e -> sliderUpdateHelper(highHueSlider, highHueField, "high-hue"));
        lowSaturationSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowSaturationSlider, lowSaturationField, "low-saturation"));
        highSaturationSlider.setOnMouseReleased(e -> sliderUpdateHelper(highSaturationSlider, highSaturationField, "high-saturation"));
        lowValueSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowValueSlider, lowValueField, "low-value"));
        highValueSlider.setOnMouseReleased(e -> sliderUpdateHelper(highValueSlider, highValueField, "high-value"));

        Scanner in = null;
        try
        {
            in = new Scanner(new FileReader("profile.txt"));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        StringBuilder inBuilder = new StringBuilder();
        while (in.hasNext())
        {
            inBuilder.append(in.next());
        }
        in.close();
        String profile = inBuilder.toString();
        for (String settingPair : profile.split(";"))
        {
            if (settingPair.split("=").length == 1)
                continue;
            String name = settingPair.split("=")[0];
            String value = settingPair.split("=")[1];
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

        udpHandler = new UDPHandler(hostIPField.getText(), Integer.parseInt(sendPortField.getText()), Integer.parseInt(receivePortField.getText()));

        TabPane finalRoot = root;
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

                        Tab generalTab = root.getTabs().get(0);
                        root.getTabs().clear();
                        root.getTabs().add(generalTab);

                        for (String line : lines)
                        {
                            if (line.split(":").length == 1)
                                continue;

                            String category = line.split(":")[0];
                            if (category.isEmpty())
                                continue;

                            String settingsString = line.split(":")[1];
                            if (settingsString.isEmpty())
                                continue;

                            LinkedHashMap<String, String> settings = new LinkedHashMap<>();
                            for (String settingPair : settingsString.split(";"))
                            {
                                if (settingPair.isEmpty())
                                    continue;
                                if (settingPair.split("=")[0].isEmpty() || settingPair.split("=")[1].isEmpty())
                                    continue;

                                settings.put(settingPair.split("=")[0], settingPair.split("=")[1]);
                            }

                            Tab tab = new Tab();
                            tab.setText(category);

                            GridPane gridPane = new GridPane();
                            gridPane.setHgap(10);
                            gridPane.setVgap(5);
                            gridPane.setPadding(new Insets(10, 10, 10, 10));

                            int rowCounter = 0;
                            int columnCounter = 0;
                            for (String title : settings.keySet())
                            {
                                Text text = new Text(title);
                                text.setFont(textFont);
                                gridPane.add(text, columnCounter, rowCounter * 2);

                                TextField textField = new TextField(settings.get(title));
                                textField.setFont(textFieldFont);
                                gridPane.add(textField, columnCounter, rowCounter * 2 + 1);

                                switch (title)
                                {
                                    case "low-hue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowHueField, lowHueSlider);
                                        });
                                        break;
                                    case "low-saturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowSaturationField, lowSaturationSlider);
                                        });
                                        break;
                                    case "low-value":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowValueField, lowValueSlider);
                                        });
                                        break;
                                    case "high-hue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, highHueField, highHueSlider);
                                        });
                                        break;
                                    case "high-saturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, highSaturationField, highSaturationSlider);
                                        });
                                        break;
                                    case "high-value":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, highValueField, highValueSlider);
                                        });
                                        break;
                                    default:
                                        textField.setOnKeyTyped(e ->
                                                categories.get(category).replace(title, textField.getText()));
                                        break;
                                }

                                ++rowCounter;
                                if (rowCounter > 7)
                                {
                                    rowCounter = 0;
                                    ++columnCounter;
                                }
                            }

                            SplitPane splitPane = new SplitPane();
                            splitPane.setDisable(true);
                            splitPane.setOrientation(Orientation.VERTICAL);
                            splitPane.getItems().removeAll();
                            splitPane.getItems().add(gridPane);
                            splitPane.getItems().add(getButtons());

                            double oldDividerPosition = splitPane.getDividers().get(0).getPosition();
                            splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> splitPane.getDividers().get(0).setPosition(oldDividerPosition));

                            tab.setContent(splitPane);
                            tab.setClosable(false);
                            finalRoot.getTabs().add(tab);

                            categories.put(category, settings);
                        }
                    }

                    udpHandler.clearMessage();
                }),
                new KeyFrame(Duration.millis(100))
        );
        udpUpdater.setCycleCount(Timeline.INDEFINITE);

        udpUpdater.play();
    }

    @Override
    public void start(Stage stage)
    {
        stage.setOnCloseRequest(e ->
                System.exit(0));

        Scene scene = null;

        try
        {
            scene = new Scene(FXMLLoader.load(getClass().getResource("VisionCommunicator.fxml")));
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        stage.setTitle("Vision Communicator");
        stage.setScene(scene);
        stage.show();
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
            builder.append('\n');
        }
        return builder.toString();
    }

    private void fieldUpdateHelper(TextField field, Slider slider, String setting)
    {
        if (field.getText().isEmpty())
            return;

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

        getTransmitDataButton().fire();
    }

    private void sliderUpdateHelper(Slider slider, TextField field, String setting)
    {
        field.setText(String.valueOf((int) slider.getValue()));

        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    categories.get(category).put(setting, String.valueOf((int) slider.getValue()));
                }
            }
        }

        getTransmitDataButton().fire();
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
                    if (categories.get(category).get(setting).equals(""))
                        return;
                    newValue = Double.parseDouble(categories.get(category).get(setting));
                }
            }
        }

        field.setText(String.valueOf(newValue));
        slider.setValue(newValue);

        getTransmitDataButton().fire();
    }

    @FXML
    private void saveSettings()
    {
        try
        {
            PrintWriter writer = new PrintWriter("profile.txt");

            String outProfile = "host-ip=" + hostIPField.getText() +
                    ";send-port=" + sendPortField.getText() +
                    ";receive-port=" + receivePortField.getText() +
                    ";low-hue=" + sendPortField.getText() +
                    ";low-saturation=" + lowSaturationField.getText() +
                    ";low-value=" + lowValueField.getText() +
                    ";high-hue=" + highHueField.getText() +
                    ";high-saturation=" + highSaturationField.getText() +
                    ";high-value=" + highValueField.getText();

            writer.println(outProfile);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Button getDefaultButton(String text)
    {
        Button button = new Button(text);
        button.setMnemonicParsing(false);
        button.setPrefHeight(0);
        button.setPrefWidth(240);
        button.setFont(buttonFont);
        return button;
    }

    private Button getSaveSettingsButton()
    {
        Button saveSettingsButton = getDefaultButton("Save Settings");
        saveSettingsButton.setOnAction(e ->
                saveSettings());
        return saveSettingsButton;
    }

    private Button getTransmitDataButton()
    {
        Button transmitDataButton = getDefaultButton("Transmit Data");
        transmitDataButton.setOnAction(e ->
                udpHandler.send(getSendProfile(), timeout));
        return transmitDataButton;
    }

    private Button getUpdateValuesButton()
    {
        Button updateValuesButton = getDefaultButton("Update Values");
        updateValuesButton.setOnAction(e ->
                udpHandler.send("get config", timeout));
        return updateValuesButton;
    }

    private Button getToggleStreamButton()
    {
        Button toggleStreamButton = getDefaultButton("Toggle Stream");
        toggleStreamButton.setOnAction(e ->
                udpHandler.send("switch camera", timeout));
        return toggleStreamButton;
    }

    private Button getRestartProgramButton()
    {
        Button restartProgramButton = getDefaultButton("Restart Program");
        restartProgramButton.setOnAction(e ->
                udpHandler.send("restart program", 0));
        return restartProgramButton;
    }

    private Button getRebootButton()
    {
        Button rebootButton = getDefaultButton("Reboot");
        rebootButton.setOnAction(e ->
                udpHandler.send("reboot", 0));
        return rebootButton;
    }

    private AnchorPane getButtons()
    {
        GridPane gridPane = new GridPane();
        gridPane.add(getSaveSettingsButton(), 0, 0);
        gridPane.add(getTransmitDataButton(), 1, 0);
        gridPane.add(getUpdateValuesButton(), 0, 1);
        gridPane.add(getToggleStreamButton(), 1, 1);
        gridPane.add(getRestartProgramButton(), 0, 2);
        gridPane.add(getRebootButton(), 1, 2);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        AnchorPane anchorPane = new AnchorPane(gridPane);
        anchorPane.setMaxSize(0, 0);

        return anchorPane;
    }
}
