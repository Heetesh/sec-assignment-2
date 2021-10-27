package texteditor.application.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import texteditor.application.controller.FileIO;
import texteditor.application.controller.FileIOException;

import java.io.File;
import java.util.ResourceBundle;

public class LoadSaveUI {
    private Stage stage;
    private FileIO fileIO;
    private ResourceBundle bundle;
    private Dialog<String> encodingDialog;
    private FileChooser fileDialog = new FileChooser();
    private TextArea textArea;

    private final static int SPACING = 8;

    public LoadSaveUI(Stage stage, FileIO fileIO, ResourceBundle bundle, TextArea textArea) {
        this.stage = stage;
        this.fileIO = fileIO;
        this.bundle = bundle;
        this.textArea = textArea;
    }

    // Dialog box to request encoding type from user
    private String getEncoding() {
        if (encodingDialog == null) {
            var encodingComboBox = new ComboBox<String>();
            var content = new FlowPane();
            encodingDialog = new Dialog<>();
            encodingDialog.setTitle(this.bundle.getString("encodingDialog"));
            encodingDialog.getDialogPane().setContent(content);
            encodingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            encodingDialog.setResultConverter(
                    btn -> (btn == ButtonType.OK) ? encodingComboBox.getValue() : null
            );

            content.setHgap(SPACING);
            content.getChildren().setAll(new Label("Encoding"), encodingComboBox);

            encodingComboBox.getItems().setAll("UTF-8", "UTF-16", "UTF-32");
            encodingComboBox.setValue("UTF-8");
        }
        return encodingDialog.showAndWait().orElse(null);
    }

    public void load() {
        fileDialog.setTitle(this.bundle.getString("UILoadingMessage"));

        File f = fileDialog.showOpenDialog(stage);
        if (f != null) {
            String encoding = getEncoding();
            if(encoding != null) {
                try {
                    String content = fileIO.load(f,encoding);

                    Platform.runLater(()-> {
                        textArea.setText(content);
                    });

                } catch (FileIOException e) {

                    new Alert(
                            Alert.AlertType.ERROR,
                            String.format("Error loading file %s for reason: %s",f.getName(), e.getMessage()),
                            ButtonType.CLOSE
                    ).showAndWait();
                }

            }
        }
    }

    public void save(String content) {
        if (content.length() > 0) {

            fileDialog.setTitle(this.bundle.getString("UISavingMessage"));
            File f = fileDialog.showSaveDialog(stage);
            if (f != null) {
                String encoding = getEncoding();

                // TODO: Save using encoding specified
                try {
                        fileIO.save(f, content, encoding);
                } catch (FileIOException e ) {
                    new Alert(
                            Alert.AlertType.ERROR,
                            String.format("%s", e.getMessage()),
                            ButtonType.CLOSE
                    ).showAndWait();
                }
            }
        }
    }

    /*private Charset getCharset(String unicodeType) {
        switch (unicodeType) {
            case "UTF-8":
                return StandardCharsets.UTF_8;
                break;
            case "UTF-16":
                return StandardCharsets.UTF_16;
                break;
            case "UTF-32":
                return Charset.
                break;
        }
    }*/
}
