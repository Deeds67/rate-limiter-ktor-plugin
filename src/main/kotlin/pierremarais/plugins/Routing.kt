package pierremarais.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception

fun Application.configureRouting() {
    val tokenBucket = TokenBucket()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/unlimited") {
            call.respondText("unlimited")
        }
        get("/limited") {
            tokenBucket.processRequest(call.request.origin.remoteAddress)
            call.respondText("limited")
        }
    }
}
