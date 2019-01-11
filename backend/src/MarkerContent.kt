import io.ktor.freemarker.FreeMarkerContent
import pl.elpassion.instaroom.calendarStuff

fun createMarkerContent(token: String): FreeMarkerContent {
    val hash = configHeadCommitHash
    val stuff = calendarStuff(token)
    return FreeMarkerContent(
        "index.ftl", mapOf(
            "commit" to hash,
            "user" to token,
            "events" to stuff
        ), "e"
    )
}