@file:Suppress("unused")

package cz.skywall.microfunspace

import cz.skywall.microfunspace.model.adapter.LocalDateAdapter
import cz.skywall.microfunspace.routes.apiRouter
import cz.skywall.microfunspace.routes.webRouter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat
import java.time.LocalDate

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

@kotlin.jvm.JvmOverloads
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            setDateFormat(DateFormat.FULL)
            setPrettyPrinting()
            serializeNulls()
        }
    }

    routing {
        apiRouter()
        webRouter()
    }
}
