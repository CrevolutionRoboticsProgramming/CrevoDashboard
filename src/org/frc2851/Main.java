package org.frc2851;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.frc2851.widgets.crevodashboard.CrevoDashboard;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main extends Application
{
    private static String mConfigPath = System.getProperty("user.dir") + File.separator + "config.yaml";

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        Config config = new Config();

        Yaml configYaml = new Yaml(new Constructor(Config.class));
        try
        {
            config = configYaml.load(new FileInputStream(mConfigPath));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        Constants.visionClientIP = config.hostIP;
        Constants.sendPort = config.sendPort;
        Constants.receivePort = config.receivePort;
        Constants.udpHandler.bind(config.receivePort);
        Constants.videoReceiverUrl = config.videoReceiverUrl;

        stage.setOnCloseRequest(e ->
                System.exit(0));

        stage.setTitle("CrevoDashboard");
        stage.setScene(new Scene(new CrevoDashboard()));
        stage.show();
    }

    public static void saveSettings()
    {
        try
        {
            PrintWriter writer = new PrintWriter(mConfigPath);
            writer.println(new Yaml().dumpAs(new Config(Constants.visionClientIP, Constants.sendPort,
                            Constants.receivePort, Constants.videoReceiverUrl),
                    Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}