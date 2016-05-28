package cloudlab.protoparser;

import java.io.*;
import java.util.HashMap;

/**
 * Created by shreyasbr on 27-05-2016.
 */
public class ProtoParser {
    public static HashMap<String, String> parse() throws IOException {
        HashMap<String, String> parsedMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "/src/main/proto/main.proto")))) {
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
        }
        System.out.println("parsedMap = " + parsedMap);
        return parsedMap;
    }

    public static Class getJavaClass(String javaType) {
        if (javaType.toLowerCase().equals("string")) {
            return String.class;
        } else if (javaType.toLowerCase().equals("float")) {
            return float.class;
        } else if (javaType.toLowerCase().equals("int")) {
            return int.class;
        } else if (javaType.toLowerCase().equals("long")) {
            return long.class;
        } else if (javaType.toLowerCase().equals("double")) {
            return double.class;
        } else if (javaType.toLowerCase().equals("boolean")) {
            return boolean.class;
        }
        System.out.println("No equivalent Java Type found for: " + javaType);
        return null;
    }
}
