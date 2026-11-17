package protocolbuffers;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Map;

public class DatabaseClient {

    public static void main(String[] args) {
        // Verbindung zum Server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .build();
        DatabaseGrpc.DatabaseBlockingStub stub = DatabaseGrpc.newBlockingStub(channel);

        // Records schreiben
        Map<Integer, String> cities = Map.of(4101, "Appen", 4102, "Ahrensburg", 4103, "Wedel",
                4104, "Aumühle", 4105, "Seevetal", 4106, "Quickborn");
        for (var e : cities.entrySet()) {
            stub.addRecord(AddRecordRequest.newBuilder().setIndex(e.getKey()).setRecord(e.getValue()).build());
        }

        // Records lesen
        for (int index : new int[]{4103, 4107}) {
            RpcResponse r = stub.getRecord(GetRecordRequest.newBuilder().setIndex(index).build());
            System.out.println(index + " → " + (r.hasRecord() ? r.getRecord() : "nicht gefunden"));
        }

        // Größe
        System.out.println("Größe: " + stub.getSize(GetSizeRequest.newBuilder().build()).getSize());

        channel.shutdown();
    }
}