package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DocumentVersionEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val database: AppDatabase) {

  private val projectDao = database.projectDao()
  private val procurementDao = database.procurementDao()
  private val auditDao = database.auditLogDao()
  private val versionDao = database.documentVersionDao()

  fun getActiveProjects(): Flow<List<ProjectEntity>> = projectDao.getActiveProjects()

  fun getProjectById(id: String): Flow<ProjectEntity?> = projectDao.getProjectById(id)

  fun getOrderLinesForProject(projectId: String): Flow<List<PurchaseOrderLineEntity>> =
    procurementDao.getOrderLinesForProject(projectId)

  fun getPurchaseOrdersForProject(projectId: String): Flow<List<PurchaseOrderEntity>> =
    procurementDao.getPurchaseOrdersForProject(projectId)

  fun getReceptionsForProject(projectId: String): Flow<List<ReceptionEntity>> =
    procurementDao.getReceptionsForProject(projectId)

  fun getInvoicesForProject(projectId: String): Flow<List<SupplierInvoiceEntity>> =
    procurementDao.getInvoicesForProject(projectId)

  suspend fun saveProject(project: ProjectEntity, currentUser: String = "Acheteur") {
    val existing = projectDao.getProjectByIdDirect(project.id)
    val now = System.currentTimeMillis()

    if (existing == null) {
      val newProj = project.copy(
        createdAt = now,
        createdBy = currentUser,
        updatedAt = now,
        updatedBy = currentUser,
        version = 1
      )
      projectDao.insert(newProj)

      auditDao.insert(
        AuditLogEntity(
          entityType = "PROJECT",
          entityId = newProj.id,
          action = "CREATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = null,
          newValueJson = com.example.core.audit.AuditSnapshotHelper.projectToJson(newProj),
          description = "Création du projet/chantier ${newProj.name} (${newProj.code}) - Budget: ${newProj.totalBudgetDzd} DA"
        )
      )

      versionDao.insert(
        DocumentVersionEntity(
          entityType = "PROJECT",
          entityId = newProj.id,
          versionNumber = 1,
          snapshotJson = com.example.core.audit.AuditSnapshotHelper.projectToJson(newProj),
          changedBy = currentUser,
          changeTimestamp = now,
          changeReason = "Création initiale"
        )
      )
    } else {
      val nextVersion = existing.version + 1
      val updated = project.copy(
        version = nextVersion,
        updatedAt = now,
        updatedBy = currentUser
      )
      projectDao.update(updated)

      auditDao.insert(
        AuditLogEntity(
          entityType = "PROJECT",
          entityId = updated.id,
          action = "UPDATE",
          who = currentUser,
          timestamp = now,
          oldValueJson = com.example.core.audit.AuditSnapshotHelper.projectToJson(existing),
          newValueJson = com.example.core.audit.AuditSnapshotHelper.projectToJson(updated),
          description = "Mise à jour v$nextVersion du projet ${updated.name}"
        )
      )
    }
  }

  suspend fun softArchiveProject(id: String, reason: String, currentUser: String = "Acheteur") {
    projectDao.softArchive(id, reason, currentUser, System.currentTimeMillis())
  }
}
