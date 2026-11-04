package br.com.debcontrol;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.ui.MainFrame;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;

import javax.swing.*;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--terminal".equals(args[0])) {
            br.com.debcontrol.ui.terminal.TerminalApp.main(args);
            return;
        }
        // Erros não tratados em ações de botão viram mensagem, em vez de falhar sem aviso ao usuário.
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            if (java.awt.GraphicsEnvironment.isHeadless()) {
                error.printStackTrace();
                return;
            }
            SwingUtilities.invokeLater(() -> MessageBox.error(null,
                    error instanceof RuntimeException runtime ? runtime : new RuntimeException(error)));
        });
        UiTheme.install();
        SwingUtilities.invokeLater(() -> {
            try {
                ConnectionFactory.testConnection();
                new MainFrame(new AppContext()).setVisible(true);
            } catch (RuntimeException exception) {
                MessageBox.error(null, exception);
                System.exit(1);
            }
        });
    }
}
