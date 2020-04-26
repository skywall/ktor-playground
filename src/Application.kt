@file:Suppress("unused")

package cz.skywall.microfunspace

import cz.skywall.microfunspace.model.adapter.LocalDateAdapter
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.forEachPart
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.lang.Exception
import java.text.DateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.NoSuchElementException

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

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
//    install(StatusPages) {
//        exception<NoSuchElementException> {
//            // exception swallowed
//            call.respond(HttpStatusCode.NotFound)
//        }
//    }

    routing {
        route("/api") {
            get("/calendar") {
                val parameters = call.request.queryParameters
                if (parameters.contains("year") && parameters.contains("month")) {
                    val year = parameters["year"]?.toInt()!! // check if INT
                    val month = parameters["month"]?.toInt()!!

                    call.respond(R.calendarRepository.getForMonth(YearMonth.of(year, month)))
                } else {
                    call.respond(R.calendarRepository.getForMonth(YearMonth.now()))
                }
            }

            get("/user/{name}") {
                val name: String? = call.parameters["name"]
                call.respond(R.userRepository.getByName(name!!))
            }
        }
    }
}
