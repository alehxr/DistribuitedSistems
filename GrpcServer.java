/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package my.project;

import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Random;

/**
 * Server code based on the helloWorld App
 * https://raw.githubusercontent.com/grpc/grpc-java/master/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldServer.java
 */
public class GrpcServer {
	private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
	static Random rand = new Random();
	private Server server;

	private void start() throws IOException, InterruptedException {
		/* Grpc will find a suitable port to run the services (see "0" below) */
		server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
				.addService((BindableService) new MyService1Impl())
				.addService((BindableService) new MyService2Impl())
				.addService((BindableService) new MyService3Impl())
				.build()
				.start();
		JmDnsServiceRegistration.register("_gRPCserver._tcp.local.", server.getPort());
		logger.info("Server started, listening on " + server.getPort());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its JVM shutdown hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				GrpcServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	private void stop() {
	    if (server != null) {
	        try {
	            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            logger.warning("Server shutdown interrupted: " + e.getMessage());
	        }
	    }}
	
	/**
	 * Await termination on the main thread since the grpc library uses daemon threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final GrpcServer server = new GrpcServer();
		server.start();
		server.blockUntilShutdown();
	}

	static class MyService1Impl extends MyService1Grpc.MyService1ImplBase {
	    private static final String ACCESS_CODE = "access123"; // Access code

	    @Override
	    public void function1Service1(MsgRequest req, StreamObserver<MsgReply> responseObserver) {
	        logger.info("Calling gRPC unary type (from the server side)");

	        if (req.getMessage().equals(ACCESS_CODE)) {
	            // go to MyService2Impl (Service streaming)
	            MyService2Impl myService2 = new MyService2Impl();
	            myService2.function1Service2(req, responseObserver);
	        } else {
	            // Ring the bell
	            String message = "Ring the bell!";
	            MsgReply reply = MsgReply.newBuilder().setMessage(message).build();
	            responseObserver.onNext(reply);
	            responseObserver.onCompleted();
	        }
	    }
	}

	
	static class MyService2Impl extends MyService2Grpc.MyService2ImplBase {
	    public void function1Service2(MsgRequest req, StreamObserver<MsgReply> responseObserver) {
	        logger.info("Calling gRPC server streaming type (from the server side)");

	        // Verificar si la entrada tiene 6 números
	        String input = req.getMessage();
	        if (input.matches("\\d{6}")) {
	            String message = "Door opened!";
	            MsgReply reply = MsgReply.newBuilder().setMessage(message).build();
	            responseObserver.onNext(reply);
	        } else {
	            String message = "Access denied. Enter 6 numbers to open the door.";
	            MsgReply reply = MsgReply.newBuilder().setMessage(message).build();
	            responseObserver.onNext(reply);
	        }

	        responseObserver.onCompleted();
	    }
	}

		/*
		 * Client streaming RPCs where the client writes a sequence of messages and sends them to the server,
		 * again using a provided stream
		 * https://grpc.io/docs/what-is-grpc/core-concepts/
		 */
	
static class MyService3Impl extends MyService3Grpc.MyService3ImplBase {
    @Override
    public StreamObserver<MsgRequest> function1Service3(StreamObserver<MsgReply> responseObserver) {
        logger.info("Calling gRPC bi-directional streaming type (from the server side)");

        return new StreamObserver<MsgRequest>() {
            @Override
            public void onNext(MsgRequest value) {
                if (value.getMessage().equalsIgnoreCase("turn_on_cameras")) {
                    // Encender las cámaras una por una
                    for (int i = 1; i <= 3; i++) {
                        String cameraMessage = "Camera " + i + " is ON";
                        MsgReply reply = MsgReply.newBuilder().setMessage(cameraMessage).build();
                        responseObserver.onNext(reply);
                        try {
                            Thread.sleep(1000); // Simular el tiempo que lleva encender una cámara
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (value.getMessage().equalsIgnoreCase("turn_off_cameras")) {
                    // Apagar las cámaras
                    MsgReply reply = MsgReply.newBuilder().setMessage("Cameras are OFF").build();
                    responseObserver.onNext(reply);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
}
