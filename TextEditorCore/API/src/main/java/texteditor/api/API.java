package texteditor.api;

import texteditor.api.handlers.ButtonHandler;
import texteditor.api.handlers.KeyPressedHandler;
import texteditor.api.handlers.TextModificationHandler;

public interface API {
//    String getDate();
//    int getCaretPosition();
//    void registerTextModification(TextModificationHandler textModificationHandler);
//    void registerFnKeyPress(KeyPressedHandler keyHandler);
//    void registerButtonHandler(String btnName, ButtonHandler buttonHandler);
//    String requestUserInput();


    String getLocalDateAndTime();

//    void registerFnKeyPress(KeyPressedHandler callback);

    void registerNewButtonToGui(String buttonName, ButtonHandler callback);

    String requestStringFromUser(String dialogueTitle, String headerText);

    void findStringFirstOccurrenceAndHighlight(String userInput);

    void registerKeyPressedForButton(ButtonHandler handler, String keyOne, String keyTwo, String three);
    void registerKeyPressedForButton(ButtonHandler handler, String keyOne, String keyTwo);
    void registerKeyPressedForButton(ButtonHandler handler, String keyOne);

    int getGUICaretPosition();

    void addTextToGUI(String dateAndTime, int caretPosition);

    void registerTextAndModifyToProvided(String monitorText, String replaceText, TextModificationHandler handler);
}
