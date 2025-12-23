package com.aaltix.lotto.core.domain.repository

/**
 * Combined repository interface for all lottery operations.
 * Extends segregated interfaces following Interface Segregation Principle.
 *
 * Use specific interfaces when only subset of functionality is needed:
 * - [NumberGeneratorRepository] for number generation
 * - [LotteryConfigRepository] for lottery type configuration
 * - [HistoryRepository] for history management
 */
interface LotteryRepository : NumberGeneratorRepository, LotteryConfigRepository, HistoryRepository
