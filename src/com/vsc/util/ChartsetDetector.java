package  com.vsc.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.poi.ddf.EscherColorRef.SysIndexSource;
 
/**
 *
 * @author Georgios Migdos
 */
public class ChartsetDetector{
 
    public Charset detectCharset(File f, String[] charsets) {
 
        Charset charset = null;
 
        for (String charsetName : charsets) {
            charset = detectCharset(f, Charset.forName(charsetName));
            System.out.println(charset);
            if (charset != null) {
//                break;
            }
        }
 
        return charset;
    }
 
    private Charset detectCharset(File f, Charset charset) {
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));
 
            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();
 
            byte[] buffer = new byte[512];
            boolean identified = false;
            int i=0;
            while ((input.read(buffer) != -1) && (!identified)) {           
                identified = identify(buffer, decoder);
            }
            input.close();
            if (identified) {
                return charset;
            } else {
                return null;
            }
 
        } catch (Exception e) {
            return null;
        }
    }
 
    private boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }
 
    public static void main(String[] args) {
        File f = new File("C:\\xmldata\\checkout\\KCB_31291_201703171458_1700004701.xml");
 
        String[] charsetsToBeTested = {"UTF-8"};
 
        ChartsetDetector cd = new ChartsetDetector();
        Charset charset = cd.detectCharset(f, charsetsToBeTested);
//        System.out.println(charset);
        if (charset != null) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
                int c = 0;
                while ((c = reader.read()) != -1) {
//                    System.out.print((char) c);
                }
                reader.close();
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
 
        }else{
            System.out.println("Unrecognized charset.");
        }
    }
}