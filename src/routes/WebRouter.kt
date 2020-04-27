package cz.skywall.microfunspace.routes

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserHashedTableAuth
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.basic
import io.ktor.html.respondHtml
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.getDigestFunction
import kotlinx.html.*
import sun.plugin2.util.SystemUtil.decodeBase64

fun Route.webRouter() {
    val userTable = UserHashedTableAuth(
        getDigestFunction("SHA-256", salt = { "ktor" }), mapOf(
            "test" to decodeBase64("VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=") // sha256 for "test"
        )
    )

    application.install(Authentication) {
        basic("BASIC") {
            realm = "Ktor realm"
            validate { credentials ->
                userTable.authenticate(UserPasswordCredential(credentials.name, credentials.password))
            }
        }
    }

    route("/login") {
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

private fun HTML.layout(content: MAIN.() -> Unit) {
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
                        +"microFunspace"
                    }
                }
            }
            main(classes = "mdl-layout__content center") {
                content()
            }
        }
    }
}
