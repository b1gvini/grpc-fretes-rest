package br.com.zup

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun fretesClientStub(@GrpcChannel("fretes") channel: ManagedChannel): FretesServiceGrpc.FretesServiceBlockingStub? {

//        Responsabilidade do micronaut gerenciar (inversão de dependência)
//        val channel: ManagedChannel = ManagedChannelBuilder
//            .forAddress("localhost", 50051)
//            .usePlaintext()
//            .maxRetryAttempts(10)
//            .build()

        return FretesServiceGrpc.newBlockingStub(channel)
    }


}