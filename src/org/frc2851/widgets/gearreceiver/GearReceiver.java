package org.frc2851.widgets.gearreceiver;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class GearReceiver extends CustomWidget
{
    @FXML
    private Label mLabel;

    public GearReceiver()
    {
        super("GearReceiver.fxml");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-HIGH", (message) ->
                mLabel.setText("HIGH")));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-LOW", (message) ->
                mLabel.setText("LOW")));
    }
}
