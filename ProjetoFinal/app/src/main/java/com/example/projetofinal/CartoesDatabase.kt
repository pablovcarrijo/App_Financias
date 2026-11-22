package com.example.projetofinal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Cartao::class], version = 2, exportSchema = false)
abstract class CartoesDatabase : RoomDatabase() {
    abstract fun cartaoDao(): CartaoDao

    companion object {
        //volatile garante que quando uma thread atribuir o banco à variável,
        //outras consigam enxergar essa atualização
        @Volatile private var instancia: CartoesDatabase? = null

        // Executada somente ao atualizar um banco da versão 1 para a 2.
        // Cartões existentes recebem o limite como saldo inicial.
//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE cartoes ADD COLUMN saldoDisponivelCentavos INTEGER NOT NULL DEFAULT 0")
//                db.execSQL("UPDATE cartoes SET saldoDisponivelCentavos = limiteCentavos")
//            }
//        }

        //caso já houver uma instância utiliza ela, caso não, cria outra
        fun obter(context: Context): CartoesDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                CartoesDatabase::class.java,
                "cartoes.db"
            ).build().also { instancia = it }
                //.addMigrations(MIGRATION_1_2).build().also { instancia = it }
        }
    }
}
