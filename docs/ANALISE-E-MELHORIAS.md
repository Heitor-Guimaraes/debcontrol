# Análise do projeto e melhorias aplicadas

Revisão de 04/11/2026 de todo o código, SQL, scripts e documentação. As restrições da disciplina foram mantidas: Java puro, Swing, JDBC, sem frameworks e com os formulários do NetBeans intactos (o único `.form` alterado foi `MainFrame`, para incluir o botão **Testar conexão**, atualizado junto com o `.java`).

## Corrigido

### Segurança e dados

| Problema | Consequência | Correção |
| --- | --- | --- |
| Senhas em SHA-256 sem sal | Senhas iguais geravam hashes iguais, fáceis de quebrar por dicionário | PBKDF2-HMAC-SHA256 com sal e 600 mil iterações (`SenhaUtil`), usando só o JDK; hashes antigos continuam verificáveis; mínimo de 8 caracteres |
| CPF validado só pelo tamanho | `11111111111` era aceito | Conferência dos dígitos verificadores (`Validador.cpfValido`) |
| `dados_demonstracao.sql` usava `ON DUPLICATE KEY UPDATE` | Executado em um banco em uso, sobrescrevia silenciosamente os registros de ID 1 a 5 | `INSERT IGNORE`, CPFs de exemplo válidos e aviso no script |
| Exportação CSV | Campos com `;` eram alterados, nomes iniciados por `=` viravam fórmula no Excel, acentos quebravam e o saldo não podia ser somado | Aspas conforme o padrão CSV, proteção contra fórmulas, BOM UTF-8 e saldo numérico |
| Exportar sobre arquivo existente | Substituía o arquivo sem avisar | Pede confirmação |

### Defeitos

| Problema | Consequência | Correção |
| --- | --- | --- |
| Editar funcionário ou forma de pagamento sem selecionar a linha | Exceção não tratada e nenhuma mensagem na tela | Mensagem de erro, como já ocorria em Clientes |
| Formulário alterava o objeto da lista antes de salvar | Dados rejeitados reapareciam ao reabrir o formulário | A edição carrega um registro novo do banco |
| Falha ao ler a configuração em um inicializador `static` | A aplicação não abria e não mostrava erro | Leitura tardia em `ConnectionFactory` |
| Falta do driver JDBC | Mensagem genérica de conexão | Mensagem indicando `lib/mysql-connector-j.jar` |
| Erro de banco ao abrir "Nova venda" ou "Nova cobrança" | Exceção não tratada e nenhuma mensagem | Tratador global em `Main` |
| Forma de pagamento com nome repetido | Erro genérico vindo do banco | Regra de negócio com mensagem clara |
| `java -jar dist/debcontrol.jar` | O driver não entrava no classpath e a conexão falhava | `Class-Path` no manifesto |
| Senha do banco passava por `trim()` | Senhas com espaço nas pontas falhavam | A senha é lida sem alteração |
| Formulário de edição marcava a senha como obrigatória | Sugeria redigitar a senha, embora ela seja opcional | Rótulo "Nova senha (opcional)" |

### Desempenho e robustez

- **Tela de clientes:** abria uma conexão por cliente para buscar o saldo (N+1). Agora faz uma única consulta.
- **Conexão:** a URL padrão ganhou `connectTimeout=5000`, para a tela não ficar presa com o servidor fora do ar. Saiu `serverTimezone=UTC`, que convertia os horários gravados.
- **Bloqueio de pagamento:** `buscarPorId(connection, id)` só bloqueava a linha dependendo do estado da conexão. Agora a intenção está explícita em `buscarParaPagamento`.
- **Causa dos erros:** a mensagem da tela continua amigável, e a causa técnica vai para o console.
- **Código sem uso removido:** `calcularTotalPago` e validações repetidas em `FinanceiroService`.

### Scripts, testes e documentação

- **Scripts:** a compilação estava repetida em quatro `.bat` e agora fica em `compilar.bat`, com `--release 23`. `testar.bat` escondia o código de saída com `pause` e agora o preserva.
- **Testes:** os de integração sobrescreviam as evidências em `docs/aula12` a cada execução. Agora usam `build/test-output`. Foram adicionados casos para senhas, CPF, CSV e nome repetido (de 28 para 35 testes locais).
- **Documentação:** os textos pediam JDK 25, mas o projeto compila com JDK 23+. Os roteiros, o guia e o formulário técnico foram alinhados, e o README foi reescrito.
- **Repositório:** foi adicionado `.gitattributes`.

## Atenção ao usar

- **Clientes antigos com CPF inválido** continuam no banco, mas precisam ter o CPF corrigido na próxima edição.
- **Seu `config/database.properties`** não foi alterado e ainda contém `serverTimezone=UTC`. Para adotar a nova URL, copie-a de `config/database.properties.example`.
- **Senhas de funcionários já cadastrados** seguem em SHA-256 até serem alteradas.

## Próximos passos recomendados

Não foram feitos porque mudam o comportamento do sistema, exigem alterar telas no Design do NetBeans ou dependem de uma decisão sua.

1. **Login:** o cadastro guarda senhas, mas o sistema abre sem autenticação. `SenhaUtil.verificar` já está pronto para isso.
2. **Consultas fora da thread da interface:** todo acesso ao banco roda na thread do Swing, e a tela congela se o banco estiver lento. `SwingWorker` resolve sem bibliotecas externas.
3. **Reativar cadastros:** é possível desativar clientes, funcionários e formas de pagamento, mas não reativar.
4. **Hora real:** vendas e pagamentos são gravados à meia-noite, porque a tela só pede a data. Isso impede saber a ordem exata no dia.
5. **Atualização de atrasadas:** `atualizarAtrasadas` grava no banco durante a listagem. O ideal é calcular o atraso na consulta ou executar a atualização uma vez ao abrir o sistema.
6. **Usuário do banco:** criar um usuário próprio com permissões limitadas, em vez de `root`, e habilitar SSL se o banco sair da máquina local.
