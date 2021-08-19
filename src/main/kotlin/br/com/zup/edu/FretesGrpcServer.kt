package br.com.zup.edu

import br.com.zup.edu.CalculaFreteRequest
import br.com.zup.edu.CalculaFreteResponse
import br.com.zup.edu.FretesServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer: FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        isValidCep(request!!.cep, responseObserver)
        isValidFormatCep(request!!.cep, responseObserver)

        logger.info("Calculando frete para request: $request")

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(Random.nextDouble(from = 0.0, until = 140.0))
            .build()

        logger.info("Frete calculado: $response ")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }

    fun isValidCep(cep: String, responseObserver: StreamObserver<CalculaFreteResponse>?): Boolean {
        if(cep == null || cep.isBlank()) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
            return false
        }
        return true
    }

    fun isValidFormatCep(cep: String, responseObserver: StreamObserver<CalculaFreteResponse>?): Boolean {
        if(!cep.matches("[0-9]{5}-[\\d]{3}".toRegex())) {
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("cep inválido")
                .augmentDescription("formato esperado 99999-999")
                .asRuntimeException())
            return false
        }
        return true
    }

}