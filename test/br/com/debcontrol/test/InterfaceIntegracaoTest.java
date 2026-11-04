package br.com.debcontrol.test;

import br.com.debcontrol.AppContext;
import br.com.debcontrol.ui.*;
import br.com.debcontrol.ui.dialogs.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/** Dispara os listeners reais dos botoes Swing e verifica a persistencia real. */
public final class InterfaceIntegracaoTest {
    private static int checks;
    private static MainFrame frame;
    private static AppContext app;
    private static final Path PASTA = Path.of("docs/aula12/testes-interface");

    public static void main(String[] args) throws Exception {
        Thread watchdog = new Thread(() -> {
            try { Thread.sleep(60000); } catch (InterruptedException e) { return; }
            System.err.println("FALHA: interface excedeu 60 segundos."); System.exit(2);
        });
        watchdog.setDaemon(true); watchdog.start();
        try {
            Files.createDirectories(PASTA);
            SwingUtilities.invokeAndWait(() -> {
                UiTheme.install(); app = new AppContext(); frame = new MainFrame(app); frame.setVisible(true);
                try { executar(); }
                finally { for (Window w : Window.getWindows()) w.dispose(); }
            });
            System.out.println("APROVADOS: " + checks + " verificacoes de interface com listeners e banco reais.");
        } finally { watchdog.interrupt(); }
    }

    private static void executar() {
        Object clientes = navegar("clientsButton", "clientesPanel");
        modal(() -> click(clientes, "newButton"), ClienteDialog.class, d -> {
            texto(d,"nameField","Cliente Interface"); texto(d,"cpfField","52998224725");
            texto(d,"phoneField","11999999999"); texto(d,"addressField","Teste visual");
            foto(d,"01-formulario-cliente.png"); click(d,"saveButton");
        });
        ok(app.clienteService().listar("Interface",true).size()==1,"Novo cliente: botao abre formulario e Salvar grava no banco");
        selecionar(clientes,"table");
        modal(() -> click(clientes,"editButton"), ClienteDialog.class, d -> {
            texto(d,"nameField","Cliente Interface Editado"); click(d,"saveButton");
        });
        ok(app.clienteService().listar("Editado",true).size()==1,"Editar cliente atualiza banco e tabela");
        modal(() -> click(clientes,"newButton"),ClienteDialog.class,d -> click(d,"cancelButton"));
        ok(app.clienteService().listar("",false).size()==1,"Cancelar formulario nao grava cliente");
        Object funcionarios = navegar("employeesButton","funcionariosPanel");
        modal(() -> click(funcionarios,"newButton"),FuncionarioDialog.class,d -> {
            texto(d,"nameField","Operador Interface"); texto(d,"roleField","Caixa");
            texto(d,"loginField","operador_ui"); texto(d,"passwordField","Teste123"); click(d,"saveButton");
        });
        ok(app.funcionarioService().listar("",true).size()==1,"Formulario de funcionario grava no banco");
        selecionar(funcionarios,"table");
        modal(() -> click(funcionarios,"editButton"),FuncionarioDialog.class,d -> { texto(d,"roleField","Gerente"); click(d,"saveButton"); });
        ok(app.funcionarioService().listar("",true).getFirst().getCargo().equals("Gerente"),"Editar funcionario pelo botao");
        Object formas = navegar("paymentMethodsButton","formasPagamentoPanel");
        modal(() -> click(formas,"newButton"),FormaPagamentoDialog.class,d -> {
            texto(d,"nameField","Dinheiro UI"); texto(d,"descriptionField","Teste interface"); click(d,"saveButton");
        });
        ok(app.formaPagamentoService().listar("",true).size()==1,"Formulario de forma de pagamento grava no banco");
        selecionar(formas,"table");
        modal(() -> click(formas,"editButton"),FormaPagamentoDialog.class,d -> { texto(d,"descriptionField","Editada UI"); click(d,"saveButton"); });
        ok(app.formaPagamentoService().listar("",true).getFirst().getDescricao().equals("Editada UI"),"Editar forma de pagamento pelo botao");
        Object vendas = navegar("salesButton","vendasPanel");
        modal(() -> click(vendas,"newButton"),VendaDialog.class,d -> {
            texto(d,"valueField","100,00"); texto(d,"descriptionArea","Venda pela interface");
            foto(d,"02-formulario-venda.png"); click(d,"saveButton");
        });
        ok(app.financeiroService().listarVendas("",true).size()==1,"Nova venda: combos carregados e Salvar grava venda");
        Object cobrancas = navegar("collectionsButton","cobrancasPanel");
        modal(() -> click(cobrancas,"newButton"),CobrancaDialog.class,d -> { texto(d,"resultField","Cliente avisado UI"); click(d,"saveButton"); });
        ok(app.cobrancaService().listar("").size()==1,"Registrar cobranca pelo formulario");
        Object pagamentos = navegar("paymentsButton","pagamentosPanel"); selecionar(pagamentos,"debtsTable");
        modal(() -> click(pagamentos,"payButton"),PagamentoDialog.class,d -> {
            texto(d,"valueField","40,00"); foto(d,"03-formulario-pagamento.png"); click(d,"saveButton");
        });
        ok(app.financeiroService().listarVendas("",true).getFirst().getSaldo().intValueExact()==60,"Registrar pagamento pelo botao atualiza saldo para 60");
        navegar("clientsButton","clientesPanel"); selecionar(clientes,"table");
        modal(() -> click(clientes,"statementButton"),ExtratoClienteDialog.class,d -> {
            ok(((JTable)campo(d,"table")).getRowCount()==1,"Extrato abre com venda do cliente"); foto(d,"04-extrato.png"); click(d,"closeButton");
        });
        Object relatorios = navegar("reportsButton","relatoriosPanel"); click(relatorios,"calculateButton");
        ok(((JLabel)campo(relatorios,"totalValue")).getText().contains("40,00"),"Calcular periodo mostra recebido correto");
        Path csv = PASTA.resolve("exportacao-interface.csv").toAbsolutePath();
        try { Files.deleteIfExists(csv); } catch (Exception e) { throw new RuntimeException(e); }
        modal(() -> click(relatorios,"exportButton"),JDialog.class,d -> {
            JFileChooser chooser = buscar(d,JFileChooser.class);
            if (chooser==null) throw new AssertionError("Seletor de arquivo nao abriu");
            chooser.setSelectedFile(csv.toFile());
            javax.swing.Timer fecharSucesso = new javax.swing.Timer(200,e -> {
                for(Window w:Window.getWindows()) if(w instanceof JDialog j && j.isShowing() && j.getTitle().equals("Sucesso")) {
                    j.dispose(); ((javax.swing.Timer)e.getSource()).stop();
                }
            });
            fecharSucesso.start();
            chooser.approveSelection();
        });
        ok(Files.exists(csv),"Exportar abre seletor e grava CSV pelo botao");
        navegar("dashboardButton","dashboardPanel"); foto(frame,"05-dashboard-conectado.png");
        for(String nome : new String[]{"clientesPanel","funcionariosPanel","formasPagamentoPanel","vendasPanel","pagamentosPanel","cobrancasPanel","relatoriosPanel"}) {
            Object painel=campo(frame,nome);
            for(var field:painel.getClass().getDeclaredFields()) if(JButton.class.isAssignableFrom(field.getType())) {
                JButton botao=(JButton)campo(painel,field.getName());
                ok(botao.getActionListeners().length>0,painel.getClass().getSimpleName()+"."+field.getName()+" tem acao conectada");
            }
        }
    }
    private static Object navegar(String botao,String painel) { click(frame,botao); return campo(frame,painel); }
    private static Object campo(Object obj,String nome) {
        try { var f=obj.getClass().getDeclaredField(nome); f.setAccessible(true); return f.get(obj); }
        catch(Exception e){ throw new RuntimeException(e); }
    }
    private static void click(Object obj,String nome){ ((AbstractButton)campo(obj,nome)).doClick(); }
    private static void texto(Object obj,String nome,String valor){ ((JTextComponent)campo(obj,nome)).setText(valor); }
    private static void selecionar(Object obj,String nome){ ((JTable)campo(obj,nome)).setRowSelectionInterval(0,0); }
    private static <T extends Component> T buscar(Container c,Class<T> tipo){
        for(Component child:c.getComponents()) { if(tipo.isInstance(child))return tipo.cast(child); if(child instanceof Container cc){T r=buscar(cc,tipo);if(r!=null)return r;} } return null;
    }
    private static void modal(Runnable abrir,Class<? extends JDialog> tipo,Consumer<JDialog> agir){
        AtomicReference<Throwable> erro=new AtomicReference<>();
        boolean[] encontrou={false};
        javax.swing.Timer timer=new javax.swing.Timer(180,null);
        timer.addActionListener(e -> {
            for(Window w:Window.getWindows()) if(tipo.isInstance(w)&&w.isShowing()) {
                timer.stop(); encontrou[0]=true;
                try { agir.accept((JDialog)w); } catch(Throwable t){ erro.set(t); for(Window j:Window.getWindows())if(j instanceof JDialog)j.dispose(); }
                break;
            }
        });
        timer.start(); try { abrir.run(); } finally { timer.stop(); }
        if(erro.get()!=null)throw new AssertionError("Falha no formulario",erro.get());
        if(!encontrou[0])throw new AssertionError("Formulario esperado nao abriu: "+tipo.getSimpleName());
    }
    private static void foto(Window window,String nome){
        try { window.validate(); layout(window); BufferedImage image=new BufferedImage(window.getWidth(),window.getHeight(),BufferedImage.TYPE_INT_RGB); var g=image.createGraphics();try{window.printAll(g);}finally{g.dispose();}javax.imageio.ImageIO.write(image,"png",PASTA.resolve(nome).toFile()); }
        catch(Exception e){throw new RuntimeException(e);}
    }
    private static void layout(Container c){c.doLayout();for(Component child:c.getComponents())if(child instanceof Container cc)layout(cc);}
    private static void ok(boolean condition,String nome){if(!condition)throw new AssertionError(nome);checks++;System.out.println("[OK] "+nome);}
}
