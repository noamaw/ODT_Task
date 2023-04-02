package com.noam.odt_task.model

import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor() : PatientRepository {
    override fun getPatients(): List<Patient> = listOf(
        Patient("yossi", "https://android-tools.ru/wp-content/uploads/2021/09/Group-106.png", "", emptyList()),
        Patient("david", "https://android-tools.ru/wp-content/uploads/2021/09/Group-105.png", "", emptyList()),
        Patient("morris", "https://android-tools.ru/wp-content/uploads/2021/09/%D1%85%D0%B0%D0%BA%D0%B5%D1%80.png", "", emptyList()),
        Patient("jason", "https://android-tools.ru/wp-content/uploads/2021/09/%D1%85%D0%B0%D0%BA%D0%B5%D1%80-2.png", "", emptyList()),
        Patient("hank", "https://android-tools.ru/wp-content/uploads/2021/09/Group-107.png", "", emptyList()),
        Patient("jay", "https://android-tools.ru/wp-content/uploads/2021/09/%D1%85%D0%B0%D0%BA%D0%B5%D1%80-3.png", "", emptyList()),
    )
}