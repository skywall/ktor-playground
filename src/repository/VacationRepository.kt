package cz.skywall.microfunspace.repository

import cz.skywall.microfunspace.model.User
import cz.skywall.microfunspace.model.Vacation
import cz.skywall.microfunspace.model.VacationType
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class VacationRepository(userRepository: UserRepository) {

    private val vacations = listOf(
        Vacation(
            "e9ae4597-8ce8-4a7b-a587-25b5afee668b",
            VacationType.DOCTOR,
            userRepository.getByName("bob"),
            LocalDate.of(2020, Month.APRIL, 1),
            LocalDate.of(2020, Month.APRIL, 1)
        ),
        Vacation(
            "209e3589-92cf-4ebb-8dd1-90b992942600",
            VacationType.HOME_OFFICE,
            userRepository.getByName("bob"),
            LocalDate.of(2020, Month.APRIL, 5),
            LocalDate.of(2020, Month.APRIL, 7)
        ),
        Vacation(
            "20f21701-55bb-4ae3-96bc-1bf04204edfa",
            VacationType.DOCTOR,
            userRepository.getByName("don"),
            LocalDate.of(2020, Month.MAY, 5),
            LocalDate.of(2020, Month.MAY, 5)
        )
    )

    fun getFiltered(yearMonth: YearMonth, user: User): List<Vacation> {
        return vacations
            .filter(filterMonth(yearMonth))
            .filter(filterUser(user))
    }

    private fun filterMonth(yearMonth: YearMonth): (Vacation) -> Boolean {
        return {
            it.dateFrom.month == yearMonth.month && it.dateFrom.year == yearMonth.year
        }
    }

    private fun filterUser(user: User): (Vacation) -> Boolean {
        return {
            it.user.uuid == user.uuid
        }
    }
}