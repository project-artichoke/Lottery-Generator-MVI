package com.aaltix.lotto.core.data.repository

import com.aaltix.lotto.core.data.mapper.CustomLotteryTypeMapper.toDomain
import com.aaltix.lotto.core.data.mapper.CustomLotteryTypeMapper.toEntity
import com.aaltix.lotto.core.database.dao.CustomLotteryTypeDao
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.repository.CustomLotteryTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of CustomLotteryTypeRepository using Room database.
 * Handles all Entity to Domain model conversions internally.
 */
class CustomLotteryTypeRepositoryImpl(
    private val customLotteryTypeDao: CustomLotteryTypeDao
) : CustomLotteryTypeRepository {

    override fun getCustomLotteryTypes(): Flow<List<LotteryType>> {
        return customLotteryTypeDao.getAllCustomTypes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCustomLotteryTypesCount(): Flow<Int> {
        return customLotteryTypeDao.getCount()
    }

    override suspend fun getCustomLotteryTypeById(id: String): LotteryType? {
        return customLotteryTypeDao.getById(id)?.toDomain()
    }

    override suspend fun saveCustomLotteryType(type: LotteryType) {
        val now = System.currentTimeMillis()
        val existing = customLotteryTypeDao.getById(type.id)
        val entity = type.toEntity(
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )
        customLotteryTypeDao.insert(entity)
    }

    override suspend fun deleteCustomLotteryType(id: String) {
        customLotteryTypeDao.deleteById(id)
    }

    override suspend fun customLotteryTypeExists(id: String): Boolean {
        return customLotteryTypeDao.exists(id)
    }
}
