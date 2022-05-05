package com.sonnt.moneymanagement.data.dao

import androidx.room.*
import com.sonnt.moneymanagement.data.entities.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getWallets(): Flow<List<Wallet>>

    @Query("SELECT * FROM wallet LIMIT 1")
    fun getWallet(): Flow<Wallet>

    @Query("SELECT * FROM wallet WHERE id=:id")
    fun getWalletById(id: Long): Flow<Wallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet) : Long

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)
}