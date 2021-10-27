package texteditor.plugins;

import texteditor.api.API;
import texteditor.api.Plugin;
import texteditor.api.handlers.ButtonHandler;

import java.util.ArrayList;

public class FindPlugin implements Plugin {



    @Override
    public void start(API api) {

        // Setting up button handler
        ButtonHandler handler = () -> {
            String userInput = api.requestStringFromUser("Search term", "Enter the search term");
            if(userInput !=null) {
                api.findStringFirstOccurrenceAndHighlight(userInput);
            }

        };

        // Request to add button to GUI.
        api.registerNewButtonToGui("Find", handler);

        var keyCode = "F3";

        api.registerKeyPressedForButton(handler, keyCode);

    }
}
