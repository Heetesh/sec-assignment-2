package texteditor.application;

public class ParsedContent {
    private String comboString;
    private String insertOrDelete;
    private String quoteContents;
    private String caretOrStart;

    public ParsedContent setComboString(String comboString) {
        this.comboString = comboString;
        return this;
    }

    public ParsedContent setInsertOrDelete(String insertOrDelete) {
        this.insertOrDelete = insertOrDelete;
        return this;
    }

    public ParsedContent setQuoteContents(String quoteContents) {
        this.quoteContents = quoteContents;
        return this;
    }

    public ParsedContent setCaretOrStart(String caretOrStart) {
        this.caretOrStart = caretOrStart;
        return this;
    }

    public String getComboString() {
        return comboString;
    }

    public String getInsertOrDelete() {
        return insertOrDelete;
    }

    public String getQuoteContents() {
        return quoteContents;
    }

    public String getCaretOrStart() {
        return caretOrStart;
    }
}
