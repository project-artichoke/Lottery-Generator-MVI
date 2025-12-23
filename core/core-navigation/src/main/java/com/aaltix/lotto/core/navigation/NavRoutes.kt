package com.aaltix.lotto.core.navigation

/**
 * Navigation route constants for the app.
 */
object NavRoutes {
    const val GENERATOR = "generator"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history/{entryId}"
    const val SETTINGS = "settings"

    // Custom lottery type routes
    const val CUSTOM_LOTTERY_TYPES = "settings/custom-types"
    const val ADD_LOTTERY_TYPE = "settings/custom-types/add"
    const val EDIT_LOTTERY_TYPE = "settings/custom-types/edit/{typeId}"

    fun historyDetail(entryId: String): String = "history/$entryId"
    fun editLotteryType(typeId: String): String = "settings/custom-types/edit/$typeId"
}
