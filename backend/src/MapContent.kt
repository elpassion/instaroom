import com.google.api.client.http.HttpResponseException
import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import kotlinx.html.*
import pl.elpassion.instaroom.getSomeRooms

suspend fun ApplicationCall.respondMapContent(token: String) {
    try {
        val rooms = getSomeRooms(token)
        respondHtml {
            body {
                h1 { +"MAP" }
                ul {
                    for (room in rooms) {
                        li { +"$room" }
                    }
                }
            }
        }
    }
    catch(e: HttpResponseException) {
        if (e.statusCode == 401) respondRedirect("/login")
        if (e.statusCode == 404) respondText("No rooms found for this account")
        else throw e
    }
}
