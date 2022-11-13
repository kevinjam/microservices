package com.thinkdevs;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    @Override
    public String getServiceId() {
        return null;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance>restult
                = new ArrayList<>();
        restult.add(new DefaultServiceInstance(
                "PAYMENT-SERVICE",
                "PAYMENT-SERVICE",
                "localhost",
                8080,
                false
        ));

        restult.add(new DefaultServiceInstance(
                "PRODUCT-SERVICE",
                "PRODUCT-SERVICE",
                "localhost",
                8080,
                false
        ));

        return Flux.just(restult);
    }
}
