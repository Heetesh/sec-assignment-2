package texteditor.application.view;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.python.util.PythonInterpreter;
import texteditor.KeymapParser;
import texteditor.ParseException;
import texteditor.api.API;
import texteditor.api.Plugin;
import texteditor.api.handlers.Handler;
import texteditor.api.handlers.TextModificationHandler;
import texteditor.application.ParsedContent;
import texteditor.application.controller.FileIO;
import texteditor.application.controller.LoadSaveUI;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditorGUI extends Application
{
    private ResourceBundle bundle = null;
    private Locale locale = null;
    private LoadSaveUI loadSaveUI = null;
    private FileIO fileIO = null;
    private API api = null;
    private Stage stageReference = null;
    private List<String> pluginsImplemented = new ArrayList<>();
    private List<String> scriptsImplemented = new ArrayList<>();
    private List<ParsedContent> parsedContents = new ArrayList<>();
//    private Parser parser = new Parser();
    private TextArea textArea = new TextArea();
    private ToolBar toolBar;
    private Scene scene;


    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        parseKeymaps();
        stageReference = stage;
        setupApi();
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

        Button saveBtn = new Button(this.bundle.getString("saveBtn"));
        Button loadBtn = new Button(this.bundle.getString("loadBtn"));
        Button addPluginBtn = new Button(this.bundle.getString("addPluginBtn"));
        Button addScriptBtn = new Button(this.bundle.getString("addScriptBtn"));
        toolBar = new ToolBar(saveBtn, loadBtn, addPluginBtn, addScriptBtn);

        // Subtle user experience tweaks
        toolBar.setFocusTraversable(false);
        toolBar.getItems().forEach(btn -> btn.setFocusTraversable(false));
        textArea.setStyle("-fx-font-family: 'monospace'"); // Set the font
        
        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(textArea);
        scene = new Scene(mainBox);
        
        // Button event handlers.
//        btn1.setOnAction(event -> showDialog1());
//        btn3.setOnAction(event -> toolBar.getItems().add(new Button("ButtonN")));

        // TODO: Test save and load features
        saveBtn.setOnAction(event -> loadSaveUI.save(this.textArea.getText()));

        loadBtn.setOnAction(event -> loadSaveUI.load());

        addPluginBtn.setOnAction(event -> showAddPluginDialog());

        addScriptBtn.setOnAction(event -> showAddScriptDialog());

        // TextArea event handlers & caret positioning.
        textArea.textProperty().addListener((object, oldValue, newValue) ->
        {
            System.out.println(oldValue + "this is old");
            System.out.println("caret position is " + textArea.getCaretPosition() +
                               "; text is\n---\n" + newValue + "\n---\n");

        });


        textArea.setText("This is some\ndemonstration text\nTry pressing F1, ctrl+b, ctrl+shift+b or alt+b." +
                "\u677f");
        textArea.selectRange(6, 3); // Select a range of text (and move the caret to the end)

        textArea.replaceText(4,4, " Test");
        
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

    private void parseKeymaps() /*throws IOException, ParseException */{
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("keymap"));
            KeymapParser parser = new KeymapParser(bufferedReader);
            this.parsedContents = parser.DSL();
            System.out.println(parsedContents.toString());
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }
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
                "You entered '" + inputStr + "",
                ButtonType.OK).showAndWait();
        }
    }


    private void showAddPluginDialog() {

        Button pluginAddBtn = new Button("Add plugin");
        ToolBar toolBar = new ToolBar(pluginAddBtn);

        ObservableList<String> list = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(list);

        list.addAll(this.pluginsImplemented);


        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);



        pluginAddBtn.setOnAction(event -> askForPlugin(list));

        Dialog dialog = new Dialog();
        dialog.setTitle("Plugin adder");
        dialog.setHeaderText("Currently loaded plugins below.");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();


    }

    private void askForPlugin(ObservableList<String> list) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(this.bundle.getString("select_plugin_text"));
        textInputDialog.setHeaderText(this.bundle.getString("enter_plugin_name"));
        var inputPluginName = textInputDialog.showAndWait().orElse(null);


        if (inputPluginName != null) {
            try { // Try to load plugin dynamically using java's reflection
                Class<?> className = Class.forName(inputPluginName);

                String pluginSimpleName;

                Plugin plugin = (Plugin) className.getConstructor().newInstance();
//                Constr
                pluginSimpleName = plugin.getClass().getName();

                // Check if list has already loaded plugin
                if (!this.pluginsImplemented.contains(pluginSimpleName)) {
                    plugin.start(this.api); // Start if not already present
                    this.pluginsImplemented.add(pluginSimpleName); // Add plugin class name into the list
                    list.add(pluginSimpleName);
                }
                // Getting plugin name to add to list

            } catch (ClassNotFoundException |
                    NoSuchMethodException |
                    InstantiationException |
                    IllegalAccessException |
                    InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void showAddScriptDialog() {
        Button scriptAddBtn = new Button("Add plugin");
        ToolBar toolBar = new ToolBar(scriptAddBtn);

        ObservableList<String> list = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(list);

        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);

        scriptAddBtn.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle(this.bundle.getString("script_selection_chooser"));
            File scriptFile = chooser.showOpenDialog(stageReference);

            StringBuilder scriptFileContent = new StringBuilder();

            if (scriptFile != null) {
                try (BufferedReader rdr = new BufferedReader(
                        new InputStreamReader(new FileInputStream(scriptFile)))) {
                    String line;
                    while ((line = rdr.readLine() )!= null) {
                        scriptFileContent.append(line);
                    }
                } catch (IOException e) {
                    new Alert(Alert.AlertType.INFORMATION, "" + e.getMessage(), ButtonType.OK).showAndWait();
                    return; // break out of code
                }

                PythonInterpreter interpreter = new PythonInterpreter();
                interpreter.set("api", this.api);
                interpreter.exec(String.valueOf(scriptFileContent));

                var scriptFileName = scriptFile.getName();
                if (!this.scriptsImplemented.contains(scriptFile.getName())) {
                    this.scriptsImplemented.add(scriptFileName);
                    list.add(scriptFileName);
                }
            }
        });

        Dialog dialog = new Dialog();
        dialog.setTitle("Script adder");
        dialog.setHeaderText("Currently loaded script below.");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void showDialog2()
    {
        Button addBtn = new Button("addText");
        Button removeBtn = new Button("removeText");
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

    private void setupApi() {
        this.api = new API() {
            @Override
            public String getLocalDateAndTime() {
                // TODO
                ZonedDateTime time = ZonedDateTime.now();
                DateTimeFormatter dtf = DateTimeFormatter
                        .ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(locale);

                return dtf.format(time);
            }

            @Override
            public void registerNewButtonToGui(String buttonName, Handler callback) {
//                btn3.setOnAction(event -> toolBar.getItems().add(new Button("ButtonN")));
                var button = new Button(buttonName);
                // Setting handler on button action
                button.setOnAction(event -> callback.handleEvent());

                toolBar.getItems().add(button); // Add button to main GUI Toolbar
            }

            @Override
            public String requestStringFromUser(String dialogueTitle, String headerText) {
                var dialog = new TextInputDialog();
                dialog.setTitle(dialogueTitle);
                dialog.setHeaderText(headerText);

                return dialog.showAndWait().orElse(null);
            }

            @Override
            public void findStringFirstOccurrenceAndHighlight(String userInput) {
                /*  Start from caret pos
                *   Find first text occurrence
                *   Highlight text -> selectRange fn??
                *   Index from 0 - caret = not used range
                *   Index from caret+1 = end = used range
                *   */
                // Get care position first
                int caretPos = textArea.getCaretPosition();
                // Get text from caret onwards
                String textFromCaretOnwards = textArea.getText(caretPos+1, textArea.getLength());
                Pattern word = Pattern.compile(userInput);
                Matcher match = word.matcher(textFromCaretOnwards);

                int start = 0, end = 0;
                int realStart, realEnd;

                if (match.find()) {
                    start = match.start();
                    end = match.end();
                }

                realStart = caretPos + start+1;
                realEnd = caretPos + end+1;

                textArea.selectRange(realStart, realEnd);
            }

            @Override
            public void registerKeyPressedForButton(Handler handler, String keyOne, String keyTwo, String keyThree) {
                scene.setOnKeyPressed(keyEvent -> {
                    KeyCode key = keyEvent.getCode();
                    KeyCode one = KeyCode.getKeyCode(keyOne);
                    KeyCode two = KeyCode.getKeyCode(keyTwo);
                    KeyCode three = KeyCode.getKeyCode(keyThree);

                    if (key == one && key == two && key == three) {
                        handler.handleEvent();
                    }
                });
            }

            @Override
            public void registerKeyPressedForButton(Handler handler, String keyOne, String keyTwo) {
                scene.setOnKeyPressed(keyEvent -> {
                    KeyCode key = keyEvent.getCode();
                    KeyCode one = KeyCode.getKeyCode(keyOne);
                    KeyCode two = KeyCode.getKeyCode(keyTwo);

                    if (key == one && key == two) {
                        handler.handleEvent();
                    }
                });
            }

            @Override
            public void registerKeyPressedForButton(Handler handler, String keyOne) {
                scene.setOnKeyPressed(keyEvent -> {
                    KeyCode key = keyEvent.getCode();
                    KeyCode one = KeyCode.getKeyCode(keyOne);

                    if (key == one) {
                        handler.handleEvent();
                    }
                });
            }

            @Override
            public int getGUICaretPosition() {
                return textArea.getCaretPosition();
            }

            @Override
            public void addDateAndTimeText(String dateAndTime, int caretPosition) {
                String dateTime = this.getLocalDateAndTime();
                textArea.replaceText(caretPosition, caretPosition, String.format(locale, "  %s",dateAndTime));
            }

            @Override
            public void registerTextAndModifyToProvided(String monitorText, String replaceText, TextModificationHandler handler) {
                // TODO
//                textArea.replaceText();
            }
        };
    }
}
