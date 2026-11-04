package br.com.debcontrol.util;

import br.com.debcontrol.exception.RegraNegocioException;

public final class Validador {
    public static String textoObrigatorio(String value, String fieldName, int maxLength) {
        String text = textoObrigatorio(value, fieldName);
        if (text.codePointCount(0, text.length()) > maxLength) {
            throw new RegraNegocioException(fieldName + " deve ter no máximo " + maxLength + " caracteres.");
        }
        return text;
    }

    public static String textoOpcional(String value, String fieldName, int maxLength) {
        String text = textoOpcional(value);
        return text == null ? null : textoObrigatorio(text, fieldName, maxLength);
    }

    public static void valorMonetario(java.math.BigDecimal value) {
        if (value == null || value.signum() <= 0 || value.compareTo(new java.math.BigDecimal("99999999.99")) > 0
                || Math.max(0, value.stripTrailingZeros().scale()) > 2) {
            throw new RegraNegocioException("Informe um valor entre 0,01 e 99.999.999,99, com até duas casas decimais.");
        }
    }

    /** Confere tamanho e dígitos verificadores de um CPF contendo somente números. */
    public static boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("[0-9]{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digitoCpf(cpf, 9) == cpf.charAt(9) - '0' && digitoCpf(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoCpf(String cpf, int quantidade) {
        int soma = 0;
        for (int indice = 0; indice < quantidade; indice++) {
            soma += (cpf.charAt(indice) - '0') * (quantidade + 1 - indice);
        }
        int resto = soma * 10 % 11;
        return resto == 10 ? 0 : resto;
    }

    private Validador() {
    }

    public static String textoObrigatorio(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RegraNegocioException(fieldName + " é obrigatório.");
        }
        return value.trim();
    }

    public static String somenteNumerosOuNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }

    public static String textoOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
