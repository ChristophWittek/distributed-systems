package protocolbuffers;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(9091)
                .addService(new DatabaseService())
                .build()
                .start();
        System.out.println("Server läuft auf Port 9091");
        server.awaitTermination();
    }

    // Die Implementierung der drei rpc-Methoden aus der .proto
    static class DatabaseService extends DatabaseGrpc.DatabaseImplBase {

        private final Map<Integer, String> db = new ConcurrentHashMap<>();

        @Override
        public void addRecord(AddRecordRequest req, StreamObserver<RpcResponse> res) {
            db.put(req.getIndex(), req.getRecord());
            res.onNext(RpcResponse.newBuilder().build());
            res.onCompleted();
        }

        @Override
        public void getRecord(GetRecordRequest req, StreamObserver<RpcResponse> res) {
            RpcResponse.Builder answer = RpcResponse.newBuilder();
            String record = db.get(req.getIndex());
            if (record != null) answer.setRecord(record);
            res.onNext(answer.build());
            res.onCompleted();
        }

        @Override
        public void getSize(GetSizeRequest req, StreamObserver<RpcResponse> res) {
            res.onNext(RpcResponse.newBuilder().setSize(db.size()).build());
            res.onCompleted();
        }
    }
}