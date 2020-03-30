package org.frc2851.widgets.intakestatereceiver;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;
import org.frc2851.widgets.CustomWidget;

public class IntakeStateReceiver extends CustomWidget
{
    @FXML
    private ImageView mImageView;

    public IntakeStateReceiver()
    {
        super("IntakeStateReceiver.fxml");

        mImageView.setImage(new Image(getClass().getResourceAsStream("IntakeRetracted.png")));

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("INTAKE-EXTEND", (message) ->
                mImageView.setImage(new Image(getClass().getResourceAsStream("IntakeExtended.png")))));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("INTAKE-RETRACT", (message) ->
                mImageView.setImage(new Image(getClass().getResourceAsStream("IntakeRetracted.png")))));
    }
}
