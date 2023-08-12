package my.project;

import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;
import java.util.Random;

public class MyService2Impl extends MyService2Grpc.MyService2ImplBase {
    private static final Logger logger = Logger.getLogger(MyService2Impl.class.getName());
    private static final Random rand = new Random();
    private static final String ACCESS_CODE = "secret123"; // Código de acceso

    @Override
    public void function1Service2(MsgRequest req, StreamObserver<MsgReply> responseObserver) {
        logger.info("Calling gRPC server streaming type (from the server side)");

        if (req.getMessage().equals(ACCESS_CODE)) {
            // Si el cliente proporciona el código correcto, redirige a MyService1Impl
            MyService1Impl myService1 = new MyService1Impl();
            myService1.function1Service1(req, responseObserver);
        } else {
            // Si el cliente no proporciona el código correcto, suena el timbre
            MsgReply reply = MsgReply.newBuilder().setMessage("(Server: Ring the bell)").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}