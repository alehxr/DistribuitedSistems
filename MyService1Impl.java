package my.project;


import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class MyService1Impl extends MyService1Grpc.MyService1ImplBase {
    private static final Logger logger = Logger.getLogger(MyService1Impl.class.getName());
    private static final String ACCESS_CODE = "secret123"; // Código de acceso

    @Override
    public void function1Service1(MsgRequest req, StreamObserver<MsgReply> responseObserver) {
        logger.info("Calling gRPC unary type (from the server side)");

        if (req.getMessage().equals(ACCESS_CODE)) {
            MsgReply reply = MsgReply.newBuilder().setMessage(req.getMessage() + "(Unary RPC Server said: Hiya)").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } else {
            MsgReply reply = MsgReply.newBuilder().setMessage("(Server: Access denied)").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}