package pierremarais.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
