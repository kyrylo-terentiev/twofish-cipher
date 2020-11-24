package twofish;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller {

    @FXML
    private ToggleButton encodeButton;
    @FXML
    private ToggleButton decodeButton;
    @FXML
    private Button proceedButton;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea keyText;
    @FXML
    private TextArea outputText;
    @FXML
    private Slider keyLengthSlider;
    @FXML
    private Label inputError;
    @FXML
    private Label keyError;
    @FXML
    private Label outputError;

    private ToggleGroup modeGroup;
    private boolean isEncoding = true;

    @FXML
    public void initialize() {
        setupToggleGroup();
        setupSliders();
        setupTextAreas();
        setupProceedButton();
        setupErrors();
    }

    private void setupToggleGroup() {
        encodeButton.setToggleGroup(modeGroup);
        modeGroup = new ToggleGroup();
        encodeButton.setToggleGroup(modeGroup);
        decodeButton.setToggleGroup(modeGroup);
        modeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
                proceedButton.setText(oldVal == encodeButton ? "Encode" : "Decode");
                isEncoding = oldVal == encodeButton;
            } else {
                proceedButton.setText(newVal == encodeButton ? "Encode" : "Decode");
                isEncoding = newVal == encodeButton;
            }
            if (isEncoding && !outputText.getText().isEmpty()) {
                inputText.setText(outputText.getText());
            }
            if (!isEncoding && !outputText.getText().isEmpty()) {
                inputText.setText(outputText.getText());
            }
        });
    }

    private void setupSliders() {
        keyLengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 16 && newVal.intValue() < 20) {
                keyLengthSlider.setValue(16);
            } else if (newVal.intValue() >= 20 && newVal.intValue() < 28) {
                keyLengthSlider.setValue(24);
            } else {
                keyLengthSlider.setValue(32);
            }
            if (keyText.getText().length() > keyLengthSlider.getValue()) {
                keyError.setText("Max. key length is " + (int) keyLengthSlider.getValue());
                keyError.setVisible(true);
            } else if (keyText.getText().length() > 0) {
                keyError.setVisible(false);
            }
        });
    }

    private void setupTextAreas() {
        keyText.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > keyLengthSlider.getValue()) {
                keyError.setText("Max. key length is " + (int) keyLengthSlider.getValue());
                keyError.setVisible(true);
            } else if (newVal.isEmpty()) {
                keyError.setText("Please enter the key");
                keyError.setVisible(true);
            } else {
                keyError.setVisible(false);
            }
        });
        inputText.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                inputError.setText("Please enter the text to " + (isEncoding ? "encode" : "decode"));
                inputError.setVisible(true);
            } else {
                inputError.setVisible(false);
            }
        });
    }

    private void setupProceedButton() {
        outputError.setVisible(false);
        proceedButton.setOnAction(e -> {
            boolean noErrors = !inputError.isVisible() && !keyError.isVisible();
            if (noErrors) {
                try {
                    String result;
                    if (isEncoding) {
                        result = TwofishAlgorithm.textEncrypt(inputText.getText(), keyText.getText().trim(), (int) keyLengthSlider.getValue());
                    } else {
                        result = TwofishAlgorithm.textDecrypt(inputText.getText(), keyText.getText().trim(), (int) keyLengthSlider.getValue());
                    }
                    outputText.setText(result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.toString());
                    outputError.setText(e.toString());
                    outputError.setVisible(true);
                }
            }
        });
    }

    private void setupErrors() {
        inputError.setText("Please enter the text to " + (isEncoding ? "encode" : "decode"));
        inputError.setVisible(true);
        keyError.setText("Please enter the key");
        keyError.setVisible(true);
    }
}
