# PetPass

Aplicativo Android para manter em um só lugar as informações importantes de cada pet: identificação, vacinas, exames, peso e próximos cuidados.

## Funcionalidades

- Cadastro e edição de pets e geração de RG digital com QR Code.
- Carteirinha de vacinas, histórico de exames e acompanhamento de peso.
- Lembretes na tela inicial para reforços de vacina e exames agendados.
- Compartilhamento do RG do pet em texto, imagem e PDF.

Os próximos cuidados são exibidos dentro do aplicativo. O app ainda não envia notificações do sistema.

## Tecnologias

- Kotlin e Jetpack Compose
- Room para armazenamento local
- Navigation Compose
- ZXing para geração de QR Code

## Requisitos

- Android Studio com suporte ao Android Gradle Plugin do projeto.
- JDK 17 ou superior.
- Android SDK 37.

## Compilar e testar

Na raiz do projeto:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

O APK de debug é gerado em `app/build/outputs/apk/debug/`.

## Gerar bundle de release

O identificador do aplicativo é `com.leandro.petpass`. A chave de upload é necessária para produzir um bundle assinado para a Play Store. Gere uma chave e mantenha uma cópia segura:

```bash
keytool -genkeypair -v \
  -keystore petpass-upload.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias petpass-upload
```

Não versione o arquivo da chave nem suas senhas. Configure as propriedades abaixo no `~/.gradle/gradle.properties` (fora do repositório):

```properties
PETPASS_STORE_FILE=/caminho/seguro/petpass-upload.jks
PETPASS_STORE_PASSWORD=sua-senha-do-keystore
PETPASS_KEY_ALIAS=petpass-upload
PETPASS_KEY_PASSWORD=sua-senha-da-chave
```

Então gere e teste o Android App Bundle:

```bash
./gradlew testDebugUnitTest bundleRelease
```

O AAB é gerado em `app/build/outputs/bundle/release/`. A tarefa `bundleRelease` falha explicitamente se as quatro propriedades de assinatura não estiverem configuradas. Para distribuição pela Play Store, ative o Play App Signing no Play Console e faça upload do AAB assinado com essa chave de upload.

## Checklist antes de publicar

- [ ] Confirmar que `applicationId` é o identificador definitivo; não pode ser alterado após a publicação.
- [ ] Definir um `versionCode` maior que o de qualquer versão já enviada e atualizar `versionName`.
- [ ] Configurar a chave de upload em ambiente seguro e guardar backup.
- [ ] Testar o AAB assinado em uma faixa de teste interno no Play Console.
- [ ] Preparar nome, descrição curta e completa, capturas de tela, ícone de 512 × 512 e imagem de destaque da ficha da loja.
- [ ] Revisar [a política de privacidade](docs/index.md), publicar o site pelo GitHub Pages usando a pasta `/docs` e cadastrar a URL pública na ficha da loja.
- [ ] Preencher Segurança dos dados, classificação indicativa e declaração de anúncios no Play Console de acordo com o comportamento real do app.
- [ ] Revisar requisitos atuais de nível de API alvo, acesso à conta de desenvolvedor e testes exigidos pelo Google Play antes do envio para produção.
- [ ] Verificar o fluxo de backup e restauração e as declarações correspondentes no Play Console.

O projeto está configurado para compilar um AAB de release assinado quando as credenciais forem fornecidas. A publicação ainda depende da configuração da conta Play Console, da chave de upload, dos materiais da ficha e das declarações/política de privacidade.
