package org.frc2851.widgets.gearreceiver;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class GearReceiver extends CustomWidget
{
    @FXML
    private Text text;

    public GearReceiver()
    {
        super("GearReceiver.fxml");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-HIGH", (message) ->
                text.setText("HIGH")));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-LOW", (message) ->
                text.setText("LOW")));
    }
}
