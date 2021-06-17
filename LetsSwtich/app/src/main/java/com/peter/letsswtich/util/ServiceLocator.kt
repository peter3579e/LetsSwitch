package com.peter.letsswtich.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.peter.letsswtich.data.source.DefaultLetsSwitchRepository
import com.peter.letsswtich.data.source.LetsSwitchDataSource
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.data.source.local.LetsSwitchLocalDataSource
import com.peter.letsswtich.data.source.remote.LetsSwitchRemoteDataSource

/**
 * A Service Locator for the [LetsSwitchRepository].
 */

object ServiceLocator {

    @Volatile
    var letsSwitchRepository: LetsSwitchRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): LetsSwitchRepository {
        synchronized(this) {
            return letsSwitchRepository
                ?: letsSwitchRepository
                ?: createLetsSwitchRepository(context)
        }
    }

    private fun createLetsSwitchRepository(context: Context): LetsSwitchRepository {
        return DefaultLetsSwitchRepository(
            LetsSwitchRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): LetsSwitchDataSource {
        return LetsSwitchLocalDataSource(context)
    }
}