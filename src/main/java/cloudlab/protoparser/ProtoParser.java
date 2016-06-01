package cloudlab.protoparser;

import cloudlab.genericadapter.Adapter;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shreyasbr on 27-05-2016.
 */
public class ProtoParser {
    private static final Logger logger = Logger.getLogger(ProtoParser.class.getName());

    public static HashMap<String, String> parse() {
        HashMap<String, String> parsedMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("/api/main.proto")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("package")) {
                    String packageName = line.split(" ")[1].substring(0, line.split(" ")[1].length() - 1);
                    System.out.println("packageName = " + packageName);
                    parsedMap.put("packageName", packageName);
                } else if (line.startsWith("service")) {
                    String serviceName = line.split(" ")[1];
                    System.out.println("serviceName = " + serviceName);
                    parsedMap.put("serviceName", serviceName);
                }
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Proto file not found", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while parsing Proto file", e);
        }
        System.out.println("parsedMap = " + parsedMap);
        return parsedMap;
    }

    public static Class getJavaClass(String javaType) {
        if (javaType.toLowerCase().equals("string")) {
            return String.class;
        } else if (javaType.toLowerCase().equals("float")) {
            return Float.TYPE;
        } else if (javaType.toLowerCase().equals("int")) {
            return Integer.TYPE;
        } else if (javaType.toLowerCase().equals("long")) {
            return Long.TYPE;
        } else if (javaType.toLowerCase().equals("double")) {
            return Double.TYPE;
        } else if (javaType.toLowerCase().equals("boolean")) {
            return Boolean.TYPE;
        }
        System.out.println("No equivalent Java Type found for: " + javaType);
        return null;
    }
}
