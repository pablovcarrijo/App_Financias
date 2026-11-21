# Cartões integrados ao ProjetoFinal

Abra a pasta `pf_pdm/App_Financias/ProjetoFinal` no Android Studio. As dependências e os arquivos Kotlin já estão integrados; basta sincronizar o Gradle e executar o módulo `app`.

## Funcionalidades

- A MainActivity abre a tela **Meus cartões**.
- **+ Cadastrar cartão** abre a tela de cadastro usando Navigation 3.
- Nome, banco e limite são validados. Para o limite, use `3000`, `3000,00` ou `3.000,00`.
- O cartão é salvo no Room e a navegação retorna à lista.
- Há um máximo de cinco cartões, com cores diferentes e persistentes.
- As setas dos cartões são botões sem ação.
- `saldoDisponivelCentavos` é uma coluna independente de `limiteCentavos`. No cadastro recebe o valor do limite, e a tela exibe o saldo. Futuras despesas poderão diminuir o saldo sem mudar o limite.
- O banco `cartoes.db` usa a versão 2, com migração da versão 1 que preserva os cartões e inicializa os saldos existentes.
- O banco inicia vazio em uma instalação nova. Os cartões de exemplo aparecem apenas nas funções `@Preview`.

## Arquivos

Os oito arquivos de cartões estão em `app/src/main/java/com/example/projetofinal/`: `MainActivity.kt`, `TelasCartoes.kt`, `Cartao.kt`, `CartaoDao.kt`, `CartoesDatabase.kt`, `CartoesRepository.kt`, `CartoesViewModel.kt` e `Dinheiro.kt`.

O tema existente `ProjetoFinalTheme` foi adaptado para a paleta das imagens. A aplicação usa tema claro e cores fixas para manter esse visual. Os previews estão ao final de `TelasCartoes.kt`.

## Configuração

Foram mantidos o pacote `com.example.projetofinal`, o AGP 9.3.3, o wrapper Gradle 9.5.0, o compileSdk/targetSdk 37 e o minSdk 24 do projeto enviado. O Manifest já estava configurado com `adjustResize`.

O catálogo e os dois arquivos `build.gradle.kts` já incluem Room 2.8.5, Navigation 3 1.2.1, Serialization 1.9.0, Lifecycle Compose e KSP 2.3.12. A versão Kotlin foi alinhada em 2.3.20 para o compilador embutido do AGP e os plugins Compose/Serialization. O projeto continua usando o Kotlin embutido do AGP, sem aplicar o plugin `org.jetbrains.kotlin.android`.

O script `gradlew` foi normalizado para finais de linha Unix; `gradlew.bat` foi preservado para Windows. A configuração de JDK 25 fornecida no projeto foi mantida. Se o Android Studio solicitar, instale os componentes de SDK/JDK indicados na sincronização.

## Verificação

A sintaxe Kotlin/Gradle, o catálogo TOML e suas referências, o Manifest e os pacotes foram conferidos. Foi tentado `./gradlew :app:assembleDebug :app:testDebugUnitTest`, mas o ambiente não conseguiu baixar a distribuição do Gradle (`Network is unreachable`). Portanto, não houve compilação nem execução em emulador aqui.

No Windows, para verificar após a sincronização, abra o terminal na pasta `ProjetoFinal`:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Depois execute o aplicativo e confira cadastro, limite de cinco cartões e persistência após reabertura. Para ver o layout sem cadastrar dados, use os previews de `TelasCartoes.kt` no Android Studio.
