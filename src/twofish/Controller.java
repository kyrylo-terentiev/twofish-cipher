package twofish;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


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
    @FXML
    private Hyperlink githubLink;
    @FXML
    private Button licenseButton;


    private ToggleGroup modeGroup;
    private boolean isEncoding = true;

    @FXML
    public void initialize() {
        setupToggleGroup();
        setupSliders();
        setupTextAreas();
        setupProceedButton();
        setupErrors();
        setupInfo();
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
        keyText.setWrapText(true);
        inputText.setWrapText(true);
        outputText.setWrapText(true);
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

    private void setupInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, LICENSE_TEXT, ButtonType.OK);
        alert.setTitle("License");
        alert.setHeaderText("License");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        licenseButton.setOnAction(e -> alert.showAndWait());
        githubLink.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(URI.create(githubLink.getText()));
                    } catch (IOException ioException) {
                        // ignore
                    }
                }
            }
        });
    }

    private static final String LICENSE_TEXT = "Copyright 2020 Kyrylo Terentiev\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.";
}
