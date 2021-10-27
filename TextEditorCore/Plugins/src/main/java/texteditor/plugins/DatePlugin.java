package texteditor.plugins;

import texteditor.api.API;
import texteditor.api.Plugin;
import texteditor.api.handlers.ButtonHandler;

public class DatePlugin implements Plugin {
    @Override
    public void start(API api) {
        api.registerNewButtonToGui("Date",() -> {
            // Getting caret position
            int caretPosition = api.getGUICaretPosition();
            // Getting data and time as string
            String dateAndTime = api.getLocalDateAndTime();

            // Add the text to Text editor
            api.addTextToGUI(dateAndTime, caretPosition);
        });
    }
}
