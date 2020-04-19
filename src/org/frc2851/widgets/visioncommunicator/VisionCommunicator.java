package org.frc2851.widgets.visioncommunicator;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VisionCommunicator extends CustomWidget
{
    private HashMap<String, HashMap<String, Object>> mConfigs = new LinkedHashMap<>();

    @FXML
    private TabPane mRoot;
    @FXML
    private SplitPane mGeneralSplitPane;
    @FXML
    private TextField mLowHueField;
    @FXML
    private TextField mHighHueField;
    @FXML
    private TextField mLowSaturationField;
    @FXML
    private TextField mHighSaturationField;
    @FXML
    private TextField mLowValueField;
    @FXML
    private TextField mHighValueField;
    @FXML
    private Slider mLowHueSlider;
    @FXML
    private Slider mHighHueSlider;
    @FXML
    private Slider mLowSaturationSlider;
    @FXML
    private Slider mHighSaturationSlider;
    @FXML
    private Slider mLowValueSlider;
    @FXML
    private Slider mHighValueSlider;

    public VisionCommunicator()
    {
        super("VisionCommunicator.fxml");
    }

    @FXML
    public void initialize()
    {
        mGeneralSplitPane.getItems().add(getAnchorPaneWithButtons());

        double oldGeneralPaneDividerPosition = mGeneralSplitPane.getDividers().get(0).getPosition();
        mGeneralSplitPane.getDividers().get(0).positionProperty()
                .addListener((observable, oldValue, newValue) -> mGeneralSplitPane.getDividers().get(0).setPosition(oldGeneralPaneDividerPosition));

        bind(mLowHueField, mLowHueSlider, "lowHue");
        bind(mHighHueField, mHighHueSlider, "highHue");
        bind(mLowSaturationField, mLowSaturationSlider, "lowSaturation");
        bind(mHighSaturationField, mHighSaturationSlider, "highSaturation");
        bind(mLowValueField, mLowValueSlider, "lowValue");
        bind(mHighValueField, mHighValueSlider, "highValue");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("CONFIGS:", this::receiveConfigs));
    }

    private void receiveConfigs(String message)
    {
        mConfigs.clear();
        Yaml receivedConfigYaml = new Yaml();
        Map<String, Object> configMap = receivedConfigYaml.load(message);

        for (Map.Entry<String, Object> pair : configMap.entrySet())
            mConfigs.put(pair.getKey(), (HashMap<String, Object>) pair.getValue());

        Tab generalTab = mRoot.getTabs().get(0);
        mRoot.getTabs().clear();
        mRoot.getTabs().add(generalTab);

        for (Map.Entry<String, HashMap<String, Object>> config : mConfigs.entrySet())
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
                Label text = new Label(title);
                text.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
                gridPane.add(text, columnCounter, rowCounter * 2);

                // Populates the text field with the value of the setting (as a string) as given by the config
                TextField textField = new TextField(config.getValue().get(title).toString());
                textField.setFont(Font.font("Courier New", 15));
                gridPane.add(textField, columnCounter, rowCounter * 2 + 1);

                textField.setOnKeyTyped((KeyEvent e) -> mConfigs.get(config.getKey()).replace(title, textField.getText()));

                switch (title)
                {
                    case "lowHue":
                        mLowHueField.setText(textField.getText());
                        mLowHueSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                    case "lowSaturation":
                        mLowSaturationField.setText(textField.getText());
                        mLowSaturationSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                    case "lowValue":
                        mLowValueField.setText(textField.getText());
                        mLowValueSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                    case "highHue":
                        mHighHueField.setText(textField.getText());
                        mHighHueSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                    case "highSaturation":
                        mHighSaturationField.setText(textField.getText());
                        mHighSaturationSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                    case "highValue":
                        mHighValueField.setText(textField.getText());
                        mHighValueSlider.setValue(Double.parseDouble(textField.getText()));
                        break;
                }

                ++rowCounter;
                if (rowCounter >= 6)
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
            splitPane.getItems().add(getAnchorPaneWithButtons());

            double oldDividerPosition = splitPane.getDividers().get(0).getPosition();
            splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> splitPane.getDividers().get(0).setPosition(oldDividerPosition));

            tab.setContent(splitPane);
            tab.setClosable(false);
            mRoot.getTabs().add(tab);
        }
    }

    private void bind(TextField field, Slider slider, String setting)
    {
        field.setOnKeyTyped((KeyEvent e) ->
        {
            if (field.getText().isEmpty())
                return;

            slider.setValue(Double.parseDouble(field.getText()));

            for (String category : mConfigs.keySet())
            {
                for (String settingLabel : mConfigs.get(category).keySet())
                {
                    if (settingLabel.equals(setting))
                    {
                        mConfigs.get(category).put(setting, field.getText());
                    }
                }
            }

            getTransmitDataButton().fire();
        });

        slider.setOnMouseReleased((MouseEvent e) ->
        {
            field.setText(String.valueOf((int) slider.getValue()));

            for (String category : mConfigs.keySet())
            {
                for (String settingLabel : mConfigs.get(category).keySet())
                {
                    if (settingLabel.equals(setting))
                    {
                        mConfigs.get(category).put(setting, String.valueOf((int) slider.getValue()));
                    }
                }
            }

            getTransmitDataButton().fire();
        });
    }

    private void settingUpdateHelper(String setting, TextField field, Slider slider)
    {
        double newValue = 0;
        for (String category : mConfigs.keySet())
        {
            for (String settingLabel : mConfigs.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    if (mConfigs.get(category).get(setting).equals(""))
                        return;
                    newValue = Double.parseDouble(mConfigs.get(category).get(setting).toString());
                }
            }
        }

        field.setText(String.valueOf(newValue));
        slider.setValue(newValue);

        getTransmitDataButton().fire();
    }

    private Button getDefaultButton(String text)
    {
        Button button = new Button(text);
        button.setMnemonicParsing(false);
        button.setPrefHeight(0);
        button.setPrefWidth(mRoot.getPrefWidth() / 2 - 10);
        button.setFont(Font.font("Courier New", 14));
        return button;
    }

    private Button getTransmitDataButton()
    {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Button transmitDataButton = getDefaultButton("Transmit Data");
        transmitDataButton.setPrefWidth(mRoot.getPrefWidth() - 5);
        transmitDataButton.setOnAction(e ->
                Constants.udpHandler.sendTo("CONFIGS:\n" + new Yaml(dumperOptions).dump(mConfigs).replaceAll(Pattern.quote("'"), ""), Constants.visionClientIP, Constants.sendPort, 0));
        return transmitDataButton;
    }

    private Button getUpdateValuesButton()
    {
        Button updateValuesButton = getDefaultButton("Update Values");
        updateValuesButton.setOnAction(e ->
                Constants.udpHandler.sendTo("get config", Constants.visionClientIP, Constants.sendPort, 0));
        return updateValuesButton;
    }

    private Button getToggleStreamButton()
    {
        Button toggleStreamButton = getDefaultButton("Toggle Stream");
        toggleStreamButton.setOnAction(e ->
                Constants.udpHandler.sendTo("switch camera", Constants.visionClientIP, Constants.sendPort, 0));
        return toggleStreamButton;
    }

    private Button getRestartProgramButton()
    {
        Button restartProgramButton = getDefaultButton("Restart Program");
        restartProgramButton.setOnAction(e ->
                Constants.udpHandler.sendTo("restart program", Constants.visionClientIP, Constants.sendPort, 0));
        return restartProgramButton;
    }

    private Button getRebootButton()
    {
        Button rebootButton = getDefaultButton("Reboot");
        rebootButton.setOnAction(e ->
                Constants.udpHandler.sendTo("reboot", Constants.visionClientIP, Constants.sendPort, 0));
        return rebootButton;
    }

    private AnchorPane getAnchorPaneWithButtons()
    {
        GridPane gridPane = new GridPane();

        gridPane.add(getTransmitDataButton(), 0, 0, 2, 1);
        gridPane.add(getUpdateValuesButton(), 0, 1);
        gridPane.add(getToggleStreamButton(), 1, 1);
        gridPane.add(getRestartProgramButton(), 0, 2);
        gridPane.add(getRebootButton(), 1, 2);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.getColumnConstraints().add(0, new ColumnConstraints(mRoot.getPrefWidth() / 2));
        gridPane.getColumnConstraints().add(1, new ColumnConstraints(mRoot.getPrefWidth() / 2));

        AnchorPane anchorPane = new AnchorPane(gridPane);
        anchorPane.setMaxHeight(0);
        //anchorPane.setMaxSize(0, 0);

        return anchorPane;
    }
}
