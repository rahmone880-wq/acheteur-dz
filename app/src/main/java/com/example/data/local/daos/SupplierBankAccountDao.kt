package com.example.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.SupplierBankAccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Supplier Bank Accounts.
 * STRICT POLICY: NO HARD DELETES. Only soft-archiving.
 */
@Dao
interface SupplierBankAccountDao {

  @Query("SELECT * FROM supplier_bank_accounts WHERE supplierId = :supplierId AND isArchived = 0 ORDER BY isPrimary DESC, bankName ASC")
  fun getActiveAccountsForSupplier(supplierId: String): Flow<List<SupplierBankAccountEntity>>

  @Query("SELECT * FROM supplier_bank_accounts")
  suspend fun getAllAccountsDirect(): List<SupplierBankAccountEntity>

  @Query("SELECT * FROM supplier_bank_accounts WHERE id = :id LIMIT 1")
  suspend fun getAccountById(id: String): SupplierBankAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(account: SupplierBankAccountEntity)

  @Update
  suspend fun update(account: SupplierBankAccountEntity)

  @Query("UPDATE supplier_bank_accounts SET isArchived = 1, archiveReason = :reason, updatedAt = :timestamp, updatedBy = :user WHERE id = :id")
  suspend fun softArchive(id: String, reason: String, user: String, timestamp: Long)

  @Query("UPDATE supplier_bank_accounts SET isPrimary = 0 WHERE supplierId = :supplierId")
  suspend fun clearPrimaryFlags(supplierId: String)

  @Query("UPDATE supplier_bank_accounts SET isPrimary = 1 WHERE id = :id")
  suspend fun setPrimaryAccount(id: String)
}
