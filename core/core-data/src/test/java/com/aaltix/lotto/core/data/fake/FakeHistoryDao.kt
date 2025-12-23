package com.aaltix.lotto.core.data.fake

import com.aaltix.lotto.core.database.dao.HistoryDao
import com.aaltix.lotto.core.database.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of HistoryDao for testing.
 */
class FakeHistoryDao : HistoryDao {

    private val entries = MutableStateFlow<List<HistoryEntryEntity>>(emptyList())

    override fun getAllHistory(): Flow<List<HistoryEntryEntity>> = entries

    override fun getHistoryPaged(limit: Int, offset: Int): Flow<List<HistoryEntryEntity>> {
        return entries.map { list ->
            list.sortedByDescending { it.timestamp }
                .drop(offset)
                .take(limit)
        }
    }

    override fun getHistoryByType(lotteryTypeId: String): Flow<List<HistoryEntryEntity>> {
        return entries.map { list ->
            list.filter { it.lotteryTypeId == lotteryTypeId }
        }
    }

    override suspend fun getHistoryById(id: String): HistoryEntryEntity? {
        return entries.value.find { it.id == id }
    }

    override suspend fun insertHistory(entry: HistoryEntryEntity) {
        entries.value = entries.value + entry
    }

    override suspend fun deleteHistory(entry: HistoryEntryEntity) {
        entries.value = entries.value.filter { it.id != entry.id }
    }

    override suspend fun deleteHistoryById(id: String) {
        entries.value = entries.value.filter { it.id != id }
    }

    override suspend fun clearAllHistory() {
        entries.value = emptyList()
    }

    override suspend fun getHistoryCount(): Int {
        return entries.value.size
    }

    fun reset() {
        entries.value = emptyList()
    }

    fun getEntriesCount(): Int = entries.value.size
}
