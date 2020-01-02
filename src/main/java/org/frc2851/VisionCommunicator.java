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
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VisionCommunicator extends Application
{
    private Font textFont = Font.font("Roboto", FontWeight.BOLD, 15);
    private Font textFieldFont = new Font("Courier New", 15);
    private Font buttonFont = new Font("Roboto", 15);

    private HashMap<String, HashMap<String, Object>> configs = new LinkedHashMap<>();

    private String configPath = System.getProperty("user.dir") + File.separator + "config.yaml";

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

        lowHueField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "lowHue"));
        highHueField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "highHue"));
        lowSaturationField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "lowSaturation"));
        highSaturationField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "highSaturation"));
        lowValueField.setOnKeyTyped(e -> fieldUpdateHelper(lowHueField, lowHueSlider, "lowValue"));
        highValueField.setOnKeyTyped(e -> fieldUpdateHelper(highHueField, highHueSlider, "highValue"));

        lowHueSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowHueSlider, lowHueField, "lowHue"));
        highHueSlider.setOnMouseReleased(e -> sliderUpdateHelper(highHueSlider, highHueField, "highHue"));
        lowSaturationSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowSaturationSlider, lowSaturationField, "lowSaturation"));
        highSaturationSlider.setOnMouseReleased(e -> sliderUpdateHelper(highSaturationSlider, highSaturationField, "highSaturation"));
        lowValueSlider.setOnMouseReleased(e -> sliderUpdateHelper(lowValueSlider, lowValueField, "lowValue"));
        highValueSlider.setOnMouseReleased(e -> sliderUpdateHelper(highValueSlider, highValueField, "highValue"));

        Yaml configYaml = new Yaml(new Constructor(InConfig.class));
        InConfig configOnDisk = null;
        try
        {
            configOnDisk = configYaml.load(new FileInputStream(configPath));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        hostIPField.setText(configOnDisk.getHostIP());
        sendPortField.setText(String.valueOf(configOnDisk.getSendPort()));
        receivePortField.setText(String.valueOf(configOnDisk.getReceivePort()));
        lowHueField.setText(String.valueOf(configOnDisk.getLowHue()));
        lowSaturationField.setText(String.valueOf(configOnDisk.getLowSaturation()));
        lowValueField.setText(String.valueOf(configOnDisk.getLowValue()));
        highHueField.setText(String.valueOf(configOnDisk.getHighHue()));
        highSaturationField.setText(String.valueOf(configOnDisk.getHighSaturation()));
        highValueField.setText(String.valueOf(configOnDisk.getHighValue()));

        lowHueSlider.setValue(configOnDisk.getLowHue());
        lowSaturationSlider.setValue(configOnDisk.getLowSaturation());
        lowValueSlider.setValue(configOnDisk.getLowSaturation());
        highHueSlider.setValue(configOnDisk.getHighHue());
        highSaturationSlider.setValue(configOnDisk.getHighSaturation());
        highValueSlider.setValue(configOnDisk.getHighValue());

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
                        configs.clear();
                        Yaml receivedConfigYaml = new Yaml();
                        Map<String, Object> configMap = receivedConfigYaml.load(udpHandler.getMessage().substring(udpHandler.getMessage().indexOf(configLabel) + configLabel.length()));

                        for (Map.Entry<String, Object> pair : configMap.entrySet())
                            configs.put(pair.getKey(), (HashMap<String, Object>) pair.getValue());

                        Tab generalTab = root.getTabs().get(0);
                        root.getTabs().clear();
                        root.getTabs().add(generalTab);

                        for (Map.Entry<String, HashMap<String, Object>> config : configs.entrySet())
                        {
                            Tab tab = new Tab();
                            tab.setText(config.getKey().toUpperCase());

                            GridPane gridPane = new GridPane();
                            gridPane.setHgap(10);
                            gridPane.setVgap(5);
                            gridPane.setPadding(new Insets(10, 10, 10, 10));

                            int rowCounter = 0;
                            int columnCounter = 0;
                            for (String title : config.getValue().keySet())
                            {
                                Text text = new Text(title);
                                text.setFont(textFont);
                                gridPane.add(text, columnCounter, rowCounter * 2);

                                // Populates the text field with the value of the setting (as a string) as given by the config
                                TextField textField = new TextField(config.getValue().get(title).toString());
                                textField.setFont(textFieldFont);
                                gridPane.add(textField, columnCounter, rowCounter * 2 + 1);

                                switch (title)
                                {
                                    case "lowHue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowHueField, lowHueSlider);
                                        });
                                        break;
                                    case "lowSaturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowSaturationField, lowSaturationSlider);
                                        });
                                        break;
                                    case "lowValue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, lowValueField, lowValueSlider);
                                        });
                                        break;
                                    case "highHue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, highHueField, highHueSlider);
                                        });
                                        break;
                                    case "highSaturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, highSaturationField, highSaturationSlider);
                                        });
                                        break;
                                    case "highValue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            configs.get(config.getKey()).replace(title, textField.getText());
                                            settingUpdateHelper(title, highValueField, highValueSlider);
                                        });
                                        break;
                                    default:
                                        textField.setOnKeyTyped(e ->
                                                configs.get(config.getKey()).replace(title, textField.getText()));
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

    private void fieldUpdateHelper(TextField field, Slider slider, String setting)
    {
        if (field.getText().isEmpty())
            return;

        slider.setValue(Double.parseDouble(field.getText()));

        for (String category : configs.keySet())
        {
            for (String settingLabel : configs.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    configs.get(category).put(setting, field.getText());
                }
            }
        }

        getTransmitDataButton().fire();
    }

    private void sliderUpdateHelper(Slider slider, TextField field, String setting)
    {
        field.setText(String.valueOf((int) slider.getValue()));

        for (String category : configs.keySet())
        {
            for (String settingLabel : configs.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    configs.get(category).put(setting, String.valueOf((int) slider.getValue()));
                }
            }
        }

        getTransmitDataButton().fire();
    }

    private void settingUpdateHelper(String setting, TextField field, Slider slider)
    {
        double newValue = 0;
        for (String category : configs.keySet())
        {
            for (String settingLabel : configs.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    if (configs.get(category).get(setting).equals(""))
                        return;
                    newValue = Double.parseDouble(configs.get(category).get(setting).toString());
                }
            }
        }

        field.setText(String.valueOf(newValue));
        slider.setValue(newValue);

        getTransmitDataButton().fire();
    }

    private void saveSettings()
    {
        try
        {
            PrintWriter writer = new PrintWriter(configPath);
            writer.println(getThisConfigAsString());
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getThisConfigAsString()
    {
        Yaml yaml = new Yaml();
        return yaml.dumpAs(new InConfig(hostIPField.getText(), Integer.parseInt(sendPortField.getText()), Integer.parseInt(receivePortField.getText()),
                        Integer.parseInt(lowHueField.getText()), Integer.parseInt(lowSaturationField.getText()), Integer.parseInt(lowValueField.getText()),
                        Integer.parseInt(highHueField.getText()), Integer.parseInt(highSaturationField.getText()), Integer.parseInt(highValueField.getText())),
                Tag.MAP, DumperOptions.FlowStyle.BLOCK);
    }

    private String getPiConfigAsString()
    {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);

        return yaml.dump(configs).replaceAll(Pattern.quote("'"), "");
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
                udpHandler.send("CONFIGS:\n" + getPiConfigAsString(), timeout));
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
