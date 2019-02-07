package pl.elpassion.instaroom.kalendar

import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import java.util.concurrent.atomic.AtomicInteger

enum class Salka(
    val title: String,
    val calendarId: String,
    val titleColor: String,
    val borderColor: String,
    val backgroundColor: String,
    val code: String
) {
    SALKA_PRZY_DEVELOPERACH(
        title = "Salka przy deweloperach",
        calendarId = "elpassion.pl_2d3431363530383233373435@resource.calendar.google.com",
        titleColor = "#FFD73F4C",
        borderColor = "#FFFFD2D6",
        backgroundColor = "#FFE9EB",
        code = "SPD"
    ),
    SALKA_PRZY_RECEPCJI(
        title = "Salka przy recepcji",
        calendarId = "elpassion.pl_3336373234343038393630@resource.calendar.google.com",
        titleColor = "#FF451A05",
        borderColor = "#FFCEBDB6",
        backgroundColor = "#FFE6DEDA",
        code = "SPR"
    ),
    SALKA_ZIELONA(
        title = "Salka zielona",
        calendarId = "elpassion.pl_36303736393039313938@resource.calendar.google.com",
        titleColor = "#FF25B963",
        borderColor = "#FFB1F3DC",
        backgroundColor = "#FFE9F9F0",
        code = "SZI"
    ),
    SALKA_ZOLTA(
        title = "Salka żółta",
        calendarId = "elpassion.pl_2d3837303539373033363132@resource.calendar.google.com",
        titleColor = "#FFC3720A",
        borderColor = "#FFFFE2BE",
        backgroundColor = "#FFFFF1DF",
        code = "SZO"
    ),
    SALKA_PRZY_GRAFIKACH(
        title = "Salka przy grafikach",
        calendarId = "elpassion.pl_34313639343833323536@resource.calendar.google.com",
        titleColor = "#FF5D34D2",
        borderColor = "#FFD7C9FF",
        backgroundColor = "#FFEBE4FF",
        code = "SPG"
    )
}

data class Event(
    val id: String,
    val htmlLink: String?,
    val name: String?,
    val startTime: String,
    val endTime: String,
    val isOwnBooked: Boolean
)

data class Room(
    val name: String?,
    val calendarId: String,
    val events: List<Event>,
    val titleColor: String,
    val borderColor: String,
    val backgroundColor: String,
    val code: String
)

data class BookingEvent(
    val calendarId: String,
    val title: String,
    val userEmail: String,
    val startDate: DateTime,
    val endDate: DateTime
)

private val transport = NetHttpTransport()

private val jsonFactory = JacksonFactory.getDefaultInstance()

fun calendarStuff(token: String, userEmail: String): List<String> {
    val service = createCalendarService(token)
    return try {
        Salka.values().flatMap{service.getSomeEventsStrings(it, userEmail)}
    } catch (e: HttpResponseException) {
        listOf(e.message.orEmpty())
    }
}

fun bookSomeRoom(accessToken: String, bookingEvent: BookingEvent): Event? =
    createCalendarService(accessToken).bookRoomWithEvent(bookingEvent)

private fun Calendar.bookRoomWithEvent(bookingEvent: BookingEvent): Event? {
    val events = events()
    val newEvent = com.google.api.services.calendar.model.Event().apply {
        summary = bookingEvent.title
        attendees = listOf(EventAttendee().apply { email = bookingEvent.calendarId })
        start = EventDateTime().apply { dateTime = bookingEvent.startDate }
        end = EventDateTime().apply { dateTime = bookingEvent.endDate }
    }
    val ev = events.insert("primary", newEvent).execute()
    return if (ev != null)
        Event(
            ev.id,
            ev.htmlLink,
            ev.summary,
            ev.start.dateTime.toString(),
            ev.end.dateTime.toString(),
            true
        ) else null
}

fun bookSomeRoom(accessToken: String, calendarId: String): Event? =
    createCalendarService(accessToken).bookSomeRoom(calendarId)

private fun Calendar.bookSomeRoom(roomCalendarId: String): Event? {
    val events = events()
    val now = System.currentTimeMillis()
    val newEvent = com.google.api.services.calendar.model.Event().apply {
        summary = createEventSummary(roomCalendarId)
        attendees = listOf(EventAttendee().apply { email = roomCalendarId })
        start = EventDateTime().apply { dateTime = DateTime(now) }
        end = EventDateTime().apply { dateTime = DateTime(now + 15 * 60 * 1000) }
    }
    val ev = events.insert("primary", newEvent).execute()
    return if (ev != null)
        Event(
            ev.id,
            ev.htmlLink,
            ev.summary,
            ev.start.dateTime.toString(),
            ev.end.dateTime.toString(),
            true
        ) else null

}

private val counter = AtomicInteger(0)

private fun createEventSummary(roomCalendarId: String) =
    Salka.values().find { it.calendarId == roomCalendarId }?.code?.let { "InstaRoom $it " + counter.incrementAndGet() }

private fun createCredential(accessToken: String) =
    Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken)

private fun createCalendarService(credential: Credential) =
    Calendar.Builder(transport, jsonFactory, credential).setApplicationName("Instaroom").build()

private fun createCalendarService(accessToken: String) =
    createCalendarService(createCredential(accessToken))

fun getSomeRooms(accessToken: String, userEmail: String) = createCalendarService(accessToken).getSomeRooms(userEmail)

private fun Calendar.getSomeRooms(userEmail: String) = Salka.values().map{getRoom(it, userEmail)}

private fun Calendar.getRoom(salka: Salka, userEmail: String) = Room(
    name = salka.title,
    calendarId = salka.calendarId,
    events = getSomeEvents(salka.calendarId, userEmail),
    titleColor = salka.titleColor,
    borderColor = salka.borderColor,
    backgroundColor = salka.backgroundColor,
    code = salka.code
)

private fun Calendar.getSomeEvents(calendarId: String, userEmail: String) =
    events().list(calendarId)
        .setMaxResults(5)
        .setTimeMin(DateTime(System.currentTimeMillis()))
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()
        .items
        .map { calendarEventToEvent(it, userEmail) }

private fun calendarEventToEvent(event: com.google.api.services.calendar.model.Event, userEmail: String): Event =
    Event(event.id, event.htmlLink, event.summary, event.start.dateTime.toString(), event.end.dateTime.toString(), event.organizer.email==userEmail)


private fun Calendar.getSomeEventsStrings(salka: Salka, userEmail: String) = listOf(salka.title) + getSomeEventsStrings(salka.calendarId, userEmail)

private fun Calendar.getSomeEventsStrings(calendarId: String, userEmail: String) =
    getSomeEvents(calendarId, userEmail).map { "${it.name} (${it.startTime} - ${it.endTime})" } + listOf("and the calendar id is: $calendarId")
