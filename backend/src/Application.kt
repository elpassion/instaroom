package pl.elpassion.instaroom

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
import io.ktor.features.origin
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlinx.css.*
import kotlinx.html.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
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
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
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

        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }

        authenticate("google-oauth") {
            route("/login") {
                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                        ?: error("No principal")

                    val accessToken = principal.accessToken

                    val json = HttpClient(Apache).get<String>("https://www.googleapis.com/userinfo/v2/me") {
                        header("Authorization", "Bearer $accessToken")
                    }

                    println(json)

                    val stuff = calendarStuff(accessToken)

                    println(stuff)

                    call.respondText("$json\n$stuff")
                }

            }
        }
    }
}

data class MySession(val count: Int = 0)

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

private fun calendarStuff(token: String): List<String> {

    val credential = Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token)

    val calendar = Calendar.Builder(transport, jsonFactory, credential).build()

    val now = DateTime(System.currentTimeMillis())

    val events = calendar.events().list("primary")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()

    return events.items.map { "${it.summary} (${it.start.dateTime ?: it.start.date})" }
}
