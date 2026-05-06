package com.agentdesk.common.feign.decoder;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class FeignExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Feign call failed with status " + response.status();
        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(Util.UTF_8));
                if (body != null && !body.isEmpty()) {
                    errorMessage = body;
                }
            }
        } catch (IOException e) {
            // fallback to default error message
        }
        return new RuntimeException(errorMessage);
    }

}
