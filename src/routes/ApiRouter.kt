package cz.skywall.microfunspace.routes

import cz.skywall.microfunspace.R
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import java.time.YearMonth

fun Route.apiRouter() {
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