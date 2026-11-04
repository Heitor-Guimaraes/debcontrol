# Padrão do debcontrol

```text
debcontrol/
  src/br/com/debcontrol/
    Main.java
    AppContext.java
    config/       configuração e conexão JDBC
    model/        entidades e enums
    dao/          SQL e mapeamento de resultados
    service/      regras de negócio
    dto/          resultados de consultas
    exception/    exceções da aplicação
    util/         utilitários gerais
    ui/
      MainFrame.java e MainFrame.form
      UiTheme.java
      WindowSizing.java
      panels/     painéis .java e .form
      dialogs/    diálogos .java e .form
      components/ componentes reutilizáveis
  test/br/com/debcontrol/test/
  config/       configuração do ambiente e modelos .example
  sql/          schema, dados demonstrativos e consultas
  docs/         evidências da Aula 11 e roteiro de entrega
  lib/          driver JDBC, adicionado localmente
  nbproject/    configuração compartilhada do NetBeans
  build.xml
```

Classes usam PascalCase; atributos e métodos, camelCase; constantes, MAIUSCULAS_COM_UNDERLINE; pacotes, letras minúsculas. Arquivos .java e .form de uma mesma tela ficam juntos. O pacote `br.com.debcontrol` e o banco `mercado_fiado` são preservados para compatibilidade; o produto se chama debcontrol.

`build/` e `dist/` são saídas geradas e não são distribuídas no código-fonte. Arquivos privados da IDE, senhas e caminhos locais de JDK não devem ser versionados. O ZIP contém configurações iniciais sem senha; configure-as no seu computador. Os modelos `.example` permitem recriá-las.

## Notebook e desktop

A janela principal respeita a área útil do monitor e a escala lógica informada pelo Java, com rolagem do menu e do conteúdo quando necessário. Os diálogos podem ser redimensionados e possuem rolagem. Layouts Swing são preservados nos arquivos .form; os adaptadores de rolagem e ajuste de janela ficam fora do código gerado.

Foram adicionados testes de cálculo para áreas de notebook, desktop, escala e monitor à esquerda do principal. Esses testes não substituem a verificação visual real no Windows e no NetBeans.
