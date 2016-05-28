package cloudlab.protoparser;

import java.io.*;

/**
 * Created by shreyasbr on 27-05-2016.
 */
public class ProtoParser {
    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "/src/main/proto/main.proto")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("package")) {
                    String packageName = line.split(" ")[1].substring(0, line.split(" ")[1].length() - 1);
                    System.out.println("packageName = " + packageName);
                }
            }
        }
    }
}
