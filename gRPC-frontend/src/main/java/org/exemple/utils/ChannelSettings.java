package org.exemple.utils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "grpc-server")
public class ChannelSettings {

    @NonNull
    private String host;
    private int port;

}
