package user;

import com.yrrhelp.grpc.User.APIResponse;
import com.yrrhelp.grpc.User.Empty;
import com.yrrhelp.grpc.User.LoginRequest;
import com.yrrhelp.grpc.userGrpc.userImplBase;

import io.grpc.stub.StreamObserver;

public class UserService extends userImplBase {

    @Override
    public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
        System.out.println("Inside login");

        String username = request.getUsername();
        String password = request.getPassword();

        APIResponse.Builder response = APIResponse.newBuilder();
        if (username.equals(password)) {
            // Return success message
            response.setResponseCode(0).setResponsemessage("SUCCESS");
        } else {
            response.setResponseCode(100).setResponsemessage("INVALID PASSWORD");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void logout(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
        // You can implement the logout logic here if needed
    }

    @Override
    public void enterHome(Empty request, StreamObserver<APIResponse> responseObserver) {
        // Implement enter home logic here
        // If statement to check if user wants to enter or ring the bell
        // Implement Client Streaming logic
    }

    @Override
    public void verifyCode(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
        // Implement code verification logic here
        // Check if the input code is correct
        // Implement Unary logic
    }

    @Override
    public StreamObserver<LoginRequest> controlCameras(StreamObserver<APIResponse> responseObserver) {
        // Implement camera control logic here
        // Use a Bidirectional Streaming pattern to control cameras
        return new StreamObserver<LoginRequest>() {
            @Override
            public void onNext(LoginRequest request) {
                // Turn on/off cameras based on request
                // Send a response back indicating success or failure
            }

            @Override
            public void onError(Throwable t) {
                // Handle any errors
            }

            @Override
            public void onCompleted() {
                // Complete the streaming
            }
        };
    }
}
