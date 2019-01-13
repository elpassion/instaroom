import com.google.api.client.http.HttpResponseException
import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import kotlinx.html.*
import pl.elpassion.instaroom.kalendar.getSomeRooms

suspend fun ApplicationCall.respondMapContent(token: String) {
    try {
        val rooms = getSomeRooms(token)
        respondHtml {
            head {
                title("Instaroom Map")
                jsscript("static/jquery-1.10.2.js", "static/noh.js", "static/map-content.js")
            }
            body {
                h1 { +"MAP" }
                ul {
                    for (room in rooms) {
                        li { +"$room" }
                    }
                }
                div { id = "map_content" }
            }
        }
    }
    catch(e: HttpResponseException) {
        if (e.statusCode == 401) respondRedirect("/login")
        if (e.statusCode == 404) respondText("No rooms found for this account")
        else throw e
    }
}

private fun HEAD.jsscript(vararg srcs: String) = srcs.forEach { script("text/javascript", it) {} }
