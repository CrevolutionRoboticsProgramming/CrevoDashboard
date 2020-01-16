package org.frc2851.widgets;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.frc2851.Config;
import org.frc2851.Constants;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CrevoDashboard extends CustomWidget
{
    private Config mConfig;
    private String mConfigPath;

    @FXML
    private HBox root;
    @FXML
    private TextField hostIPField;
    @FXML
    private TextField sendPortField;
    @FXML
    private TextField receivePortField;
    @FXML
    private Button saveSettingsButton;
    @FXML
    private VisionCommunicator visionCommunicator;
    @FXML
    private WebView streamViewer;
    @FXML
    private Button refreshButton;

    public CrevoDashboard()
    {
        super("CrevoDashboard.fxml");
    }

    @FXML
    public void initialize()
    {
        String configPath = System.getProperty("user.dir") + File.separator + "config.yaml";
        mConfigPath = configPath;

        Yaml configYaml = new Yaml(new Constructor(Config.class));
        try
        {
            mConfig = configYaml.load(new FileInputStream(mConfigPath));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        Constants.visionClientIP = mConfig.hostIP;
        Constants.sendPort = mConfig.sendPort;
        Constants.udpHandler.bind(mConfig.receivePort);

        hostIPField.setText(mConfig.hostIP);
        sendPortField.setText(String.valueOf(mConfig.sendPort));
        receivePortField.setText(String.valueOf(mConfig.receivePort));

        hostIPField.setOnKeyPressed((KeyEvent e) ->
                tryRebind(e.getText()));
        sendPortField.setOnKeyPressed((KeyEvent e) ->
                tryRebind(e.getText()));
        receivePortField.setOnKeyPressed((KeyEvent e) ->
                tryRebind(e.getText()));

        saveSettingsButton.setOnAction((ActionEvent e) ->
                saveSettings());

        // Sends our IP to the roboRIO every second so it can send us the control panel color
        final Timeline periodicIPSender = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                        Constants.udpHandler.sendTo(Constants.udpHandler.getThisIP(), Constants.roboRioIP, Constants.sendPort, 0)),
                new KeyFrame(Duration.millis(1000))
        );
        periodicIPSender.setCycleCount(Timeline.INDEFINITE);
        periodicIPSender.play();
    }

    private void tryRebind(String keyText)
    {
        // If Enter was pressed
        if (keyText.contains("\r") || keyText.contains("\n"))
            Constants.udpHandler.bind(Integer.parseInt(receivePortField.getText()));
    }

    private void saveSettings()
    {
        try
        {
            PrintWriter writer = new PrintWriter(mConfigPath);
            writer.println(new Yaml().dumpAs(new Config(hostIPField.getText(), Integer.parseInt(sendPortField.getText()), Integer.parseInt(receivePortField.getText())),
                    Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
