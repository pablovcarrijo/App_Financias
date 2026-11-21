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
}
