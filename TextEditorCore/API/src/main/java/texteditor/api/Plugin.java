package texteditor.api;

import texteditor.api.API;

@FunctionalInterface
public interface Plugin {
    void start(API api);
}
