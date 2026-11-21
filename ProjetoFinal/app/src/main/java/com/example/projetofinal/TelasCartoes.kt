package com.example.projetofinal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetofinal.ui.theme.ProjetoFinalTheme

val Fundo = Color(0xFFF8FAF8)
val VerdePrincipal = Color(0xFF007254)
val TextoPrincipal = Color(0xFF102720)
private val TextoSecundario = Color(0xFF52635D)
private val CoresCartoes = listOf(
    Color(0xFF005B43), // Verde escuro
    Color(0xFF294B60), // Azul petróleo
    Color(0xFF24666A), // Verde azulado
    Color(0xFF455D70), // Azul acinzentado
    Color(0xFF3B6253)  // Verde musgo
)

@Composable
fun TelaMeusCartoes(
    estado: CartoesUiState,
    onCadastrar: () -> Unit,
    onTentarNovamente: () -> Unit,
    onRemover: (Cartao) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Meus cartões",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                estado.carregando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = VerdePrincipal
                    )
                }

                estado.erroLista != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = estado.erroLista,
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(onClick = onTentarNovamente) {
                            Text("Tentar novamente")
                        }
                    }
                }

                estado.cartoes.isEmpty() -> {
                    Text(
                        text = "Você ainda não cadastrou cartões.",
                        color = TextoSecundario,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = estado.cartoes,
                            key = { it.id }
                        ) { cartao ->
                            CardCartao(
                                cartao = cartao,
                                removendo = estado.removendoId == cartao.id,
                                podeRemover = estado.removendoId == null &&
                                        !estado.salvando,
                                onRemover = { onRemover(cartao) }
                            ) // Fecha a chamada de CardCartao.
                        } // Fecha o bloco de items.
                    } // Fecha LazyColumn.
                } // Fecha o else.
            } // Fecha when.
        } // Fecha Box.

        estado.erroRemocao?.let { mensagem ->
            Text(
                text = mensagem,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (estado.cartoes.size >= MAXIMO_CARTOES) {
            Text(
                text = "Limite de 5 cartões atingido.",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        BotaoPrincipal(
            onClick = onCadastrar,
            enabled = estado.podeCadastrar,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text("+", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Cadastrar cartão",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } // Fecha Column.
} // Fecha TelaMeusCartoes.


@Composable
private fun CardCartao(
    cartao: Cartao,
    removendo: Boolean,
    podeRemover: Boolean,
    onRemover: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = CoresCartoes[cartao.indiceCor],
                contentColor = Color.White
            ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth()
                .heightIn(min = 144.dp)
                .padding(start = 18.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconeCartao(Modifier.size(width = 32.dp, height = 24.dp))
                    Spacer(Modifier.width(16.dp))
                    Column(
                        Modifier.weight(1f)
                    ) {
                        Text(
                            cartao.nome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            cartao.banco,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = .9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Saldo disponível",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = .9f)
                )
                // Exibe o saldo persistido, que poderá diminuir com os gastos.
                Text(
                    formatarReais(cartao.saldoDisponivelCentavos),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { /* Sem navegação. */ }
                ) {
                    IconeSeta(
                        voltar = false,
                        cor = Color.White,
                        descricao = "Opções do cartão ${cartao.nome}"
                    )
                }

                IconButton(
                    onClick = onRemover,
                    enabled = podeRemover,
                    modifier = Modifier.semantics {
                        contentDescription = if (removendo) {
                            "Removendo cartão ${cartao.nome}"
                        } else {
                            "Remover cartão ${cartao.nome}"
                        }
                    }
                ) {
                    if (removendo) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconeLixeira(
                            cor = Color.White.copy(
                                alpha = if (podeRemover) 1f else 0.5f
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelaCadastrarCartao(
    estado: CartoesUiState,
    onNomeChange: (String) -> Unit,
    onBancoChange: (String) -> Unit,
    onLimiteChange: (String) -> Unit,
    onCadastrar: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    //safedrawingpadding -> protege conteúdo contra área ocupados pelo sistema
    //  como barra de status
    //imepadding -> espaço se ajuste caso seja necessário teclado virtual
    val foco = LocalFocusManager.current
    Column(
        modifier.fillMaxSize()
        .safeDrawingPadding()
        .imePadding()
        .padding(horizontal = 16.dp))
    {
        Row(
            Modifier.fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            IconButton(onClick = onVoltar, enabled = !estado.salvando) {
                IconeSeta(voltar = true, cor = TextoPrincipal, descricao = "Voltar")
            }
            Text("Cadastrar cartão", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = TextoPrincipal, modifier = Modifier.weight(1f))
        }
        Column(
            Modifier.weight(1f)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoCadastro(
                valor = estado.nome,
                onValorChange = onNomeChange,
                rotulo = "Nome do cartão",
                placeholder = "Cartão principal",
                erro = estado.erroNome,
                enabled = !estado.salvando,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next)
            )
            CampoCadastro(
                valor = estado.banco,
                onValorChange = onBancoChange,
                rotulo = "Banco",
                placeholder = "Meu banco",
                erro = estado.erroBanco,
                enabled = !estado.salvando,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next))
            CampoCadastro(
                valor = estado.limite,
                onValorChange = onLimiteChange,
                rotulo = "Limite total",
                placeholder = "3.000,00",
                erro = estado.erroLimite,
                enabled = !estado.salvando,
                prefixo = { Text("R$ ") },
                ajuda = "Usado para calcular o saldo disponível",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { foco.clearFocus() })
            )
            if (estado.erroCadastro != null) {
                Text(estado.erroCadastro, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
        }
        BotaoPrincipal(
            onClick = { foco.clearFocus(); onCadastrar() },
            enabled = estado.podeCadastrar && !estado.salvando && !estado.cadastroConcluido,
            modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
        ) {
            if (estado.salvando) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                    color = TextoPrincipal)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                if (estado.salvando) "Cadastrando..."
                else "Cadastrar cartão",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CampoCadastro(
    valor: String,
    onValorChange: (String) -> Unit,
    rotulo: String,
    placeholder: String,
    erro: String?,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions,
    prefixo: (@Composable () -> Unit)? = null,
    ajuda: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    //a diferença pro TextField é porque possui contorno
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(rotulo) },
        placeholder = { Text(placeholder) },
        prefix = prefixo,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = enabled,
        isError = erro != null,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        supportingText = if (erro != null || ajuda != null) {
            { Text(erro ?: ajuda.orEmpty()) }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdePrincipal,
            unfocusedBorderColor = Color(0xFF9CAEA7),
            focusedLabelColor = VerdePrincipal,
            unfocusedLabelColor = TextoPrincipal,
            focusedTextColor = TextoPrincipal,
            unfocusedTextColor = TextoPrincipal,
            focusedContainerColor = Fundo,
            unfocusedContainerColor = Fundo
        )
    )
}

@Composable
private fun BotaoPrincipal(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal,
            contentColor = Color.White), content = content
    )
}

@Composable
private fun IconeCartao(
    modifier: Modifier = Modifier
) {
    //permite desenhar formas diretamente na tela, nós programadores decidimos
    Canvas(modifier) {
        val espessura = 1.8.dp.toPx()
        drawRoundRect(
            Color.White,
            topLeft = Offset(espessura / 2, espessura / 2),
            size = androidx.compose.ui.geometry.Size(size.width - espessura,
                size.height - espessura),
            cornerRadius = CornerRadius(4.dp.toPx()), style = Stroke(espessura)
        )
        drawLine(
            Color.White,
            Offset(0f, size.height * .36f),
            Offset(size.width, size.height * .36f),
            espessura
        )
    }
}

@Composable
private fun IconeSeta(
    voltar: Boolean,
    cor: Color,
    descricao: String
) {
    Canvas(
        Modifier.size(24.dp).semantics { contentDescription = descricao }
    ) {
        val espessura = 1.8.dp.toPx()
        if (voltar) {
            drawLine(
                cor,
                Offset(size.width * .8f, size.height * .5f),
                Offset(size.width * .2f, size.height * .5f),
                espessura,
                StrokeCap.Round
            )
            drawLine(
                cor,
                Offset(size.width * .2f, size.height * .5f),
                Offset(size.width * .45f, size.height * .25f),
                espessura, StrokeCap.Round
            )
            drawLine(
                cor,
                Offset(size.width * .2f, size.height * .5f),
                Offset(size.width * .45f, size.height * .75f),
                espessura, StrokeCap.Round
            )
        } else {
            drawLine(
                cor,
                Offset(size.width * .4f, size.height * .25f),
                Offset(size.width * .65f, size.height * .5f),
                espessura, StrokeCap.Round
            )
            drawLine(
                cor,
                Offset(size.width * .65f, size.height * .5f),
                Offset(size.width * .4f, size.height * .75f),
                espessura, StrokeCap.Round
            )
        }
    }
}

@Composable
private fun IconeLixeira(
    cor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val espessura = 1.8.dp.toPx()

        // Coordenadas proporcionais a uma área de 24 × 24.
        fun ponto(x: Float, y: Float) = Offset(
            x = size.width * x / 24f,
            y = size.height * y / 24f
        )

        fun linha(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float
        ) {
            drawLine(
                color = cor,
                start = ponto(x1, y1),
                end = ponto(x2, y2),
                strokeWidth = espessura,
                cap = StrokeCap.Round
            )
        }

        // Tampa.
        linha(4f, 6f, 20f, 6f)

        // Alça.
        linha(9f, 6f, 9f, 3f)
        linha(9f, 3f, 15f, 3f)
        linha(15f, 3f, 15f, 6f)

        // Corpo.
        linha(6f, 6f, 7f, 21f)
        linha(7f, 21f, 17f, 21f)
        linha(17f, 21f, 18f, 6f)

        // Linhas internas.
        linha(10f, 10f, 10f, 17f)
        linha(14f, 10f, 14f, 17f)
    }
}
