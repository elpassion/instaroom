package pl.elpassion.instaroom.api

data class RoomsResponse(val rooms: List<Room>)

data class Room(
    val name: String = "",
    val calendarId: String = "",
    val events: List<Event> = emptyList()
)

data class Event(
    val name: String
)