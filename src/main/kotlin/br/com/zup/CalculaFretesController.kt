package br.com.zup


import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculaFretesController(@Inject val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse {
        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(
                cep=response.cep,
                frete = response.frete
            )
        }catch (e: StatusRuntimeException){
            val statusCode = e.status.code
            val description = e.status.description
            if( statusCode == Status.Code.INVALID_ARGUMENT){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }
            if( statusCode == Status.Code.PERMISSION_DENIED){
                val statusProto = io.grpc.protobuf.StatusProto.fromThrowable(e)
                if (statusProto == null){
                    throw  HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val errorDetails = statusProto.detailsList.get(0).unpack(ErrorDetails::class.java)

                throw  HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")

            }
            //erro inesperado
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message) //code+description
        }



    }
}

data class FreteResponse(val cep: String, val frete: Double) {

}