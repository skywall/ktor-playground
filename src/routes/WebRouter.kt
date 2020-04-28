package cz.skywall.microfunspace.routes

import cz.skywall.microfunspace.R
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.html.respondHtml
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.getDigestFunction
import kotlinx.html.*
import sun.plugin2.util.SystemUtil.decodeBase64
import java.time.YearMonth

object Constant {
    const val FORM_AUTH = "FORM"
    const val SESSION_AUTH = "SESSION"
    const val SESSION_NAME = "SESSION_NAME"
}

data class FunSession(val user: String) : Principal

fun Route.webRouter() {
    val userTable = UserHashedTableAuth(
        getDigestFunction("SHA-256", salt = { "ktor" }), mapOf(
            "test" to decodeBase64("VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=") // sha256 for "test"
        )
    )

    application.install(Sessions) {
        cookie<FunSession>(
            name = Constant.SESSION_NAME,
            storage = SessionStorageMemory()
        ) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }

    application.install(Authentication) {
        form(Constant.FORM_AUTH) {
            userParamName = "name"
            passwordParamName = "password"
            validate { credentials ->
                userTable.authenticate(UserPasswordCredential(credentials.name, credentials.password))
            }
            challenge {
                call.respondRedirect("/login?failed=incorrect_login")
            }
        }
        session<FunSession>(Constant.SESSION_AUTH) {
            challenge {
                call.respondRedirect("login?failed=invalid_session")
            }
            validate { session: FunSession ->
                session
            }
        }
    }

    route("/login") {
        authenticate(Constant.FORM_AUTH) {
            post {
                val principal = call.authentication.principal<UserIdPrincipal>()
                call.sessions.set(Constant.SESSION_NAME, FunSession(principal!!.name))
                call.respondRedirect("/dashboard")
            }
        }

        get {
            call.respondHtml {
                layout {
                    div("mdl-card mdl-shadow--2dp") {
                        form(action = "login", method = FormMethod.post) {
                            div("mdl-textfield mdl-js-textfield mdl-textfield--floating-label") {
                                input(
                                    classes = "mdl-textfield__input",
                                    type = InputType.text,
                                    name = "name"
                                ) {
                                    id = "name"
                                }
                                label(classes = "mdl-textfield__label") {
                                    attributes["for"] = "name"
                                    +"Username"
                                }
                            }
                            div("mdl-textfield mdl-js-textfield mdl-textfield--floating-label") {
                                input(
                                    type = InputType.password,
                                    name = "password",
                                    classes = "mdl-textfield__input"
                                ) {
                                    id = "password"
                                }
                                label(classes = "mdl-textfield__label") {
                                    attributes["for"] = "password"
                                    +"Password"
                                }
                            }
                            button(classes = "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent") {
                                +"Login"
                            }
                            if (call.request.queryParameters.contains("failed")) {
                                p(classes = "login_error") {
                                    +"Login failed: ${call.request.queryParameters["failed"]}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    authenticate(Constant.SESSION_AUTH) {
        get("/dashboard") {
            val session: FunSession = call.sessions.get(Constant.SESSION_NAME) as? FunSession ?: FunSession("test")

            val year = call.request.queryParameters["year"]?.toInt() ?: YearMonth.now().year
            val month = call.request.queryParameters["month"]?.toInt() ?: YearMonth.now().monthValue
            val yearMonth = YearMonth.of(year, month)
            val currentMonthDays = yearMonth.atEndOfMonth().dayOfMonth

            val vacations = R.calendarRepository.getForMonth(yearMonth)

            call.respondHtml {
                layout(titleSuffix = ": ${session.user}") {
                    table(classes = "mdl-data-table mdl-js-data-table") {
                        thead {
                            tr {
                                th {
                                    +"Jméno"
                                }
                                for (i in (1..currentMonthDays)) {
                                    th {
                                        +i.toString()
                                    }
                                }
                            }
                        }
                        tbody {
                            for (userVacation in vacations) {
                                tr {
                                    td(classes = "user_cell") {
                                        id = userVacation.user.uuid
                                        +userVacation.user.name
                                        div(classes = "mdl-tooltip") {
                                            attributes["for"] = userVacation.user.uuid
                                            +"Celkem dovolené: ${userVacation.user.count}"
                                        }
                                    }
                                    for (day in userVacation.days) {
                                        val classes = if (day.vacationType != null) "vacation_cell" else ""
                                        td(classes = classes) {
                                            +day.date.dayOfMonth.toString()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static("/static") {
        resource("styles.css")
    }
}

private fun HTML.layout(titleSuffix: String = "", content: MAIN.() -> Unit) {
    head {
        script(src = "https://code.getmdl.io/1.3.0/material.min.js") {}
        styleLink("https://code.getmdl.io/1.3.0/material.indigo-pink.min.css")
        styleLink("https://fonts.googleapis.com/icon?family=Material+Icons")
        styleLink("/static/styles.css")
    }
    body {
        div("mdl-layout mdl-js-layout mdl-layout--fixed-header") {
            header("mdl-layout__header") {
                div("mdl-layout__header-row") {
                    span("mdl-layout-title") {
                        +"microFunspace$titleSuffix"
                    }
                }
            }
            main(classes = "mdl-layout__content center") {
                content()
            }
        }
    }
}
