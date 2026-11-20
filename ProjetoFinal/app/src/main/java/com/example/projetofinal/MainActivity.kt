package com.example.projetofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.projetofinal.ui.theme.ProjetoFinalTheme
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjetoFinalTheme {
                val viewModel: CartoesViewModel = viewModel()
                val estado by viewModel.uiState.collectAsStateWithLifecycle()
                var cadastrando by rememberSaveable { mutableStateOf(false) }

                // Fecha a tela de cadastro quando o cartão é salvo.
                LaunchedEffect(estado.cadastroConcluido) {
                    if (estado.cadastroConcluido) {
                        viewModel.consumirCadastroConcluido()
                        cadastrando = false
                    }
                }

                // Gesto/botão voltar do sistema (bloqueado enquanto salva).
                BackHandler(enabled = cadastrando && !estado.salvando) {
                    cadastrando = false
                }

                if (cadastrando) {
                    TelaCadastrarCartao(
                        estado = estado,
                        onNomeChange = viewModel::alterarNome,
                        onBancoChange = viewModel::alterarBanco,
                        onLimiteChange = viewModel::alterarLimite,
                        onCadastrar = viewModel::cadastrar,
                        onVoltar = { cadastrando = false }
                    )
                } else {
                    TelaMeusCartoes(
                        estado = estado,
                        onCadastrar = {
                            viewModel.iniciarCadastro() // limpa os campos
                            cadastrando = true
                        },
                        onTentarNovamente = viewModel::observarCartoes
                    )
                }
            }
        }
    }
}