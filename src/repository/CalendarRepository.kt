package cz.skywall.microfunspace.repository

import cz.skywall.microfunspace.model.CalendarDay
import cz.skywall.microfunspace.model.UserMonthCalendar
import cz.skywall.microfunspace.tools.rangeTo
import java.time.YearMonth

class CalendarRepository(
    private val userRepository: UserRepository,
    private val vacationRepository: VacationRepository
) {
    suspend fun getForMonth(yearMonth: YearMonth): List<UserMonthCalendar> {
        val startDay = yearMonth.atDay(1)
        val endDay = yearMonth.atEndOfMonth().plusDays(1)

        val result = mutableListOf<UserMonthCalendar>()

        userRepository.getAll().forEach { user ->
            val userVacations = vacationRepository.getFiltered(yearMonth, user)

            val calendarList = mutableListOf<CalendarDay>().apply {
                (startDay.rangeTo(endDay)).forEach {
                    add(CalendarDay(it, null))
                }
            }

            userVacations.forEach { vacation ->
                (vacation.dateFrom.rangeTo(vacation.dateTo)).forEach {
                    calendarList[it.dayOfMonth - 1] = CalendarDay(it, vacation.vacationType)
                }
            }
            result.add(UserMonthCalendar(user, calendarList))
        }
        return result
    }
}