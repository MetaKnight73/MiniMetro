package vue;

import controleur.MiniMetro;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ToggleSwitch extends HBox {

    private final Label label = new Label();
    private final Button button = new Button();
    private final MiniMetro m;
    private final String type;

    private SimpleBooleanProperty switchedOn;

    public SimpleBooleanProperty switchOnProperty() {
        return switchedOn;
    }

    private void init() {

        label.setText("");

        if ("nightMode".equals(type)) {
            if (m.isNightMode()) {
                switchedOn = new SimpleBooleanProperty(true);
            } else {
                switchedOn = new SimpleBooleanProperty(false);
            }
        } else if ("audio".equals(type)) {
            if (m.isNoAudio()) {
                switchedOn = new SimpleBooleanProperty(false);
            } else {
                switchedOn = new SimpleBooleanProperty(true);
            }
        }

        getChildren().addAll(label, button);
        button.setOnAction((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        label.setOnMouseClicked((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        setStyle();
        bindProperties();
    }

    private void setStyle() {
        //Default Width
        setWidth(80);

        if ("nightMode".equals(type)) {
            if (m.isNightMode()) {
                setStyle("-fx-background-color: green; -fx-background-radius: 4;");
                label.toFront();
            } else {
                setStyle("-fx-background-color: red; -fx-background-radius: 4;");
                button.toFront();
            }
        }
        if ("audio".equals(type)) {
            if (!m.isNoAudio()) {
                setStyle("-fx-background-color: green; -fx-background-radius: 4;");
                label.toFront();
            } else {
                setStyle("-fx-background-color: red; -fx-background-radius: 4;");
                button.toFront();
            }
        }

        setAlignment(Pos.CENTER_LEFT);
    }

    private void bindProperties() {
        label.prefWidthProperty().bind(widthProperty().divide(2));
        label.prefHeightProperty().bind(heightProperty());
        button.prefWidthProperty().bind(widthProperty().divide(2));
        button.prefHeightProperty().bind(heightProperty());
    }

    public ToggleSwitch(MiniMetro m, String type) {
        this.m = m;
        this.type = type;

        init();
        switchedOn.addListener((a, b, c) -> {
            if (c) {
                label.setText("");
                setStyle("-fx-background-color: green; -fx-background-radius: 0 4 4 0;");
                label.toFront();
                if ("nightMode".equals(type)) {
                    m.setNightMode(true);
                } else if ("audio".equals(type)) {
                    m.setNoAudio(false);
                }
            } else {
                label.setText("");
                setStyle("-fx-background-color: red; -fx-background-radius: 4 0 0 4;");
                button.toFront();
                if ("nightMode".equals(type)) {
                    m.setNightMode(false);
                } else if ("audio".equals(type)) {
                    m.setNoAudio(true);
                }
            }
        });
    }
}