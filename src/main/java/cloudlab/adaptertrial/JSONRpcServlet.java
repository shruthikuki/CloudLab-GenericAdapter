package cloudlab.adaptertrial;

// The JSON-RPC 2.0 classes

import com.thetransactioncompany.jsonrpc2.*;

// We'll need the standard Map and HashMap classes too
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by shreyasbr on 25-05-2016.
 */
public class JSONRpcServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonString = req.getParameter("jsonString");
        // Parse request string
        JSONRPC2Request reqIn = null;

        try {
            reqIn = JSONRPC2Request.parse(jsonString);

        } catch (JSONRPC2ParseException e) {
            System.out.println(e.getMessage());
            // Handle exception...
        }

// How to extract the request data
        System.out.println("Parsed request with properties :");
        System.out.println("\tmethod     : " + reqIn.getMethod());
        System.out.println("\tparameters : " + reqIn.getNamedParams());
        System.out.println("\tid         : " + reqIn.getID() + "\n\n");

// Process request...
        Map<String, Object> paramsMap = reqIn.getNamedParams();

        String result = "Hello " + paramsMap.get("name") + ". You stay in " + paramsMap.get("location");

// Create the response (note that the JSON-RPC ID must be echoed back)

        JSONRPC2Response respOut = new JSONRPC2Response(result, reqIn.getID());

// Serialise response to JSON-encoded string
        jsonString = respOut.toString();
        PrintWriter out = resp.getWriter();
        out.println(jsonString);
    }
}
