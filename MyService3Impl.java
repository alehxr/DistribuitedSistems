package my.project;

import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class MyService3Impl extends MyService3Grpc.MyService3ImplBase {
    private static final Logger logger = Logger.getLogger(MyService3Impl.class.getName());
    private static final String ACCESS_CODE = "secret123"; // Código de acceso

    @Override
    public StreamObserver<MsgRequest> function1Service3(StreamObserver<MsgReply> responseObserver) {
        logger.info("Calling gRPC bi-directional streaming type (from the server side)");
        return new StreamObserver<MsgRequest>() {
            @Override
            public void onNext(MsgRequest value) {
                if (value.getMessage().equals(ACCESS_CODE)) {
                    System.out.println("(Bi-di Server Received: " + value.getMessage() + ")");
                    for (int i = 1; i <= 3; i++) {
                        String cameraMessage = "Camera " + i + " is ON";
                        MsgReply reply = MsgReply.newBuilder().setMessage("(Bi-di Server said: " + cameraMessage + ")").build();
                        responseObserver.onNext(reply);
                    }
                } else {
                    System.out.println("(Bi-di Server Received: Access denied)");
                    MsgReply reply = MsgReply.newBuilder().setMessage("(Bi-di Server said: Access denied)").build();
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