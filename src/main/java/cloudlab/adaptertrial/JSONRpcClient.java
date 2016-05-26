package cloudlab.adaptertrial;
// The JSON-RPC 2.0 classes

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// We'll need the standard Map and HashMap classes too

/**
 * Created by shreyasbr on 25-05-2016.
 */
public class JSONRpcClient {
    public static void main(String[] args) throws IOException {
        // The remote method to call
        String method = "sayHello";

// The required named parameters to pass
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Kuki");
        params.put("location", "Allamndring 3");

// The mandatory request ID
        String id = "req-001";

// Create a new JSON-RPC 2.0 request
        JSONRPC2Request reqOut = new JSONRPC2Request(method, params, id);

// Serialise the request to a JSON-encoded string
        String jsonString = reqOut.toString();

// jsonString can now be dispatched to the server...
        String url = "http://52.39.53.136:8080/adapter-trial/HelloWorld";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // add header
        // post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("jsonString", jsonString));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());
        String jsonResponse = result.toString();
        // Parse response string
        JSONRPC2Response respIn = null;

        try {
            respIn = JSONRPC2Response.parse(jsonResponse);
        } catch (JSONRPC2ParseException e) {
            System.out.println(e.getMessage());
            // Handle exception...
        }


// Check for success or error
        if (respIn.indicatesSuccess()) {

            System.out.println("The request succeeded :");

            System.out.println("\tresult : " + respIn.getResult());
            System.out.println("\tid     : " + respIn.getID());
        } else {
            System.out.println("The request failed :");

            JSONRPC2Error err = respIn.getError();

            System.out.println("\terror.code    : " + err.getCode());
            System.out.println("\terror.message : " + err.getMessage());
            System.out.println("\terror.data    : " + err.getData());
        }

    }
}
