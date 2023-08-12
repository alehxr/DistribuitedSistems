package my.project;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class JmDnsServiceRegistration {
	private static JmDNS jmdns = null;

    public static void register(String service, int grpcPort) throws InterruptedException {

        try {
            // Create a JmDNS instance
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            String locGrpc = InetAddress.getLocalHost().getHostAddress() + ":" + grpcPort;

            // Register a service on default multicast DNS port 5353
            ServiceInfo serviceInfo = ServiceInfo.create(service, locGrpc, 5353, "Location of gRPC service");
            jmdns.registerService(serviceInfo);
            System.out.println("Service " + service + " registered");

            // Wait a bit
            //Thread.sleep(25000);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() throws InterruptedException, IOException {
        jmdns.unregisterAllServices();
        jmdns.close();
    }
}