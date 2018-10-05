package pl.elpassion.instaroom

import com.fasterxml.jackson.databind.SerializationFeature
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlinx.css.*
import kotlinx.html.*
import java.io.IOException
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)

data class Event(val name: String, val startTime: String, val endTime: String)

data class Room(val name: String, val isFreeNow: Boolean, val events: List<Event>)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Authentication) {
        oauth("google-oauth") {
            client = HttpClient(Apache)
            providerLookup = { googleOauthProvider }
            urlProvider = { redirectUrl("/login") }
        }
    }

    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {

            val accessToken = call.sessions.get<MySession>()?.accessToken

            if (accessToken === null) {
                call.respondHtml {
                    body {
                        h2 { +"Unknown user" }
                        h2 { a("/login") { +"Login" } }
                    }
                }
            } else {
                val stuff = calendarStuff(accessToken)

                call.respondHtml {
                    body {
                        h2 { +"Hi user" }
                        ul {
                            for (event in stuff) {
                                li { h4 { +event } }
                            }
                        }
                        h2 { a("/login") { +"Login again" } }
                    }
                }
            }
        }

        get("/rooms") {
            val data = mapOf("rooms" to listOf(
                Room("Fake room 1", true, listOf(Event("some event", "tomorrow", "someday"))),
                Room("Fake room 2", true, listOf(Event("some event", "tomorrow", "someday")))

            ))
            call.respond(data)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        authenticate("google-oauth") {
            route("/login") {
                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                        ?: error("No principal")

                    val accessToken = principal.accessToken

                    call.sessions.set(MySession(accessToken))

                    val json = HttpClient(Apache).get<String>("https://www.googleapis.com/userinfo/v2/me") {
                        header("Authorization", "Bearer $accessToken")
                    }

                    println(json)

                    call.respondHtml {
                        body {
                            h2 { +"User details" }
                            code { +json }
                            h2 { a("/") { +"Go back to calendar" } }
                        }
                    }
                }
            }
        }
    }
}

data class MySession(val accessToken: String)

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = System.getenv("INSTAROOM_WEB_CLIENT_ID"),
    clientSecret = System.getenv("INSTAROOM_WEB_CLIENT_SECRET"),
    defaultScopes = listOf("profile", "https://www.googleapis.com/auth/calendar.events")
)

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}


private val transport = GoogleNetHttpTransport.newTrustedTransport()

private val jsonFactory = JacksonFactory.getDefaultInstance()

private val idSalkaPrzyDeveloperach = "elpassion.pl_2d3431363530383233373435@resource.calendar.google.com"
private val idSalkaPrzyRecepcji = "elpassion.pl_3336373234343038393630@resource.calendar.google.com"
private val idSalkaZielona = "elpassion.pl_36303736393039313938@resource.calendar.google.com"
private val idSalkaZolta = "elpassion.pl_2d3837303539373033363132@resource.calendar.google.com"
private val idSalkaPrzyGrafikach = "elpassion.pl_34313639343833323536@resource.calendar.google.com"

private fun calendarStuff(token: String): List<String> {

    val credential = Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token)

    val service = Calendar.Builder(transport, jsonFactory, credential)
        .setApplicationName("Instaroom")
        .build()


    val results = try {
        listOf("Salka przy developerach") + getSomeEvents(service, idSalkaPrzyDeveloperach) +
                listOf("Salka przy recepcji") + getSomeEvents(service, idSalkaPrzyRecepcji) +
                listOf("Salka zielona") + getSomeEvents(service, idSalkaZielona) +
                listOf("Salka zolta") + getSomeEvents(service, idSalkaZolta) +
                listOf("Salka przy grafikach") + getSomeEvents(service, idSalkaPrzyGrafikach)
    } catch (e: Exception) {
        listOf("Blad dostepu do salek: $e")
    }

    return results
}

private fun getSomeEvents(service: Calendar, calendarId: String) =
    service.events().list(calendarId)
        .setMaxResults(10)
        .setTimeMin(DateTime(System.currentTimeMillis()))
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()
        .items
        .map { "${it.summary} (${it.start.dateTime ?: it.start.date})" }
