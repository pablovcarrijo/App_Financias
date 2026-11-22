package com.example.projetofinal

class CartoesRepository(private val dao: CartaoDao) {
    val cartoes = dao.observarCartoes()

    suspend fun cadastrar(
        nome: String,
        banco: String,
        limiteCentavos: Long
    ): Boolean =
        dao.cadastrar(nome, banco, limiteCentavos)

    suspend fun remover(cartao: Cartao) {
        dao.remover(cartao)
    }

    suspend fun editar(
        id: Long,
        nome: String,
        banco: String,
        limiteCentavos: Long
    ): Boolean {
        return dao.editar(id, nome, banco, limiteCentavos) == 1
    }
}
