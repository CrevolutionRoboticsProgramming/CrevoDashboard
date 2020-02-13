package org.frc2851.widgets;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.frc2851.Constants;
import org.frc2851.UDPHandler;

public class VisionStatusReceiver extends CustomWidget
{
    @FXML
    Rectangle backgroundRectangle;
    @FXML
    Text statusText;

    private long mLast = System.currentTimeMillis();

    public VisionStatusReceiver()
    {
        super("VisionStatusReceiver.fxml");

        setDown();

        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-SEARCHING", (message) ->
        {
            setSearching();
            mLast = System.currentTimeMillis();
        }));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-LOCKED", (message) ->
        {
            setLocked();
            mLast = System.currentTimeMillis();
        }));
        Constants.udpHandler.addReceiver(new UDPHandler.MessageReceiver("VISION-DOWN", (message) ->
        {
            setDown();
            mLast = System.currentTimeMillis();
        }));


        final Timeline updater = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                {
                    if (System.currentTimeMillis() - mLast > 1000)
                        setDown();
                }),
                new KeyFrame(Duration.millis(250))
        );
        updater.setCycleCount(Timeline.INDEFINITE);
        updater.play();
    }

    private void setSearching()
    {
        statusText.setText("SEARCHING");
        backgroundRectangle.setFill(Paint.valueOf("GRAY"));
    }

    private void setLocked()
    {
        statusText.setText("LOCKED");
        backgroundRectangle.setFill(Paint.valueOf("GREEN"));
    }

    private void setDown()
    {
        statusText.setText("DOWN");
        backgroundRectangle.setFill(Paint.valueOf("RED"));
    }
}
