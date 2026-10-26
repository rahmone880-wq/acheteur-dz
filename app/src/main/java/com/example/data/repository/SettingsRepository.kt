package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val database: AppDatabase) {

  private val settingsDao = database.settingsDao()

  fun getSettings(): Flow<SettingsEntity> {
    return settingsDao.getSettings().map { it ?: SettingsEntity() }
  }

  suspend fun updateSettings(settings: SettingsEntity) {
    settingsDao.insertOrUpdate(settings)
  }

  suspend fun toggleWarehouseModule(enabled: Boolean) {
    val current = settingsDao.getSettingsDirect() ?: SettingsEntity()
    settingsDao.insertOrUpdate(current.copy(isWarehouseModuleEnabled = enabled))
  }

  suspend fun updateBuyerProfile(
    userName: String,
    userRole: String,
    userEmail: String,
    userPhone: String,
    gmailSignature: String,
    companyName: String? = null
  ) {
    val current = settingsDao.getSettingsDirect() ?: SettingsEntity()
    settingsDao.insertOrUpdate(
      current.copy(
        currentUserName = userName,
        currentUserRole = userRole,
        currentUserEmail = userEmail,
        currentUserPhone = userPhone,
        gmailSignature = gmailSignature,
        companyName = companyName ?: current.companyName
      )
    )
  }

  suspend fun setGoogleLinked(isLinked: Boolean, email: String? = null, displayName: String? = null) {
    val current = settingsDao.getSettingsDirect() ?: SettingsEntity()
    settingsDao.insertOrUpdate(
      current.copy(
        isGoogleLinked = isLinked,
        currentUserEmail = if (isLinked && !email.isNullOrBlank()) email else current.currentUserEmail,
        googleDisplayName = if (isLinked && !displayName.isNullOrBlank()) displayName else current.googleDisplayName,
        lastSyncTimestamp = if (isLinked) System.currentTimeMillis() else current.lastSyncTimestamp
      )
    )
  }
}
