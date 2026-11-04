package br.com.debcontrol.ui.components;

import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.exception.RegraNegocioException;

import javax.swing.*;
import java.awt.*;

public final class MessageBox {
    private MessageBox() {
    }

    public static void success(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, RuntimeException exception) {
        String message;
        if (exception instanceof RegraNegocioException || exception instanceof DatabaseException) {
            message = exception.getMessage();
            // A tela mostra uma mensagem amigável; a causa técnica fica no console para diagnóstico.
            if (exception.getCause() != null) exception.printStackTrace();
        } else {
            message = "Ocorreu um erro inesperado. Verifique os dados e tente novamente.";
            exception.printStackTrace();
        }
        JOptionPane.showMessageDialog(parent, message, "Não foi possível concluir", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
