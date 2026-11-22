package com.example.projetofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.projetofinal.ui.theme.ProjetoFinalTheme
import kotlinx.serialization.Serializable

//serializable salva e restaura as rotas
//data object porque não preciso passar dados para rota
@Serializable
private data object MeusCartoes : NavKey

@Serializable
private data object CadastrarCartao : NavKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProjetoFinalTheme {
                // Uma única instância, compartilhada pelas duas telas.
                val viewModel: CartoesViewModel = viewModel()
                val estado by viewModel.uiState.collectAsStateWithLifecycle()

                //a primeira tela da pilha é MeusCartoes.
                val backStack = rememberNavBackStack(MeusCartoes)

                //quando a chave muda volta para MeusCartoes
                LaunchedEffect(estado.cadastroConcluido) {
                    if (estado.cadastroConcluido) {
                        if (backStack.lastOrNull() == CadastrarCartao) {
                            backStack.removeLastOrNull()
                        }

                        //muda cadastrando para falso novamente
                        viewModel.consumirCadastroConcluido()
                    }
                }

                NavDisplay(
                    backStack = backStack,

                    //volta apenas se tiver para onde voltar e
                    //não estiver no processo de salvar
                    onBack = {
                        if (!estado.salvando && backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },

                    // Associa cada rota ao Composable correspondente.
                    entryProvider = { rota ->
                        when (rota) {
                            MeusCartoes -> NavEntry(rota) {
                                TelaMeusCartoes(
                                    estado = estado,
                                    onTentarNovamente = viewModel::observarCartoes,
                                    onRemover = viewModel::removerCartao,
                                    onCadastrar = {
                                        if (
                                            estado.podeCadastrar &&
                                            !estado.salvando &&
                                            estado.removendoId == null &&
                                            backStack.lastOrNull() == MeusCartoes
                                        ) {
                                            viewModel.iniciarCadastro()
                                            backStack.add(CadastrarCartao)
                                        }
                                    },
                                    onEditar = { cartao ->
                                        if (
                                            !estado.salvando &&
                                            estado.removendoId == null &&
                                            backStack.lastOrNull() == MeusCartoes
                                        ) {
                                            viewModel.iniciarEdicao(cartao)
                                            backStack.add(CadastrarCartao)
                                        }
                                    }
                                )
                            }

                            CadastrarCartao -> NavEntry(rota) {
                                TelaCadastrarCartao(
                                    estado = estado,
                                    onNomeChange = viewModel::alterarNome,
                                    onBancoChange = viewModel::alterarBanco,
                                    onLimiteChange = viewModel::alterarLimite,
                                    onCadastrar = viewModel::cadastrar,

                                    // Trata a seta de voltar da própria tela.
                                    onVoltar = {
                                        if (
                                            !estado.salvando &&
                                            backStack.lastOrNull() == CadastrarCartao
                                        ) {
                                            backStack.removeLastOrNull()
                                        }
                                    }
                                )
                            }

                            else -> error("Rota desconhecida: $rota")
                        }
                    }
                )

                // Bloqueia o retorno enquanto o cartão está sendo salvo.
                BackHandler(enabled = estado.salvando) {
                    // Aguarda a gravação terminar.
                }
            }
        }
    }
}