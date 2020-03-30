package org.frc2851.widgets.crevodashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.frc2851.Config;
import org.frc2851.Constants;
import org.frc2851.widgets.CustomWidget;
import org.frc2851.widgets.visioncommunicator.VisionCommunicator;
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
    private HBox mRoot;
    @FXML
    private TextField mHostIPField;
    @FXML
    private TextField mSendPortField;
    @FXML
    private TextField mReceivePortField;
    @FXML
    private Button mSaveSettingsButton;
    @FXML
    private TextField mCustomMessageField;
    @FXML
    private Button mSendCustomMessageButton;
    @FXML
    private VisionCommunicator mVisionCommunicator;
    @FXML
    private Button mRefreshButton;

    public CrevoDashboard()
    {
        super("CrevoDashboard.fxml");
    }

    @FXML
    public void initialize()
    {
        mConfigPath = System.getProperty("user.dir") + File.separator + "config.yaml";

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

        mHostIPField.setText(mConfig.hostIP);
        mSendPortField.setText(String.valueOf(mConfig.sendPort));
        mReceivePortField.setText(String.valueOf(mConfig.receivePort));

        mHostIPField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));
        mSendPortField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));
        mReceivePortField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));

        mSaveSettingsButton.setOnAction((ActionEvent e) -> saveSettings());

        mSendCustomMessageButton.setOnAction((ActionEvent e) -> Constants.udpHandler.sendTo(mCustomMessageField.getText(), mConfig.hostIP, Constants.sendPort, 0));

        // Sends our IP to the roboRIO every second so it can send us the control panel color
        final Timeline periodicIPSender = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                        Constants.udpHandler.sendTo("IP:" + Constants.udpHandler.getThisIP(), Constants.roboRioIP, Constants.sendPort, 0)),
                new KeyFrame(Duration.millis(1000))
        );
        periodicIPSender.setCycleCount(Timeline.INDEFINITE);
        periodicIPSender.play();
    }

    private void saveSettings()
    {
        try
        {
            PrintWriter writer = new PrintWriter(mConfigPath);
            writer.println(new Yaml().dumpAs(new Config(mHostIPField.getText(), Integer.parseInt(mSendPortField.getText()), Integer.parseInt(mReceivePortField.getText())),
                    Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
