package pl.elpassion.instaroom

import com.fasterxml.jackson.databind.SerializationFeature
import createMarkerContent
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.freemarker.FreeMarker
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.ApplicationRequest
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import kotlinx.css.CSSBuilder
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FlowOrMetaDataContent
import kotlinx.html.style
import pl.elpassion.instaroom.kalendar.bookSomeRoom
import pl.elpassion.instaroom.kalendar.getSomeRooms
import respondMapContent
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

        get("/") {
            call.request.requireHttps()
            val token = call.sessions.get<MySession>()?.accessToken ?: return@get call.respondRedirect("/login")
            call.respond(createMarkerContent(token))
        }

        get("/rooms") {
            call.request.requireHttps()
            val accessToken = call.request.headers["AccessToken"]
            accessToken ?: return@get call.respond(HttpStatusCode.Unauthorized, "No access token provided")
            val rooms = getSomeRooms(accessToken)
            val data = mapOf("rooms" to rooms)
            call.respond(data)
        }

        post("/book") {
            call.request.requireHttps()
            val accessToken = call.request.headers["AccessToken"]
            val calendarId = call.request.headers["CalendarId"]
            accessToken ?: return@post call.respond(HttpStatusCode.Unauthorized, "No access token provided")
            calendarId ?: return@post call.respond(HttpStatusCode.ExpectationFailed, "No calendar id provided")
            val result = bookSomeRoom(accessToken, calendarId)
            call.respond(result)
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") { resources("static") }

        get("/map") {
            call.request.requireHttps()
            val token = call.sessions.get<MySession>()?.accessToken ?: return@get call.respondRedirect("/login")
            call.respondMapContent(token)
        }

        get("/map-rooms") {
            call.request.requireHttps()
            val token = call.sessions.get<MySession>()?.accessToken ?: return@get call.respondRedirect("/login")
            val rooms = getSomeRooms(token)
            val data = mapOf("rooms" to rooms)
            call.respond(data)
        }

        authenticate("google-oauth") {
            route("/login") { handle {
                call.request.requireHttps()
                val token = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()!!.accessToken
                call.sessions.set(MySession(token))
                call.respondRedirect("/")
            } }
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

private fun ApplicationCall.redirectUrl(path: String) = "${request.origin.scheme}://${request.hostPort}$path"

private val ApplicationRequest.hostPort get() = host()!! + port().let { port ->
    val defaultPort = if (origin.scheme == "http") 80 else 443
    if (port == defaultPort) "" else ":$port"
}

private fun ApplicationRequest.requireHttps() {
    origin.scheme == "https" || origin.host == "instaroom.elpassion.pl" || throw IllegalArgumentException("Use https!")
}

