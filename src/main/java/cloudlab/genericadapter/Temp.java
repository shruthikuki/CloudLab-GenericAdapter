package cloudlab.genericadapter;

import cloudlab.GenericOpsProto.GenericRequest;
import cloudlab.protoparser.ProtoParser;
import com.google.protobuf.Descriptors;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyasbr on 28-05-2016.
 */
public class Temp {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext(true).build();
        HashMap<String, String> parsedMap = ProtoParser.parse();
        Class cls = null;
        try {
            cls = Class.forName(parsedMap.get("packageName") + "." + parsedMap.get("serviceName") + "Grpc");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(cls.getName());
        Class[] p = new Class[1];
        p[0] = Channel.class;
        Method getStubMethod = cls.getDeclaredMethod("newBlockingStub", p);

        System.out.println("getStubMethod = " + getStubMethod);

        Object stubObject = getStubMethod.invoke(null, channel);
        System.out.println("stubObject.getClass() = " + stubObject.getClass());
        System.out.println("stubObject.getClass().getTypeName() = " + stubObject.getClass().getTypeName());
        Method methodToInvoke = null;
        Method[] methods = stubObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("create")) {
                methodToInvoke = method;
            }
        }
        System.out.println("methodToInvoke = " + methodToInvoke.getName());
        if (methodToInvoke == null) {
            System.out.println("WRONG METHOD NAME!!!");
        }


        Class r = null;
        Parameter[] parametersList = methodToInvoke.getParameters();
        for (Parameter parameter : parametersList) {
            System.out.println(parameter.getType());
            r = parameter.getType();
        }
//Object rObj = r.newInstance();
        List<Descriptors.FieldDescriptor> fd;
        Method builderMethod = null;
        try {
            builderMethod = r.getDeclaredMethod("newBuilder", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object bObj = null;
        try {
            bObj = builderMethod.invoke(null, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Method descMethod = null;
        try {
            descMethod = bObj.getClass().getDeclaredMethod("getDescriptor", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Descriptors.Descriptor dObj = null;
        try {
            dObj = (Descriptors.Descriptor) descMethod.invoke(bObj, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        GenericRequest.newBuilder().setBucketName("du");

//Descriptor d = Request.newBuilder().getDescriptor();
        String[] requestParameters = {"shreyas", "ssm-output-cloudlab", "ubuntu", "52.39.4.207", "puppetlabs-mysql", "https://github.com/shruthikuki/CloudLab-Sample-Puppet-MySQL.git"};
        fd = dObj.getFields();
        int index = 0;
        for (Descriptors.FieldDescriptor f : fd) {
            char first = Character.toUpperCase(f.getName().charAt(0));
            String setMethodName = "set" + first + f.getName().substring(1);
            System.out.println("setMethodName = " + setMethodName);
            Method setMethod = bObj.getClass().getDeclaredMethod(setMethodName, ProtoParser.getJavaClass(f.getJavaType().toString()));
            System.out.println("setMethod.getName() = " + setMethod.getName());
            System.out.println("Setting: " + requestParameters[index]);
            bObj = setMethod.invoke(bObj, requestParameters[index]);
            index++;
        }
        Method buildMethod = bObj.getClass().getDeclaredMethod("build", null);
        Object requestObject = buildMethod.invoke(bObj, null);
        System.out.println("requestObject.getClass() = " + requestObject.getClass());

        Object replyObject = methodToInvoke.invoke(stubObject, requestObject);
        System.out.println("replyObject.getClass() = " + replyObject.getClass());

        Method getOutputMethod = replyObject.getClass().getDeclaredMethod("getOutput");
        System.out.println(getOutputMethod.invoke(replyObject, null));
    }
}
