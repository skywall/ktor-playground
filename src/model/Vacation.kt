package cz.skywall.microfunspace.model

import java.time.LocalDate

data class Vacation(
    val uuid: String,
    val vacationType: VacationType,
    val user: User,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)

enum class VacationType {
    HOME_OFFICE,
    DOCTOR,
}