package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import kotlinx.coroutines.flow.Flow

class AuditRepository(private val database: AppDatabase) {

  private val auditDao = database.auditLogDao()

  fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = auditDao.getAllLogs()

  fun getLogsForEntity(entityType: String, entityId: String): Flow<List<AuditLogEntity>> =
    auditDao.getLogsForEntity(entityType, entityId)

  fun getLogsByEntityType(entityType: String): Flow<List<AuditLogEntity>> =
    auditDao.getLogsByEntityType(entityType)
}
