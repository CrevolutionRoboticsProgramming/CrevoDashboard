package org.frc2851.widgets;

import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;

public class VisionStatusReceiver extends CustomWidget
{
    @FXML
    Rectangle backgroundRectangle;
    @FXML
    Text statusText;

    public VisionStatusReceiver()
    {
        super("VisionStatusReceiver.fxml");

        backgroundRectangle.setFill(Paint.valueOf("GRAY"));

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-SEARCHING", (message) ->
        {
            statusText.setText("SEARCHING");
            backgroundRectangle.setFill(Paint.valueOf("GRAY"));
        }));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-LOCKED", (message) ->
        {
            statusText.setText("LOCKED");
            backgroundRectangle.setFill(Paint.valueOf("GREEN"));
        }));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-DOWN", (message) ->
        {
            statusText.setText("DOWN");
            backgroundRectangle.setFill(Paint.valueOf("RED"));
        }));
    }
}
