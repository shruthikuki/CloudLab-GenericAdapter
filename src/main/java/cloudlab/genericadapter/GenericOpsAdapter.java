package cloudlab.genericadapter;

import cloudlab.GenericOpsProto.GenericOpsGrpc;
import cloudlab.GenericOpsProto.GenericReply;
import cloudlab.GenericOpsProto.GenericRequest;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shreyasbr on 25-05-2016.
 */
public class GenericOpsAdapter extends HttpServlet {
    private static final Logger logger = Logger.getLogger(GenericOpsAdapter.class.getName());

    private ManagedChannel channel;
    private GenericOpsGrpc.GenericOpsBlockingStub blockingStub;


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public void init() throws ServletException {
        Map<String, String> env = System.getenv();
        channel = ManagedChannelBuilder.forAddress(env.get("API_HOST"), Integer.parseInt(env.get("API_PORT"))).usePlaintext(true).build();
        blockingStub = GenericOpsGrpc.newBlockingStub(channel);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonString = req.getParameter("jsonString");

        JSONRPC2Request reqIn = null;
        try {
            reqIn = JSONRPC2Request.parse(jsonString);
        } catch (JSONRPC2ParseException e) {
            e.printStackTrace();
        }
        Map<String, Object> paramsMap = reqIn.getNamedParams();

        GenericRequest request = GenericRequest.newBuilder()
                .setCredentials(paramsMap.get("keyPair").toString())
                .setBucketName(paramsMap.get("bucketName").toString())
                .setUsername(paramsMap.get("username").toString())
                .setPublicIP(paramsMap.get("publicIP").toString())
                .setModuleName(paramsMap.get("moduleName").toString())
                .setInstallFile(paramsMap.get("installFile").toString())
                .build();
        GenericReply reply;
        try {
            reply = blockingStub.create(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Output: " + reply.getOutput());

        JSONRPC2Response respOut = new JSONRPC2Response(reply.getOutput(), reqIn.getID());
        jsonString = respOut.toString();
        PrintWriter out = resp.getWriter();
        out.println(jsonString);
    }
}
