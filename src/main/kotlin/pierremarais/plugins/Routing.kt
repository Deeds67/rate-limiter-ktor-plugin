package pierremarais.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/unlimited") {
            call.respondText("unlimited")
        }
        get("/limited") {
            call.respondText("limited")
        }
    }
}
