package com.aaltix.lotto.core.data.repository

import com.aaltix.lotto.core.data.api.LotteryApi
import com.aaltix.lotto.core.data.mapper.DataMapper.toDomain
import com.aaltix.lotto.core.data.mapper.DataMapper.toDomainList
import com.aaltix.lotto.core.data.mapper.DataMapper.toDomainTypeList
import com.aaltix.lotto.core.data.mapper.DataMapper.toDto
import com.aaltix.lotto.core.data.mapper.DataMapper.toEntity
import com.aaltix.lotto.core.database.dao.HistoryDao
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.LotteryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of LotteryRepository using stubbed API and Room database.
 * Handles all DTO/Entity to Domain model conversions internally.
 */
class LotteryRepositoryImpl(
    private val lotteryApi: LotteryApi,
    private val historyDao: HistoryDao
) : LotteryRepository {

    override suspend fun generateNumbers(lotteryType: LotteryType): GeneratedNumbers {
        val lotteryTypeDto = lotteryType.toDto()
        val generatedNumbersDto = lotteryApi.generateNumbers(lotteryTypeDto)

        // Save to history
        historyDao.insertHistory(generatedNumbersDto.toEntity())

        return generatedNumbersDto.toDomain()
    }

    override suspend fun getLotteryTypes(): List<LotteryType> {
        return lotteryApi.getLotteryTypes().toDomainTypeList()
    }

    override fun getHistory(): Flow<List<GeneratedNumbers>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toDto().toDomain() }
        }
    }

    override fun getHistoryByType(lotteryTypeId: String): Flow<List<GeneratedNumbers>> {
        return historyDao.getHistoryByType(lotteryTypeId).map { entities ->
            entities.map { it.toDto().toDomain() }
        }
    }

    override suspend fun getHistoryById(id: String): GeneratedNumbers? {
        return historyDao.getHistoryById(id)?.toDto()?.toDomain()
    }

    override suspend fun deleteHistory(id: String) {
        historyDao.deleteHistoryById(id)
    }

    override suspend fun clearAllHistory() {
        historyDao.clearAllHistory()
    }

    override fun getHistoryPaged(limit: Int, offset: Int): Flow<List<GeneratedNumbers>> {
        return historyDao.getHistoryPaged(limit, offset).map { entities ->
            entities.map { it.toDto().toDomain() }
        }
    }

    override suspend fun getHistoryCount(): Int {
        return historyDao.getHistoryCount()
    }
}
