package org.exemple.service;

import com.example.grpc.GRpcProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteClientStreamObserver implements io.grpc.stub.StreamObserver<GRpcProtocol.NumberResponse> {
    private long value;
    private CountDownLatch latch;

    public RemoteClientStreamObserver (CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GRpcProtocol.NumberResponse response) {
        long currentValue = response.getValue();
        log.info("Response from server value: {}", currentValue);
        setCurrentValue(currentValue);
    }

    @Override
    public void onError(Throwable e) {
        log.error("An error was received", e);
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("Request has been completed");
        latch.countDown();
    }

    private synchronized void setCurrentValue(long currentValue) {
        value = currentValue;
    }

    public synchronized long getCurrentValueAndReset() {
        long currentValue = value;
        value = 0;
        return currentValue;
    }
}
