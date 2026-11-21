package com.example.projetofinal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartoesUiState(
    val cartoes: List<Cartao> = emptyList(),
    val carregando: Boolean = true,
    val erroLista: String? = null,
    val nome: String = "",
    val banco: String = "",
    val limite: String = "",
    val erroNome: String? = null,
    val erroBanco: String? = null,
    val erroLimite: String? = null,
    val erroCadastro: String? = null,
    val salvando: Boolean = false,
    val cadastroConcluido: Boolean = false,
    val removendoId: Long? = null,
    val erroRemocao: String? = null
) {
    val podeCadastrar: Boolean
        get() = !carregando && erroLista == null && cartoes.size < MAXIMO_CARTOES
}

class CartoesViewModel(application: Application) : AndroidViewModel(application) {
    //cria instância do banco, pega o Dao, e depois cria o repository
    private val repository = CartoesRepository(CartoesDatabase.obter(application).cartaoDao())
    //permite alterar estado
    private val _uiState = MutableStateFlow(CartoesUiState())
    val uiState = _uiState.asStateFlow()
    private var observacao: Job? = null


    init { observarCartoes() }

    //carrega todos os cartoes
    fun observarCartoes() {
        observacao?.cancel()
        _uiState.update { it.copy(carregando = true, erroLista = null) }
        observacao = viewModelScope.launch {
            //collect por conta do Flow
            try {
                repository.cartoes.collect { lista ->
                    _uiState.update { it.copy(cartoes = lista, carregando = false, erroLista = null) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(carregando = false,
                    erroLista = "Não foi possível carregar os cartões.") }
            }
        }
    }

    fun iniciarCadastro() {
        if (_uiState.value.salvando) return
        _uiState.update { it.copy(nome = "", banco = "", limite = "",
            erroNome = null, erroBanco = null, erroLimite = null,
            erroCadastro = null, cadastroConcluido = false) }
    }

    /*
    Mesma coisa que:
    fun alterarNome(valor: String) {
        if (!_uiState.value.salvando) {
            _uiState.update { estadoAtual ->
                estadoAtual.copy(
                    nome = valor.take(100),
                    erroNome = null,
                    erroCadastro = null
                )
            }
        }
    }
   */
    //".take" limita o valor do nome
    fun alterarNome(valor: String) {
        if (!_uiState.value.salvando) _uiState.update {
            it.copy(nome = valor.take(100), erroNome = null, erroCadastro = null)
        }
    }

    fun alterarBanco(valor: String) {
        if (!_uiState.value.salvando) _uiState.update {
            it.copy(banco = valor.take(80), erroBanco = null, erroCadastro = null)
        }
    }

    fun alterarLimite(valor: String) {
        if (!_uiState.value.salvando) _uiState.update {
            it.copy(limite = valor.take(30), erroLimite = null, erroCadastro = null)
        }
    }

    fun cadastrar() {
        val estado = _uiState.value
        if (estado.salvando || estado.cadastroConcluido) return
        if (!estado.podeCadastrar) {
            _uiState.update { it.copy(erroCadastro =
                if (it.cartoes.size >= MAXIMO_CARTOES) "Você já possui 5 cartões."
                else "Aguarde o carregamento dos cartões.") }
            return
        }

        val centavos = converterParaCentavos(estado.limite)
        val erroNome = if (estado.nome.isBlank()) "Informe o nome do cartão." else null
        val erroBanco = if (estado.banco.isBlank()) "Informe o banco." else null
        val erroLimite = if (centavos == null)
            "Informe um valor numérico e que seja positivo, como 3.000,00." else null
        _uiState.update { it.copy(erroNome = erroNome, erroBanco = erroBanco,
            erroLimite = erroLimite, erroCadastro = null) }
        if (erroNome != null || erroBanco != null || centavos == null) return

        // Atualizado antes de iniciar a corrotina, impedindo toques duplicados.
        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            try {
                val inserido = repository.cadastrar(
                    estado.nome.trim(), estado.banco.trim(), centavos
                )
                _uiState.update { it.copy(salvando = false, cadastroConcluido = inserido,
                    erroCadastro = if (inserido) null else "Você já possui 5 cartões.") }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(salvando = false,
                    erroCadastro = "Não foi possível salvar. Tente novamente.") }
            }
        }
    }

    fun consumirCadastroConcluido() {
        _uiState.update { it.copy(cadastroConcluido = false) }
    }

    fun removerCartao(cartao: Cartao) {
        val estado = _uiState.value

        // Evita exclusões simultâneas e exclusão durante um cadastro.
        if (estado.removendoId != null || estado.salvando) return

        _uiState.update {
            it.copy(
                removendoId = cartao.id,
                erroRemocao = null
            )
        }

        viewModelScope.launch {
            try {
                repository.remover(cartao)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        erroRemocao = "Não foi possível remover o cartão. Tente novamente."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(removendoId = null)
                }
            }
        }
    }
}
