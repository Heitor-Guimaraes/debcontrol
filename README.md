# debcontrol

Aplicação desktop para controle de vendas fiadas: clientes, funcionários, formas de pagamento, vendas, pagamentos parciais, cobranças, dashboard e relatório de devedores.

Feita com **Java 23+, Swing, JDBC e MySQL**, sem frameworks. O único JAR externo é o driver MySQL Connector/J. As 16 telas podem ser editadas na aba **Design** do Apache NetBeans (`.java` + `.form`).

## Como executar

1. Instale o JDK 23 ou superior e um servidor MySQL 8 ou MariaDB.
2. Baixe o driver e salve como `lib/mysql-connector-j.jar`. Ele não é versionado:
   ```
   curl -L -o lib/mysql-connector-j.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar
   ```
3. Execute `sql/banco_mercado_fiado.sql` para criar o banco. `sql/dados_demonstracao.sql` é opcional.
4. Copie `config/database.properties.example` para `config/database.properties` e informe usuário e senha. Esse arquivo não é versionado. Também é possível usar as variáveis `FIADO_DB_URL`, `FIADO_DB_USER` e `FIADO_DB_PASSWORD`.
5. Abra `ABRIR-DEBCONTROL.bat`, ou `terminal.bat` para o menu de CRUD no terminal.

No NetBeans, use **File > Open Project** e selecione esta pasta. Detalhes em [GUIA-DO-PROJETO.md](GUIA-DO-PROJETO.md).

## Testes

| Script | O que valida | Precisa de banco |
| --- | --- | --- |
| `testar.bat` | Regras, validações, CSV, senhas e construção das telas | Não |
| `testar-integracao.bat` | CRUD de cliente; grava e remove um registro temporário | Sim |
| `testar-sistema.bat` | Fluxo financeiro completo em um banco temporário, removido ao final | Sim |
| `testar-sistema.bat --gui` | O mesmo, acionando os botões reais das telas | Sim, e monitor |

Os arquivos gerados pelos testes ficam em `build/test-output`.

## Estrutura

```text
Swing / Terminal -> service (regras) -> dao (SQL/JDBC) -> MySQL
```

Pacotes e convenções em [ESTRUTURA-DO-PROJETO.md](ESTRUTURA-DO-PROJETO.md) e modelo em [DIAGRAMA-DE-CLASSES.md](DIAGRAMA-DE-CLASSES.md).

## Documentos

- [Guia do projeto](GUIA-DO-PROJETO.md)
- [Análise do projeto e melhorias aplicadas](docs/ANALISE-E-MELHORIAS.md)

## Limitações conhecidas

O cadastro de funcionários não implementa login: o sistema abre sem autenticação. A conexão padrão usa `useSSL=false` e é destinada apenas a um banco local. Outros pontos estão em [próximos passos](docs/ANALISE-E-MELHORIAS.md#próximos-passos-recomendados).
