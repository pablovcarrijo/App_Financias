package com.example.projetofinal

import androidx.room.Delete
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartaoDao {
    @Query("SELECT * FROM cartoes ORDER BY id ASC")
    abstract fun observarCartoes(): Flow<List<Cartao>>

    //protected porque não vamos usar fora
    @Query("SELECT indiceCor FROM cartoes")
    protected abstract suspend fun consultarCoresUsadas(): List<Int>

    @Insert
    protected abstract suspend fun inserir(cartao: Cartao)

    // A consulta e a inserção acontecem na mesma transação: não há como
    // dois cadastros simultâneos ultrapassarem o limite por este método.
    @Transaction
    open suspend fun cadastrar(
        nome: String,
        banco: String,
        limiteCentavos: Long
    ): Boolean {
        require(
            nome.isNotBlank() && banco.isNotBlank() && limiteCentavos > 0
        )
        val coresUsadas = consultarCoresUsadas()
        if (coresUsadas.size >= MAXIMO_CARTOES) return false
        val corLivre = (0 until MAXIMO_CARTOES).first { it !in coresUsadas }
        inserir(
            Cartao(
                nome = nome, banco = banco,
                limiteCentavos = limiteCentavos,
                saldoDisponivelCentavos = limiteCentavos,
                indiceCor = corLivre
            )
        )
        return true
    }

    @Delete
    abstract suspend fun remover(cartao: Cartao)
    @Query("""
    UPDATE cartoes
    SET nome = :nome,
        banco = :banco,
        saldoDisponivelCentavos =
            saldoDisponivelCentavos + (:limiteCentavos - limiteCentavos),
        limiteCentavos = :limiteCentavos
    WHERE id = :id
      AND :limiteCentavos >= (limiteCentavos - saldoDisponivelCentavos)
""")
    abstract suspend fun editar(
        id: Long,
        nome: String,
        banco: String,
        limiteCentavos: Long
    ): Int
}
