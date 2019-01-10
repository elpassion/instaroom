package pl.elpassion.instaroom

import com.fasterxml.jackson.databind.SerializationFeature
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import freemarker.cache.ClassTemplateLoader
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
import io.ktor.freemarker.*
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlinx.css.*
import kotlinx.html.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

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

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(Application::class.java.classLoader, "templates")
    }

    val client = HttpClient(Apache) {
    }

    routing {
        get("/template") {
            val user = "user name"

            val accessToken = call.sessions.get<MySession>()?.accessToken

            val calendarStuff = accessToken?.let { calendarStuff(it) } ?: listOf("No access token")

            call.respond(FreeMarkerContent("index.ftl", mapOf("user" to user, "events" to calendarStuff), "e"))
        }

        get("/") {

            val accessToken = call.sessions.get<MySession>()?.accessToken

            println("Current access token: $accessToken")

            if (accessToken === null) {
                call.respond(FreeMarkerContent("home.ftl",mapOf("user" to ""), "e"))
            } else {
                val stuff = calendarStuff(accessToken)
                call.respond(FreeMarkerContent("index.ftl", mapOf("user" to accessToken, "events" to stuff), "e"))
            }
        }

        get("/rooms") {
            val accessToken = call.request.headers["AccessToken"]
            if (accessToken === null) {
                call.respond(HttpStatusCode.Unauthorized, "No access token provided")
            }
            else {
                val rooms = getSomeRooms(accessToken)
                val data = mapOf("rooms" to rooms)
                call.respond(data)
            }
        }

        post("/book") {
            val accessToken = call.request.headers["AccessToken"]
            val calendarId = call.request.headers["CalendarId"]
            if (accessToken === null) {
                call.respond(HttpStatusCode.Unauthorized, "No access token provided")
            }
            else if (calendarId == null) {
                call.respond(HttpStatusCode.ExpectationFailed, "No calendar id provided")
            }
            else {
                val result = bookSomeRoom(accessToken, calendarId)
                call.respond(result)
            }
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

