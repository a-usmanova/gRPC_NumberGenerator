package org.exemple.service;

import com.example.grpc.GRpcProtocol;
import com.example.grpc.NumberGeneratorGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class NumberGeneratorService {

    @GrpcClient("grpc-number-generator")
    NumberGeneratorGrpc.NumberGeneratorStub asyncClient;
    private CountDownLatch latch = new CountDownLatch(1);

    private static final int NUMBER_SEQUENCE_LIMIT = 50;
    private static final int FIRST_VALUE = 1;
    private static final int LAST_VALUE = 30;
    private long value = 0;

    public void getNumbers() throws InterruptedException {
        log.info("Numbers Client is starting...");
        RemoteClientStreamObserver remoteClientStreamObserver = new RemoteClientStreamObserver(latch);
        GRpcProtocol.NumberRequest request = makeRequest();

        asyncClient.generateNumbers(request, remoteClientStreamObserver);

        for (int i = 0; i < NUMBER_SEQUENCE_LIMIT; i++) {
            long currentValue = getNextValue(remoteClientStreamObserver);
            log.info("currentValue: {}", currentValue);
            sleep();
        }
        log.info("The number sequence has ended");
        latch.await();

        log.info("Numbers Client is shutting down...");
    }

    private long getNextValue(RemoteClientStreamObserver remoteClientStreamObserver) {
        value = value + remoteClientStreamObserver.getCurrentValueAndReset() + 1;
        return value;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static GRpcProtocol.NumberRequest makeRequest() {
        return GRpcProtocol.NumberRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
    }
}
