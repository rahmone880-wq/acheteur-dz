package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

  @Query("SELECT * FROM app_settings WHERE id = 'default_settings' LIMIT 1")
  fun getSettings(): Flow<SettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = 'default_settings' LIMIT 1")
  suspend fun getSettingsDirect(): SettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: SettingsEntity)

  @Query("UPDATE app_settings SET isWarehouseModuleEnabled = :enabled WHERE id = 'default_settings'")
  suspend fun setWarehouseModuleEnabled(enabled: Boolean)
}
