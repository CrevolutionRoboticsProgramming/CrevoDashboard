package org.frc2851.widgets;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;

public class IntakeStateReceiver extends CustomWidget
{
    @FXML
    private ImageView imageView;

    public IntakeStateReceiver()
    {
        super("IntakeStateReceiver.fxml");

        imageView.setImage(new Image(getClass().getResourceAsStream("IntakeRetracted.png")));

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("INTAKE-EXTENDED", (message) ->
                imageView.setImage(new Image(getClass().getResourceAsStream("IntakeExtended.png")))));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("INTAKE-RETRACTED", (message) ->
                imageView.setImage(new Image(getClass().getResourceAsStream("IntakeRetracted.png")))));
    }
}
