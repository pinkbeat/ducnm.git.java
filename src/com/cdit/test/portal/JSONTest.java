package com.cdit.test.portal;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

//import org.json.simple.parser.JSONParser;

//import net.sf.json.JSONArray;
import org.json.JSONArray;
import org.json.JSONObject;
//import net.sf.json.JSONObject;
/*
 * using JSON.simple
 */
public class JSONTest {
	public static void main(String[] asd){
		JSONTest jsonTest = new JSONTest();
		jsonTest.writeJSONContent();
		jsonTest.readJSONContent();
	}
	private void writeJSONContent(){
		JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", new Integer(100));

        JSONArray list = new JSONArray();
        list.put("msg 1");
        list.put("msg 2");
        list.put("msg 3");

        obj.put("messages", list);

        try (FileWriter file = new FileWriter("e:\\test.json")) {

            file.write(obj.toString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);

	}
	private void readJSONContent(){
		JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("e:\\test.json"));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

            String name = (String) jsonObject.get("name");
            System.out.println(name);

            long age = (Long) jsonObject.get("age");
            System.out.println(age);

            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
}
