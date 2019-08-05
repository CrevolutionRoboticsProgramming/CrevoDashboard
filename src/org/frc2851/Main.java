package org.frc2851;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main extends Application
{
    private Font textFont = Font.font("Roboto", FontWeight.BOLD, 15);
    private Font textFieldFont = new Font("Courier New", 15);
    private Font buttonFont = new Font("Roboto", 15);

    private Button transmitData = new Button("                                   Transmit Data                                 "),
            toggleStream = new Button("                                   Toggle Stream                                "),
            updateValues = new Button("                                   Update Values                                 "),
            restartProgramButton = new Button("                                  Restart Program                              "),
            rebootButton = new Button("                                        Reboot                                        ");

    private LinkedHashMap<String, LinkedHashMap<String, String>> categories = new LinkedHashMap<>();

    private TabPane root = new TabPane();

    private UDPHandler udpHandler;
    private int timeout = 1000;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        GridPane generalPane = new GridPane();
        generalPane.setHgap(10);
        generalPane.setVgap(5);
        generalPane.setPadding(new Insets(10, 10, 0, 10));

        ArrayList<String> generalTitles = new ArrayList<>(
                Arrays.asList("Host IP", "Send Port", "Receive Port", "Low Hue",
                        "High Hue", "Low Saturation", "High Saturation", "Low Value",
                        "High Value"));
        ArrayList<String> hsvSettingTitles = new ArrayList<>(
                Arrays.asList(
                        "low-hue", "high-hue", "low-saturation", "high-saturation",
                        "low-value", "high-value"
                )
        );

        Tab generalTab = new Tab();
        generalTab.setText("General");

        {
            int rowCounter = 0;
            int columnCounter = 0;
            for (int i = 0; i < generalTitles.size(); ++i)
            {
                String title = generalTitles.get(i);

                Text text = new Text(title);
                text.setFont(textFont);
                generalPane.add(text, columnCounter + 1, rowCounter * 2);

                TextField textField = new TextField();
                textField.setId(title);
                textField.setFont(textFieldFont);

                if (textField.getText().contains("Low") || textField.getText().contains("High"))
                {
                    String settingTitle = hsvSettingTitles.get(i - 3);
                    textField.setOnKeyTyped(e ->
                            fieldUpdateHelper(Objects.requireNonNull(getFieldWithName(generalPane, title)), getSliderWithName(generalPane, title), settingTitle));
                }
                generalPane.add(textField, columnCounter + 1, rowCounter * 2 + 1);

                ++rowCounter;
                if (rowCounter > generalPane.getRowCount())
                {
                    rowCounter = 0;
                    ++columnCounter;
                }
            }
        }

        ArrayList<String> hsvTitles = new ArrayList<>(
                Arrays.asList(
                        "Low Hue", "High Hue", "Low Saturation", "High Saturation",
                        "Low Value", "High Value"
                )
        );

        for (int i = 0; i < hsvTitles.size(); ++i)
        {
            String title = hsvTitles.get(i);
            String settingTitle = hsvSettingTitles.get(i);
            Slider slider = new Slider();
            slider.setId(title);
            slider.setMin(0);
            slider.setMax(255);
            slider.setMajorTickUnit(85);
            slider.setShowTickLabels(true);
            slider.setOnMouseReleased(e ->
                    sliderUpdateHelper(Objects.requireNonNull(getSliderWithName(generalPane, title)), Objects.requireNonNull(getFieldWithName(generalPane, title)), settingTitle));

            generalPane.add(slider, GridPane.getColumnIndex(Objects.requireNonNull(getFieldWithName(generalPane, title))) + 1, GridPane.getRowIndex(Objects.requireNonNull(getFieldWithName(generalPane, title))), 14, 1);
        }

        generalTab.setContent(generalPane);
        generalTab.setClosable(false);
        root.getTabs().add(generalTab);

        Scanner in = new Scanner(new FileReader("profile.txt"));
        StringBuilder inBuilder = new StringBuilder();
        while (in.hasNext())
        {
            inBuilder.append(in.next());
        }
        in.close();
        String profile = inBuilder.toString();
        for (String settingPair : profile.split(";"))
        {
            if (settingPair.split(":").length == 1)
                continue;
            String name = settingPair.split(":")[0];
            String value = settingPair.split(":")[1];
            switch (name)
            {
                case "host-ip":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Host IP")).setText(value);
                    break;
                case "send-port":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Send Port")).setText(value);
                    break;
                case "receive-port":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Receive Port")).setText(value);
                    break;
                case "low-hue":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Low Hue")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "Low Hue")).setValue(Double.parseDouble(value));
                    break;
                case "low-saturation":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Low Saturation")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "Low Saturation")).setValue(Double.parseDouble(value));
                    break;
                case "low-value":
                    Objects.requireNonNull(getFieldWithName(generalPane, "Low Value")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "Low Value")).setValue(Double.parseDouble(value));
                    break;
                case "high-hue":
                    Objects.requireNonNull(getFieldWithName(generalPane, "High Hue")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "High Hue")).setValue(Double.parseDouble(value));
                    break;
                case "high-saturation":
                    Objects.requireNonNull(getFieldWithName(generalPane, "High Saturation")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "High Saturation")).setValue(Double.parseDouble(value));
                    break;
                case "high-value":
                    Objects.requireNonNull(getFieldWithName(generalPane, "High Value")).setText(value);
                    Objects.requireNonNull(getSliderWithName(generalPane, "High Value")).setValue(Double.parseDouble(value));
                    break;
            }
        }

        udpHandler = new UDPHandler(Objects.requireNonNull(getFieldWithName(generalPane, "Host IP")).getText(), Integer.parseInt(Objects.requireNonNull(getFieldWithName(generalPane, "Send Port")).getText()), Integer.parseInt(Objects.requireNonNull(getFieldWithName(generalPane, "Receive Port")).getText()));

        transmitData.setFont(buttonFont);
        generalPane.add(transmitData, 0, 26, 15, 1);
        updateValues.setFont(buttonFont);
        generalPane.add(updateValues, 0, 27, 15, 1);
        toggleStream.setFont(buttonFont);
        generalPane.add(toggleStream, 0, 28, 15, 1);
        restartProgramButton.setFont(buttonFont);
        generalPane.add(restartProgramButton, 0, 29, 15, 1);
        rebootButton.setFont(buttonFont);
        generalPane.add(rebootButton, 0, 30, 15, 1);

        transmitData.setOnAction(e ->
                udpHandler.send(getSendProfile(), timeout));
        updateValues.setOnAction(e ->
                udpHandler.send("get config", timeout));
        toggleStream.setOnAction(e ->
                udpHandler.send("switch camera", timeout));
        restartProgramButton.setOnAction(e ->
                udpHandler.send("restart program", timeout));
        rebootButton.setOnAction(e ->
                udpHandler.send("reboot", timeout));

        final Timeline udpUpdater = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                {
                    String configLabel = "CONFIGS:";

                    if (udpHandler.getMessage().isEmpty())
                        return;

                    if (udpHandler.getMessage().contains(configLabel))
                    {
                        categories.clear();
                        String message = udpHandler.getMessage().substring(udpHandler.getMessage().indexOf(configLabel) + configLabel.length());
                        String[] lines = message.split("\n");

                        root.getTabs().clear();
                        root.getTabs().add(generalTab);

                        for (String line : lines)
                        {
                            if (line.split(":").length == 1)
                                continue;

                            String category = line.split(":")[0];
                            if (category.isEmpty())
                                continue;

                            String settingsString = line.split(":")[1];
                            if (settingsString.isEmpty())
                                continue;

                            LinkedHashMap<String, String> settings = new LinkedHashMap<>();
                            for (String settingPair : settingsString.split(";"))
                            {
                                if (settingPair.isEmpty())
                                    continue;
                                if (settingPair.split("=")[0].isEmpty() || settingPair.split("=")[1].isEmpty())
                                    continue;

                                settings.put(settingPair.split("=")[0], settingPair.split("=")[1]);
                            }

                            Tab tab = new Tab();
                            tab.setText(category);

                            GridPane gridPane = new GridPane();
                            gridPane.setHgap(10);
                            gridPane.setVgap(5);
                            gridPane.setPadding(new Insets(10, 10, 0, 10));

                            int rowCounter = 0;
                            int columnCounter = 0;
                            for (String title : settings.keySet())
                            {
                                Text text = new Text(title);
                                text.setFont(textFont);
                                gridPane.add(text, columnCounter + 1, rowCounter * 2);

                                TextField textField = new TextField(settings.get(title));
                                textField.setFont(textFieldFont);
                                gridPane.add(textField, columnCounter + 1, rowCounter * 2 + 1);

                                switch (title)
                                {
                                    case "low-hue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "Low Hue")), Objects.requireNonNull(getSliderWithName(generalPane, "Low Hue")));
                                        });
                                        break;
                                    case "low-saturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "Low Saturation")), Objects.requireNonNull(getSliderWithName(generalPane, "Low Saturation")));
                                        });
                                        break;
                                    case "low-value":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "Low Value")), Objects.requireNonNull(getSliderWithName(generalPane, "Low Value")));
                                        });
                                        break;
                                    case "high-hue":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "High Hue")), Objects.requireNonNull(getSliderWithName(generalPane, "High Hue")));
                                        });
                                        break;
                                    case "high-saturation":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "High Saturation")), Objects.requireNonNull(getSliderWithName(generalPane, "High Saturation")));
                                        });
                                        break;
                                    case "high-value":
                                        textField.setOnKeyTyped(e ->
                                        {
                                            categories.get(category).replace(title, textField.getText());
                                            settingUpdateHelper(title, Objects.requireNonNull(getFieldWithName(generalPane, "High Value")), Objects.requireNonNull(getSliderWithName(generalPane, "High Value")));
                                        });
                                        break;
                                    default:
                                        textField.setOnKeyTyped(e ->
                                                categories.get(category).replace(title, textField.getText()));
                                        break;
                                }

                                ++rowCounter;
                                if (rowCounter > 7)
                                {
                                    rowCounter = 0;
                                    ++columnCounter;
                                }
                            }

                            Button transmitData = new Button(this.transmitData.getText());
                            transmitData.setFont(buttonFont);
                            generalPane.add(transmitData, 0, 21, 15, 1);
                            Button updateValues = new Button(this.updateValues.getText());
                            updateValues.setFont(buttonFont);
                            generalPane.add(updateValues, 0, 22, 15, 1);
                            Button toggleStream = new Button(this.toggleStream.getText());
                            toggleStream.setFont(buttonFont);
                            generalPane.add(toggleStream, 0, 23, 15, 1);
                            Button restartProgramButton = new Button(this.restartProgramButton.getText());
                            restartProgramButton.setFont(buttonFont);
                            generalPane.add(restartProgramButton, 0, 24, 15, 1);
                            Button rebootButton = new Button(this.rebootButton.getText());
                            rebootButton.setFont(buttonFont);
                            generalPane.add(rebootButton, 0, 25, 15, 1);

                            transmitData.setOnAction(e ->
                                    udpHandler.send(getSendProfile(), timeout));
                            updateValues.setOnAction(e ->
                                    udpHandler.send("get config", timeout));
                            toggleStream.setOnAction(e ->
                                    udpHandler.send("switch camera", timeout));
                            restartProgramButton.setOnAction(e ->
                                    udpHandler.send("restart program", timeout));
                            rebootButton.setOnAction(e ->
                                    udpHandler.send("reboot", timeout));

                            gridPane.add(transmitData, 0, 26, 15, 1);
                            gridPane.add(updateValues, 0, 27, 15, 1);
                            gridPane.add(toggleStream, 0, 28, 15, 1);
                            gridPane.add(restartProgramButton, 0, 29, 15, 1);
                            gridPane.add(rebootButton, 0, 30, 15, 1);

                            tab.setContent(gridPane);
                            tab.setClosable(false);
                            root.getTabs().add(tab);

                            categories.put(category, settings);
                        }
                    }

                    udpHandler.clearMessage();
                }),
                new KeyFrame(Duration.millis(100))
        );
        udpUpdater.setCycleCount(Timeline.INDEFINITE);

        stage.setOnCloseRequest(event ->
        {
            try
            {
                PrintWriter writer = new PrintWriter("profile.txt", StandardCharsets.UTF_8);

                String outProfile = "host-ip:" + Objects.requireNonNull(getFieldWithName(generalPane, "Host IP")).getText() +
                        ";send-port:" + Objects.requireNonNull(getFieldWithName(generalPane, "Send Port")).getText() +
                        ";receive-port:" + Objects.requireNonNull(getFieldWithName(generalPane, "Receive Port")).getText() +
                        ";low-hue:" + Objects.requireNonNull(getFieldWithName(generalPane, "Low Hue")).getText() +
                        ";low-saturation:" + Objects.requireNonNull(getFieldWithName(generalPane, "Low Saturation")).getText() +
                        ";low-value:" + Objects.requireNonNull(getFieldWithName(generalPane, "Low Value")).getText() +
                        ";high-hue:" + Objects.requireNonNull(getFieldWithName(generalPane, "High Hue")).getText() +
                        ";high-saturation:" + Objects.requireNonNull(getFieldWithName(generalPane, "High Saturation")).getText() +
                        ";high-value:" + Objects.requireNonNull(getFieldWithName(generalPane, "High Value")).getText();

                writer.println(outProfile);
                writer.close();

                System.exit(0);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        stage.setTitle("Vision Communicator");
        stage.setScene(new Scene(root, 485, 810));
        stage.show();

        udpUpdater.play();
    }

    private TextField getFieldWithName(GridPane pane, String name)
    {
        for (int i = 0; i < pane.getChildren().size(); ++i)
        {
            Node node = pane.getChildren().get(i);

            if (node instanceof TextField)
            {
                if (node.getId().equals(name))
                    return (TextField) node;
            }
        }
        return null;
    }

    private Slider getSliderWithName(GridPane pane, String name)
    {
        for (int i = 0; i < pane.getChildren().size(); ++i)
        {
            Node node = pane.getChildren().get(i);

            if (node instanceof Slider)
            {
                if (node.getId().equals(name))
                    return (Slider) node;
            }
        }
        return null;
    }

    private String getSendProfile()
    {
        StringBuilder builder = new StringBuilder("CONFIGS:\n");
        for (String category : categories.keySet())
        {
            builder.append(category)
                    .append(":");
            for (String settingLabel : categories.get(category).keySet())
            {
                builder.append(settingLabel)
                        .append("=")
                        .append(categories.get(category).get(settingLabel))
                        .append(";");
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private void fieldUpdateHelper(TextField field, Slider slider, String setting)
    {
        if (field.getText().isEmpty())
            return;

        slider.setValue(Double.parseDouble(field.getText()));

        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    categories.get(category).put(setting, field.getText());
                }
            }
        }

        transmitData.fire();
    }

    private void sliderUpdateHelper(Slider slider, TextField field, String setting)
    {
        field.setText(String.valueOf((int) slider.getValue()));

        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    categories.get(category).put(setting, String.valueOf((int) slider.getValue()));
                }
            }
        }

        transmitData.fire();
    }

    private void settingUpdateHelper(String setting, TextField field, Slider slider)
    {
        double newValue = 0;
        for (String category : categories.keySet())
        {
            for (String settingLabel : categories.get(category).keySet())
            {
                if (settingLabel.equals(setting))
                {
                    if (categories.get(category).get(setting).equals(""))
                        return;
                    newValue = Double.parseDouble(categories.get(category).get(setting));
                }
            }
        }

        field.setText(String.valueOf(newValue));
        slider.setValue(newValue);

        transmitData.fire();
    }
}