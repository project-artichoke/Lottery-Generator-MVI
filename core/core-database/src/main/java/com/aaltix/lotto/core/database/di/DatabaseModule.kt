package com.aaltix.lotto.core.database.di

import androidx.room.Room
import com.aaltix.lotto.core.database.LottoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            LottoDatabase::class.java,
            LottoDatabase.DATABASE_NAME
        )
            .addMigrations(
                LottoDatabase.MIGRATION_1_2,
                LottoDatabase.MIGRATION_2_3
            )
            .build()
    }

    single { get<LottoDatabase>().historyDao() }
    single { get<LottoDatabase>().customLotteryTypeDao() }
}
