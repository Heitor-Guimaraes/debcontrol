# Guia do projeto — debcontrol

## Tecnologias permitidas

- Java puro;
- Java Swing;
- JDBC (`java.sql`);
- MySQL 8;
- MySQL Connector/J apenas como driver do banco.

Não são usados Maven, Gradle, Spring, Hibernate, JPA, bibliotecas visuais ou frameworks.

## Funções implementadas

- cadastrar, editar, pesquisar e desativar clientes;
- cadastrar, editar, pesquisar e desativar funcionários;
- cadastrar e desativar formas de pagamento;
- registrar e cancelar vendas fiadas;
- registrar pagamentos parciais ou totais;
- atualizar vendas para `ABERTA`, `PARCIAL`, `QUITADA`, `ATRASADA` ou `CANCELADA`;
- registrar o histórico de cobranças e o próximo contato;
- consultar extrato, dashboard e relatório de devedores;
- exportar clientes devedores para CSV.

## Preparar o MySQL

Execute os arquivos nesta ordem no MySQL Workbench:

1. `sql/banco_mercado_fiado.sql` — cria o banco, as seis tabelas, chaves, regras e a view;
2. `sql/dados_demonstracao.sql` — opcional, insere exemplos;
3. `sql/consultas_teste.sql` — opcional, confere os registros.

O banco utilizado é `mercado_fiado`.

## Adicionar o único JAR necessário

1. Baixe o MySQL Connector/J no site oficial do MySQL ou use o comando `curl` indicado no `README.md`.
2. Coloque o arquivo na pasta `lib`.
3. Renomeie para `mysql-connector-j.jar`.

O driver é necessário porque o JDBC pertence ao Java, mas o driver específico do MySQL não acompanha o JDK.

## Configurar a conexão

Edite `config/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/mercado_fiado?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000
db.user=root
db.password=SUA_SENHA
```

Também podem ser usadas as variáveis `FIADO_DB_URL`, `FIADO_DB_USER` e `FIADO_DB_PASSWORD`.

## Abrir no Apache NetBeans

1. Use **File > Open Project**.
2. Selecione a pasta `debcontrol`, e não um arquivo Java isolado.
3. Cadastre o JDK 23 ou superior em **Tools > Java Platforms > Add Platform** e selecione-o em **Project Properties > Libraries > Java Platform**. Em **Sources**, confira o nível de código-fonte 23. Se essas opções não estiverem disponíveis, sua instalação do NetBeans precisa oferecer suporte a esse JDK.
4. Se o driver aparecer quebrado, use **Project Properties > Libraries** e selecione `lib/mysql-connector-j.jar`.
5. Execute com **Run Project**. A classe principal é `br.com.debcontrol.Main`.

### Erro release version 23 not supported

O compilador selecionado é anterior ao Java 23. Neste projeto, o Ant seleciona o JDK nesta ordem: `jdk.home` em `config/jdk.properties`, `JAVA_HOME`, Java que iniciou o Ant/NetBeans.

Abra `config/jdk.properties`, remova o comentário de `jdk.home` e informe o caminho real do seu JDK 23 ou superior. Exemplo, somente se esse diretório existir: `jdk.home=C:/Program Files/Java/jdk-23`. Não inclua `/bin` e não use aspas. Execute **Clean and Build** e **Run**. A saída mostrará o JDK selecionado.

Selecionar Java Platform nas propriedades da IDE, sozinho, não substitui a escolha explícita do JDK neste `build.xml` personalizado. Os arquivos `.bat` usam o compilador do PATH; para eles, configure PATH para o mesmo JDK.

## Requisito da aba Design e dos arquivos `.form`

O projeto contém 16 formulários NetBeans. Cada tela possui:

- um arquivo `NomeDaTela.java`;
- um arquivo `NomeDaTela.form` na mesma pasta;
- construtor público sem parâmetros;
- método `initComponents()` dentro do bloco `GEN-BEGIN`/`GEN-END`;
- declaração de componentes protegida pelo GUI Builder.

Telas disponíveis no Design:

- `MainFrame`;
- painéis `Dashboard`, `Clientes`, `Funcionarios`, `FormasPagamento`, `Vendas`, `Pagamentos`, `Cobrancas` e `Relatorios`;
- diálogos `Cliente`, `Funcionario`, `FormaPagamento`, `Venda`, `Pagamento`, `Cobranca` e `ExtratoCliente`.

Para abrir, localize a classe em **Source Packages**, dê dois cliques no `.java` e selecione a aba **Design** no topo do editor. Não apague o `.form`, não altere seu nome e não remova os blocos de código gerado.

## Organização

```text
Swing + .form -> Service -> DAO/JDBC -> MySQL
```

| Pacote | Responsabilidade |
|---|---|
| `model` | Classes com atributos, getters e setters |
| `ui` | Janelas, painéis e diálogos Swing |
| `service` | Validações e regras de negócio |
| `dao` | SQL e conversão de linhas em objetos Java |
| `config` | Configuração e abertura das conexões |
| `dto` | Dados resumidos de dashboard e relatórios |
| `util` | Datas, moedas, validação e hash de senha |

## Regras principais

1. CPF, telefone e nome do cliente são obrigatórios; o CPF é único e seus dígitos verificadores são conferidos.
2. Login do funcionário é único e sua senha é armazenada com PBKDF2 e sal. Isso não implementa autenticação; o cadastro de funcionários não restringe a abertura do sistema.
3. Cliente, funcionário e forma de pagamento desativados não podem participar de novos registros.
4. Venda deve ter valor positivo, descrição e vencimento entre a data da venda e um mês depois.
5. Pagamento deve ser positivo, não pode estar no futuro nem ultrapassar o saldo da venda.
6. Pagamento parcial muda o status para `PARCIAL` ou mantém `ATRASADA`; quitação muda para `QUITADA`.
7. Venda com pagamento não pode ser cancelada.
8. Cobrança só pode ser registrada para venda pendente e por funcionário ativo.
9. Valores usam `BigDecimal`; saldos são calculados, não digitados.
10. Operações financeiras não são excluídas fisicamente.

## Roteiro de teste manual

1. Cadastre um cliente, um funcionário e uma forma de pagamento.
2. Registre uma venda com vencimento em até um mês.
3. Registre um pagamento menor que o saldo e confirme `PARCIAL`.
4. Registre o restante e confirme `QUITADA`.
5. Crie outra venda pendente e registre uma cobrança.
6. Confira extrato, dashboard, histórico de pagamentos e relatório CSV.

Os dados demonstrativos incluem funcionários com senhas `Senha123` e `Senha456`; atualmente o projeto gerencia funcionários, mas não exige login para abrir a aplicação.

## Visual básico

A interface usa Swing com menu lateral azul-marinho, ações em azul e indicadores em azul, verde, roxo e laranja. O fundo é claro e as tabelas e formulários permanecem simples. As telas e seus arquivos `.form` estão em `src/br/com/debcontrol/ui`.

As barras de pesquisa usam azul suave, os formulários usam branco e as tabelas têm linhas alternadas e seleção contrastante. Nos formulários de cadastro e registro, Enter aciona o botão principal e Esc fecha a janela sem gravar alterações pendentes. Tab permite navegar entre campos e botões. Os campos de data e valor possuem dicas de preenchimento. As cores de fundo também estão persistidas nos arquivos `.form`.

## Limites de validação

A compilação usa Java 23 (`--release 23`) no NetBeans, no Ant e nos scripts; é necessário JDK 23 ou superior.

O pacote pode ser compilado e os testes locais podem ser executados sem MySQL. Os testes não substituem a execução real dos cadastros e pagamentos com o banco configurado. A abertura na aba Design deve ser conferida na instalação do NetBeans do aluno.

Os scripts de criação não são migrações: `CREATE TABLE IF NOT EXISTS` não altera tabelas antigas. Use uma base nova para a primeira execução ou compare o schema existente antes de importar.
