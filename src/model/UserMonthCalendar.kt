package cz.skywall.microfunspace.model

data class UserMonthCalendar(
    val user: User,
    val days: List<CalendarDay>
)