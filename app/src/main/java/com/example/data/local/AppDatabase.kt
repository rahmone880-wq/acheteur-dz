package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.daos.AuditLogDao
import com.example.data.local.daos.DocumentVersionDao
import com.example.data.local.daos.ProcurementDao
import com.example.data.local.daos.ProductDao
import com.example.data.local.daos.ProjectDao
import com.example.data.local.daos.SettingsDao
import com.example.data.local.daos.StatusHistoryDao
import com.example.data.local.daos.SupplierBankAccountDao
import com.example.data.local.daos.SupplierDao
import com.example.data.local.daos.SupplierDocumentDao
import com.example.data.local.daos.SupplierInteractionDao
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DocumentVersionEntity
import com.example.data.local.entities.PaymentEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.ReceptionLineEntity
import com.example.data.local.entities.SettingsEntity
import com.example.data.local.entities.StatusHistoryEntity
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInteractionEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import com.example.data.local.entities.WarehouseStockEntity

@Database(
  entities = [
    SupplierEntity::class,
    SupplierBankAccountEntity::class,
    SupplierDocumentEntity::class,
    SupplierInteractionEntity::class,
    AuditLogEntity::class,
    DocumentVersionEntity::class,
    StatusHistoryEntity::class,
    SettingsEntity::class,
    ProjectEntity::class,
    ProductEntity::class,
    PurchaseRequestEntity::class,
    PurchaseOrderEntity::class,
    PurchaseOrderLineEntity::class,
    ReceptionEntity::class,
    ReceptionLineEntity::class,
    SupplierInvoiceEntity::class,
    PaymentEntity::class,
    WarehouseStockEntity::class
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun supplierDao(): SupplierDao
  abstract fun supplierBankAccountDao(): SupplierBankAccountDao
  abstract fun supplierDocumentDao(): SupplierDocumentDao
  abstract fun supplierInteractionDao(): SupplierInteractionDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun documentVersionDao(): DocumentVersionDao
  abstract fun statusHistoryDao(): StatusHistoryDao
  abstract fun settingsDao(): SettingsDao
  abstract fun projectDao(): ProjectDao
  abstract fun productDao(): ProductDao
  abstract fun procurementDao(): ProcurementDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "acheteur_dz.db"
        )
          .fallbackToDestructiveMigration(true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
