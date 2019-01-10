package pl.elpassion.instaroom

import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime

enum class Salka(val title: String, val calendarId: String) {

    SALKA_PRZY_DEVELOPERACH("Salka przy deweloperach", idSalkaPrzyDeveloperach),
    SALKA_PRZY_RECEPCJI("Salka przy recepcji", idSalkaPrzyRecepcji),
    SALKA_ZIELONA("Salka zielona", idSalkaZielona),
    SALKA_ZOLTA("Salka zolta", idSalkaZolta),
    SALKA_PRZY_GRAFIKACH("Salka przy grafikach", idSalkaPrzyGrafikach)

}

data class Event(val name: String?, val startTime: String, val endTime: String)

data class Room(val name: String?, val calendarId: String, val isFreeNow: Boolean, val events: List<Event>)


private val idSalkaPrzyDeveloperach = "elpassion.pl_2d3431363530383233373435@resource.calendar.google.com"
private val idSalkaPrzyRecepcji = "elpassion.pl_3336373234343038393630@resource.calendar.google.com"
private val idSalkaZielona = "elpassion.pl_36303736393039313938@resource.calendar.google.com"
private val idSalkaZolta = "elpassion.pl_2d3837303539373033363132@resource.calendar.google.com"
private val idSalkaPrzyGrafikach = "elpassion.pl_34313639343833323536@resource.calendar.google.com"

private val transport = GoogleNetHttpTransport.newTrustedTransport()

private val jsonFactory = JacksonFactory.getDefaultInstance()

fun calendarStuff(token: String): List<String> {

    val service = createCalendarService(token)

    val results = try {
        listOf("Salka przy developerach") + service.getSomeEventsStrings(idSalkaPrzyDeveloperach) +
                listOf("Salka przy recepcji") + service.getSomeEventsStrings(idSalkaPrzyRecepcji) +
                listOf("Salka zielona") + service.getSomeEventsStrings(idSalkaZielona) +
                listOf("Salka zolta") + service.getSomeEventsStrings(idSalkaZolta) +
                listOf("Salka przy grafikach") + service.getSomeEventsStrings(idSalkaPrzyGrafikach)
    } catch (e: Exception) {
        listOf("Blad dostepu do salek: $e")
    }

    return results
}

fun bookSomeRoom(accessToken: String, calendarId: String): String {

    val service = createCalendarService(accessToken)

    return try {
        service.bookSomeRoom(calendarId)
    } catch (e: Exception) {
        "Blad bookowania salki $e"
    }
}

private fun Calendar.bookSomeRoom(roomCalendarId: String): String {
    val events = events()
    val newEvent = com.google.api.services.calendar.model.Event().apply {
        summary = "InstaRoom"
        attendees = listOf(EventAttendee().apply { email = roomCalendarId })
        start = EventDateTime().apply { dateTime = DateTime(System.currentTimeMillis()) }
        end = EventDateTime().apply { dateTime = DateTime(System.currentTimeMillis() + 15 * 60 * 1000) }
    }
    events.insert("primary", newEvent).execute()
    return "OK"
}

private fun createCredential(accessToken: String) =
    Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken)

private fun createCalendarService(credential: Credential) =
    Calendar.Builder(transport, jsonFactory, credential).setApplicationName("Instaroom").build()

private fun createCalendarService(accessToken: String) =
    createCalendarService(createCredential(accessToken))

fun getSomeRooms(accessToken: String) = createCalendarService(accessToken).getSomeRooms()

private fun Calendar.getSomeRooms() = listOf(
    getRoom("Salka przy deweloperach", idSalkaPrzyDeveloperach),
    getRoom("Salka przy recepcji", idSalkaPrzyRecepcji),
    getRoom("Salka zielona", idSalkaZielona),
    getRoom("Salka zolta", idSalkaZolta),
    getRoom("Salka przy grafikach", idSalkaPrzyGrafikach)
)

private fun Calendar.getRoom(name: String, calendarId: String) =
    Room(name, calendarId, true, getSomeEvents(calendarId)) // TODO: isFreeNow support

private fun Calendar.getSomeEvents(calendarId: String) =
    events().list(calendarId)
        .setMaxResults(10)
        .setTimeMin(DateTime(System.currentTimeMillis()))
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()
        .items
        .map { Event(it.summary, it.start.dateTime.toString(), it.end.dateTime.toString()) }

private fun Calendar.getSomeEventsStrings(calendarId: String) =
    getSomeEvents(calendarId).map { "${it.name} (${it.startTime} - ${it.endTime})" } + listOf("and the calendar id is: $calendarId")
