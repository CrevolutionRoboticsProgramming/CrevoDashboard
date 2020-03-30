package org.frc2851.widgets.crevodashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.frc2851.Constants;
import org.frc2851.Main;
import org.frc2851.widgets.CustomWidget;
import org.frc2851.widgets.visioncommunicator.VisionCommunicator;

public class CrevoDashboard extends CustomWidget
{
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
        mHostIPField.setText(Constants.visionClientIP);
        mSendPortField.setText(String.valueOf(Constants.sendPort));
        mReceivePortField.setText(String.valueOf(Constants.receivePort));

        mHostIPField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));
        mSendPortField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));
        mReceivePortField.setOnAction((ActionEvent e) -> Constants.udpHandler.bind(Integer.parseInt(mReceivePortField.getText())));

        mSaveSettingsButton.setOnAction((ActionEvent e) ->
        {
            Constants.visionClientIP = mHostIPField.getText();
            Constants.sendPort = Integer.parseInt(mSendPortField.getText());
            Constants.receivePort = Integer.parseInt(mReceivePortField.getText());
            Main.saveSettings();
        });

        mSendCustomMessageButton.setOnAction((ActionEvent e) -> Constants.udpHandler.sendTo(mCustomMessageField.getText(), Constants.visionClientIP, Constants.sendPort, 0));

        // Sends our IP to the roboRIO every second so it can send us the control panel color
        final Timeline periodicIPSender = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                        Constants.udpHandler.sendTo("IP:" + Constants.udpHandler.getThisIP(), Constants.roboRioIP, Constants.sendPort, 0)),
                new KeyFrame(Duration.millis(1000))
        );
        periodicIPSender.setCycleCount(Timeline.INDEFINITE);
        periodicIPSender.play();
    }
}
