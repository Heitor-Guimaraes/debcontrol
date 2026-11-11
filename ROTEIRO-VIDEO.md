# Roteiro do vídeo – debcontrol (~5 min)

## Antes de gravar (2 min de preparo)

1. XAMPP: confirme que o **MySQL** está rodando (o serviço `mysql` já está ativo nesta máquina).
2. **MySQL Workbench**: conexão `localhost:3306`, usuário `root`, sem senha. Abra `sql/video_demonstracao.sql`.
3. **NetBeans**: File > Open Project > `Documents\debcontrol`. Clique com o direito no projeto > **Clean and Build** (deve dar BUILD SUCCESSFUL).
4. Lado a lado: NetBeans com **Win + ←** e Workbench com **Win + →**.
5. Abra `ClientesPanel.java` no editor do NetBeans. Como o projeto acabou de ser clonado, a aba **History** pode aparecer com poucas versões. Antes de gravar, acrescente um comentário (ex.: `// tela de clientes`), salve com Ctrl+S, apague o comentário e salve de novo. Assim o histórico local terá versões para mostrar, além das versões do Git.
6. Para gravar: **Win + Shift + R** (Ferramenta de Captura), marque a tela inteira e **ligue o microfone**. Para parar, use o botão da barra de captura.

Dados usados no teste: **Maria Teste**, CPF **529.982.247-25**, telefone **(11) 91234-5678**, endereço **Rua Teste, 10**.

---

## 0:00 – 0:30 · Abertura

> "Este é o debcontrol, um sistema de controle de vendas fiadas feito em Java Swing com JDBC e banco MySQL. À esquerda está o NetBeans, à direita o MySQL Workbench conectado ao banco `mercado_fiado`."

- No Workbench, execute a consulta **[0]** e mostre os 4 clientes.

## 0:30 – 1:30 · As 3 abas (Source, Design e History)

Use o arquivo `ClientesPanel.java`:

- **Source**: role até o `initComponents` e os métodos dos botões.
  > "Na aba Source fica o código: aqui os botões Novo, Editar e Excluir chamam a camada service, que chama o DAO com o SQL."
- **Design**: clique em um botão (ex.: *Excluir*) e mostre a janela **Properties**.
  > "Na aba Design a tela é montada visualmente, pelo arquivo .form do NetBeans."
- **History**: mostre a lista de versões e clique em uma para ver a diferença.
  > "Na aba History fica o histórico local e do Git, com as alterações feitas no arquivo ao longo do projeto."
- Se sobrar tempo, mostre também `ClienteDialog.java` na aba Design.

## 1:30 – 1:50 · Executar e conectar

- Clique em **Run Project (F6)**. Coloque a janela do debcontrol no lado esquerdo.
- Clique em **Testar conexão**.
  > "A conexão com o MySQL foi realizada; a mensagem mostra o servidor, o banco e o tempo de resposta."
- Clique em **Clientes**.

## 1:50 – 2:40 · INCLUSÃO

- Clique em **Novo cliente**, preencha os dados da Maria Teste e clique em **Salvar cliente**.
- No Workbench, execute a consulta **[1]**.
  > "Cadastrei no Java e, no SELECT, a Maria aparece gravada no banco com o ID gerado automaticamente."

## 2:40 – 3:10 · CONSULTA

- No campo **Buscar**, digite `Maria` e clique em **Pesquisar**.
- No Workbench, execute a consulta **[2]**.
  > "A pesquisa na tela retorna o mesmo resultado do SELECT com LIKE no banco."

## 3:10 – 3:50 · ALTERAÇÃO

- Selecione a Maria, clique em **Editar**, troque o telefone para `(11) 99999-0000` e o endereço para `Rua Alterada, 20` e salve.
- No Workbench, execute a consulta **[3]**.
  > "O UPDATE foi feito: o telefone e o endereço novos já aparecem no banco."

## 3:50 – 4:20 · EXCLUSÃO

- Selecione a Maria, clique em **Excluir** e confirme.
- No Workbench, execute a consulta **[4]**.
  > "O SELECT volta vazio: o registro foi removido com DELETE e o total voltou a 4 clientes."

## 4:20 – 4:50 · Falha tratada e como resolver

- Selecione **Ana Souza** e clique em **Excluir**. Vai aparecer a mensagem de bloqueio.
- No Workbench, execute a consulta **[5]**.
  > "Aqui o sistema não deixa excluir, porque a Ana tem vendas registradas. A tabela `venda_fiada` tem uma chave estrangeira para o cliente; se apagasse, o histórico financeiro ficaria inconsistente. A solução é o botão **Desativar**, que só marca `ativo = 0` e preserva o histórico."
- (Opcional) Clique em **Desativar** e rode o SELECT de novo para mostrar `ativo = 0`.

## 4:50 – 5:10 · Encerramento

> "Então as interfaces estão finalizadas e conectadas ao MySQL, com inclusão, consulta, alteração e exclusão conferidas em tempo real pelo Workbench. Como próximo passo, pretendo implementar o login dos funcionários, que hoje ainda não existe."

---

## Se der erro durante a gravação (o que falar e como resolver)

| Erro na tela | Causa | Solução |
| --- | --- | --- |
| "Driver do MySQL não encontrado" | Falta `lib/mysql-connector-j.jar` | O JAR já foi baixado; confira em Properties > Libraries |
| "Não foi possível conectar ao MySQL" | MySQL parado no XAMPP | Dar Start no MySQL do XAMPP e testar de novo |
| "CPF inválido" | O dígito verificador não confere | Usar um CPF válido (ex.: 529.982.247-25) |
| "Já existe um cliente cadastrado com este CPF" | A Maria não foi excluída numa tentativa anterior | Excluir a Maria ou usar outro CPF |
| Workbench não mostra a mudança | Consulta antiga na tela | Executar o SELECT de novo (Ctrl+Enter) |
| Aba Design não abre | O NetBeans não leu o `.form` | Fechar e reabrir o arquivo; o `.form` está no projeto |
