package cz.skywall.microfunspace

import cz.skywall.microfunspace.repository.CalendarRepository
import cz.skywall.microfunspace.repository.UserRepository
import cz.skywall.microfunspace.repository.VacationRepository

object R {
    val userRepository = UserRepository()

    val vacationRepository = VacationRepository(userRepository)

    val calendarRepository = CalendarRepository(userRepository, vacationRepository)
}