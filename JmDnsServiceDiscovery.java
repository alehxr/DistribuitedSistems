package my.project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class JmDnsServiceDiscovery {
	private static ServiceInfo serviceInfo = null;
	private static String locGrpc = "";
	private static JmDNS jmdns = null;

	private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
        	serviceInfo = event.getInfo();
        	locGrpc = serviceInfo.getName().split("_", 1)[0];
        	System.out.println("Service added: " + serviceInfo);
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
        	serviceInfo = event.getInfo();
        	locGrpc = serviceInfo.getName().split("_", 1)[0];
        	System.out.println("Service removed: " + serviceInfo);
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
        	serviceInfo = event.getInfo();
        	locGrpc = serviceInfo.getName().split("_", 1)[0];
        	System.out.println("Service resolved: " + serviceInfo);
        }
    }

    public static void find(String service) throws InterruptedException {
        try {
            // Create a JmDNS instance
            jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener(service, new SampleListener());
            System.out.println("Listening services");

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getLocGrpc() {
    	return locGrpc;
    }

	public ServiceInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}