package my.project;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import my.project.MyService2Grpc.MyService2Stub;	// async stub for client-streaming services
import my.project.MyService3Grpc.MyService3Stub;// async stub for client-streaming services

import my.project.MsgRequest;
import my.project.MsgReply;
	
/**
 * Client code based on the helloWorld App
 * https://raw.githubusercontent.com/grpc/grpc-java/master/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldClient.java
 */
public class GrpcClient {
	  private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
	  private final MyService1Grpc.MyService1BlockingStub blockingStubMyService1;
	  private final MyService2Grpc.MyService2BlockingStub blockingStubMyService2;
	  private final MyService2Stub asyncService2Stub;
	  private final MyService3Stub asyncService3Stub;
	  static Random rand = new Random();

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  public GrpcClient(Channel channel) {
	    // The sync calls (blocking)
	    blockingStubMyService1 = MyService1Grpc.newBlockingStub(channel);
	    blockingStubMyService2 = MyService2Grpc.newBlockingStub(channel);
	    //MyService3Grpc.newBlockingStub(channel);

	    asyncService2Stub = MyService2Grpc.newStub(channel);	// async calls (for client-streaming)
	    asyncService3Stub = MyService3Grpc.newStub(channel);	// async calls (for bidirectional streaming)
	  }

	  // Run function1Service1 from Service1 (Unary RPC)
	  public void clientSideFunction1Service1() {
		  logger.info("Calling gRPC unary type (from the client side)");

		  try {
			  MsgRequest request = MsgRequest.newBuilder().setMessage("access123").build();
	            MsgReply reply = blockingStubMyService1
	                    .withDeadlineAfter(1, TimeUnit.SECONDS)
	                    .function1Service1(request);
	            System.out.println("Server Response: " + reply.getMessage());
	        } catch (StatusRuntimeException e) {
	            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
	        }
	    }
	  // Run function1Service2 from Service2 (Server streaming RPC)
	  public void clientSideFunction1Service2() {
		  logger.info("Calling gRPC server streaming type (from the client side)");

		  try {
			  MsgRequest request = MsgRequest.newBuilder().setMessage("123456").build();
			  Iterator<MsgReply> reply = blockingStubMyService2
					  .withDeadlineAfter(1, TimeUnit.SECONDS)
					  .function1Service2(request);
				while(reply.hasNext()) {
					System.out.println(reply.next());		// print all messages from the server				
				}
			  logger.info("End of server streaming");
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			  return;
		  }
	  }

	  // Run function2Service2 from Service2 (Client streaming RPC)
	  public void clientSideFunction2Service2() {
		  logger.info("Calling gRPC client streaming type (from the client side)");

		  StreamObserver<MsgReply> responseObserver = new StreamObserver<MsgReply>() {
			  @Override
			  public void onNext(MsgReply value) {
				  System.out.println("Server Response: " + value.getMessage());					
			  }

			  @Override
			  public void onError(Throwable t) {
				  t.printStackTrace();
			  }

			  @Override
			  public void onCompleted() {
				  System.out.println("Client Streaming Completed: ");
			  }
		  };

		  // send a stream (aka: bunch of messages) back to the server
		  StreamObserver<MsgRequest> requestObserver = asyncService2Stub.function2Service2(responseObserver);
		  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Client said: How're you keeping?)").build());
		  for (int i=0; i<rand.nextInt(1, 10); i++){
			  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Client said: blah, blah, blah)").build());
		  }

		  requestObserver.onCompleted();
	  }

	  // Run function1Service3 from Service3 (Bi-directional streaming RPC)
	  public void clientSideFunction1Service3() {
		  logger.info("Calling gRPC bi-directional streaming type (from the client side)");
		  StreamObserver<MsgRequest> requestObserver =
				  asyncService3Stub.function1Service3(new StreamObserver<MsgReply>() {
					@Override
					public void onNext(MsgReply value) {
						System.out.println("Server Response: " + value.getMessage());
					}

					@Override
					public void onError(Throwable t) {
						t.printStackTrace();
					}

					@Override
					public void onCompleted() {
						System.out.println("Bi-di Client: Stream completed");
					}
				  });
		  
		  requestObserver.onNext(MsgRequest.newBuilder().setMessage("turn_on_cameras").build());
		  for (int i=0; i<rand.nextInt(1, 10); i++){
			  requestObserver.onNext(MsgRequest.newBuilder().setMessage("Hello").build());
		  }
		  requestObserver.onNext(MsgRequest.newBuilder().setMessage("turn_off_cameras").build());
		  requestObserver.onCompleted();
	  }

	  /**
	   * 
	   */
	  public static void main(String[] args) throws Exception {
		  //String target;
		  String target = getServiceTarget();
	        if (target == null) {
	            System.err.println("Service target not found.");
	            return;
	        }


		  // Service discovery part (Where's the gRPC server running?) 
		  JmDnsServiceDiscovery jmDnsServiceDiscovery = new JmDnsServiceDiscovery();
		  JmDnsServiceDiscovery.find("_gRPCserver._tcp.local.");	// service name
		  do {
			  target = jmDnsServiceDiscovery.getLocGrpc();
			  System.out.println("jmDnsServiceDiscovery: " + target);
		  } while (target.length()<2);		// wait for the service to be discovered

		  ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
				  .build();
		  try {
			  GrpcClient client = new GrpcClient(channel);
			  client.clientSideFunction1Service1();			// unary type
			  client.clientSideFunction1Service2();			// server-streaming type
			  client.clientSideFunction2Service2();			// client-streaming type
			  client.clientSideFunction1Service3();			// bi-directional streaming type
		  } finally {
			  channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		  }
	  }

	    private static String getServiceTarget() {
	        JmDnsServiceDiscovery jmDnsServiceDiscovery = new JmDnsServiceDiscovery();
	        try {
				jmDnsServiceDiscovery.find("_gRPCserver._tcp.local.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String target = jmDnsServiceDiscovery.getLocGrpc();
	        while (target == null || target.length() < 2) {
	            target = jmDnsServiceDiscovery.getLocGrpc();
	        }
	        return target;
	    }
}

