package com.aaltix.lotto.core.domain.fake

import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCustomLotteryTypeRepository : CustomLotteryTypeRepository {

    private val customTypes = MutableStateFlow<List<LotteryType>>(emptyList())

    override fun getCustomLotteryTypes(): Flow<List<LotteryType>> = customTypes

    override fun getCustomLotteryTypesCount(): Flow<Int> = customTypes.map { it.size }

    override suspend fun getCustomLotteryTypeById(id: String): LotteryType? {
        return customTypes.value.find { it.id == id }
    }

    override suspend fun saveCustomLotteryType(type: LotteryType) {
        customTypes.value = customTypes.value
            .filterNot { it.id == type.id } + type
    }

    override suspend fun deleteCustomLotteryType(id: String) {
        customTypes.value = customTypes.value.filterNot { it.id == id }
    }

    override suspend fun customLotteryTypeExists(id: String): Boolean {
        return customTypes.value.any { it.id == id }
    }

    fun setCustomTypes(types: List<LotteryType>) {
        customTypes.value = types
    }
}
