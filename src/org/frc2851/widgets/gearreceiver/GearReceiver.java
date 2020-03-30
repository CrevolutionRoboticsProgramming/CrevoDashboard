package org.frc2851.widgets.gearreceiver;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class GearReceiver extends CustomWidget
{
    @FXML
    private Text mText;

    public GearReceiver()
    {
        super("GearReceiver.fxml");

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-HIGH", (message) ->
                mText.setText("HIGH")));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("GEAR-LOW", (message) ->
                mText.setText("LOW")));
    }
}
