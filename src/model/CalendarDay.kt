package cz.skywall.microfunspace.model

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val vacationType: VacationType?
)