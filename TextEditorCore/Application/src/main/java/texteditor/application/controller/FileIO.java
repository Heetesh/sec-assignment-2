package texteditor.application.controller;

import java.io.*;
import java.nio.charset.*;
import java.util.ResourceBundle;

/**
 * Class responsible for file I/O operations
 * */
public class FileIO {
    private ResourceBundle bundle = null;
    public FileIO(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String load(File file, String encoding) throws FileIOException {
        // TODO : Loading mechanism
        StringBuilder fileContent = new StringBuilder();


        Charset charset =  Charset.forName(encoding);
        CharsetDecoder decoder = charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith("?");

        /*TODO: Find out why replaceWith is throwing: Exception in thread "JavaFX Application Thread"
           java.lang.IllegalArgumentException:
           Replacement too long at java.base/java.nio.charset.CharsetDecoder.replaceWith(CharsetDecoder.java:291)*/
        InputStreamReader isr;

        try {
             isr = new InputStreamReader(
                    new FileInputStream(file),
                    decoder
            );

        } catch (FileNotFoundException e) {
            throw new FileIOException(this.bundle.getString("fileSaveErrMsg"),e);
        }


        try (BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line);
            }
            return fileContent.toString();
        } catch (IOException e) {
            throw new FileIOException(this.bundle.getString("fileLoadErrMsg"), e);
        }
    }

    public void save(File file, String content, String encoding) throws FileIOException {

        Charset charset = Charset.forName(encoding);
        CharsetEncoder encoder = charset.newEncoder();

        try (PrintWriter pw = new PrintWriter(file, charset)) {
            pw.write(content);
            pw.flush();
        } catch (IOException e) {
            throw new FileIOException(this.bundle.getString("failSavingToFile"),e);
        }
    }


}
