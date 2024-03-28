package org.example.service;

import com.example.grpc.GRpcProtocol;
import com.example.grpc.NumberGeneratorGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NumberGeneratorImpl extends NumberGeneratorGrpc.NumberGeneratorImplBase {
    @Override
    public void generateNumbers(GRpcProtocol.NumberRequest request, StreamObserver<GRpcProtocol.NumberResponse> responseObserver) {
        log.info("Request for a new sequence of numbers, firstValue:{}, lastValue:{}",
                request.getFirstValue(),
                request.getLastValue());

        AtomicLong currentValue = new AtomicLong(request.getFirstValue());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            long value = currentValue.incrementAndGet();

            GRpcProtocol.NumberResponse response = GRpcProtocol.NumberResponse.newBuilder()
                    .setValue((int) value)
                    .build();
            responseObserver.onNext(response);

            if (value == request.getLastValue()) {
                executor.shutdown();
                responseObserver.onCompleted();
                log.info("The sequence of numbers is finished.");
            }
        };
        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }
}