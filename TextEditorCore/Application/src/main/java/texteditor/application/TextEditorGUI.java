package texteditor.application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextEditorGUI extends Application
{
    private ResourceBundle bundle = null;
    private Locale locale = null;
    private LoadSaveUI loadSaveUI = null;
    private FileIO fileIO = null;

    public static void main(String[] args)
    {
        Application.launch(args);
    }
    
//    private TextArea textArea = new TextArea();
    private TextArea textArea = new TextArea();

    @Override
    public void start(Stage stage)
    {
        var localeString = getParameters().getNamed().get("locale"); //Get the locale

        if (localeString == null) {
            locale = Locale.getDefault(); // If no locale specified
        } else {
            locale = Locale.forLanguageTag(localeString); // If user specifies a locale.
        }

        this.bundle = ResourceBundle.getBundle("bundle", locale);
        fileIO = new FileIO(this.bundle);

        loadSaveUI = new LoadSaveUI(stage, fileIO, bundle, textArea);


        stage.setTitle(this.bundle.getString("appname"));
        stage.setMinWidth(800);




        // Create toolbar
        Button btn1 = new Button("Button1");
        Button btn2 = new Button("Button2");
        Button btn3 = new Button("Button3");
        Button saveBtn = new Button(this.bundle.getString("saveBtn"));
        Button loadBtn = new Button(this.bundle.getString("loadBtn"));
        ToolBar toolBar = new ToolBar(saveBtn, loadBtn, btn1, btn2, btn3);


        // Subtle user experience tweaks
        toolBar.setFocusTraversable(false);
        toolBar.getItems().forEach(btn -> btn.setFocusTraversable(false));
        textArea.setStyle("-fx-font-family: 'monospace'"); // Set the font
        
        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(textArea);
        Scene scene = new Scene(mainBox);        
        
        // Button event handlers.
        btn1.setOnAction(event -> showDialog1());
        btn2.setOnAction(event -> showDialog2());
        btn3.setOnAction(event -> toolBar.getItems().add(new Button("ButtonN")));

        // TODO: Test save and load features
        saveBtn.setOnAction(event -> loadSaveUI.save(this.textArea.getText()));

        loadBtn.setOnAction(event -> loadSaveUI.load());


        
        // TextArea event handlers & caret positioning.
        textArea.textProperty().addListener((object, oldValue, newValue) -> 
        {
            System.out.println("caret position is " + textArea.getCaretPosition() + 
                               "; text is\n---\n" + newValue + "\n---\n");
        });
        
        textArea.setText("This is some\ndemonstration text\nTry pressing F1, ctrl+b, ctrl+shift+b or alt+b.");
        textArea.selectRange(8, 16); // Select a range of text (and move the caret to the end)
        
        // Example global keypress handler.
        scene.setOnKeyPressed(keyEvent -> 
        {
            // See the documentation for the KeyCode class to see all the available keys.
            
            KeyCode key = keyEvent.getCode();
            boolean ctrl = keyEvent.isControlDown();
            boolean shift = keyEvent.isShiftDown();
            boolean alt = keyEvent.isAltDown();
        
            if(key == KeyCode.F1)
            {
                new Alert(Alert.AlertType.INFORMATION, "F1", ButtonType.OK).showAndWait();
            }
            else if(ctrl && shift && key == KeyCode.B)
            {
                new Alert(Alert.AlertType.INFORMATION, "ctrl+shift+b", ButtonType.OK).showAndWait();
            }
            else if(ctrl && key == KeyCode.B)
            {
                new Alert(Alert.AlertType.INFORMATION, "ctrl+b", ButtonType.OK).showAndWait();
            }
            else if(alt && key == KeyCode.B)
            {
                new Alert(Alert.AlertType.INFORMATION, "alt+b", ButtonType.OK).showAndWait();
            }
        });
        
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    private void showDialog1()
    {
        // TextInputDialog is a subclass of Dialog that just presents a single text field.
    
        var dialog = new TextInputDialog();
        dialog.setTitle("Text entry dialog box");
        dialog.setHeaderText("Enter text");
        
        // 'showAndWait()' opens the dialog and waits for the user to press the 'OK' or 'Cancel' button. It returns an Optional, which is a whole other discussion, but we can call 'orElse(null)' on that to get the actual string entered if the user pressed 'OK', or null if the user pressed 'Cancel'.

        var inputStr = dialog.showAndWait().orElse(null);
        if(inputStr != null)
        {
            // Alert is another specialised dialog just for displaying a quick message.
            new Alert(
                Alert.AlertType.INFORMATION,
                "You entered '" + inputStr + "'",
                ButtonType.OK).showAndWait();
        }
    }
    
    private void showDialog2()
    {        
        Button addBtn = new Button("Add...");
        Button removeBtn = new Button("Remove...");
        ToolBar toolBar = new ToolBar(addBtn, removeBtn);
        
        addBtn.setOnAction(event -> new Alert(Alert.AlertType.INFORMATION, "Add...", ButtonType.OK).showAndWait());
        removeBtn.setOnAction(event -> new Alert(Alert.AlertType.INFORMATION, "Remove...", ButtonType.OK).showAndWait());
        
        // FYI: 'ObservableList' inherits from the ordinary List interface, but also works as a subject for any 'observer-pattern' purposes; e.g., to allow an on-screen ListView to display any changes made to the list as they are made.
        
        ObservableList<String> list = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(list);        
        list.add("Item 1");
        list.add("Item 2");
        list.add("Item 3");        
        
        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);
        
        Dialog dialog = new Dialog();
        dialog.setTitle("List of things");
        dialog.setHeaderText("Here's a list of things");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
}
