package texteditor.api;

import texteditor.api.handlers.ButtonHandler;
import texteditor.api.handlers.Handler;
import texteditor.api.handlers.KeyPressedHandler;
import texteditor.api.handlers.TextModificationHandler;

public interface API {
//    String getDate();
//    int getCaretPosition();
//    void registerTextModification(TextModificationHandler textModificationHandler);
//    void registerFnKeyPress(KeyPressedHandler keyHandler);
//    void registerButtonHandler(String btnName, ButtonHandler buttonHandler);
//    String requestUserInput();

    void replaceSmileyToSmileyEmoji(Handler handler);

    String getLocalDateAndTime();

//    void registerFnKeyPress(KeyPressedHandler callback);

    void registerNewButtonToGui(String buttonName, Handler callback);

    String requestStringFromUser(String dialogueTitle, String headerText);

    void findStringFirstOccurrenceAndHighlight(String userInput);

    void registerKeyPressedForButton(Handler handler, String keyOne, String keyTwo, String three);
    void registerKeyPressedForButton(Handler handler, String keyOne, String keyTwo);
    void registerKeyPressedForButton(Handler handler, String keyOne);

    int getGUICaretPosition();

    void addDateAndTimeText(String dateAndTime, int caretPosition);

    void registerTextAndModifyToProvided(String monitorText, String replaceText, TextModificationHandler handler);


}
