@file:Suppress("unused")

package cz.skywall.microfunspace

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.uri
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat
import java.util.*

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.FULL)
            setPrettyPrinting()
        }
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.request.uri.contains("admin")) {
            call.respondText("No way!")
            finish()
        }
        proceed()
    }

    routing {
        get("/") {
            call.respond(Text("Hello World!", Date.from(Calendar.getInstance().toInstant())))
        }
    }
}

data class Text(val text: String, val date: Date)
