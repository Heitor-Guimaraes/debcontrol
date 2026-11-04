package br.com.debcontrol.util;

import br.com.debcontrol.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class Formatador {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Formatador() {
    }

    public static String moeda(BigDecimal value) {
        return MOEDA.format(value == null ? BigDecimal.ZERO : value);
    }

    public static String data(LocalDate value) {
        return value == null ? "" : DATA.format(value);
    }

    public static String data(LocalDateTime value) {
        return value == null ? "" : DATA.format(value);
    }

    public static LocalDate parseData(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RegraNegocioException(fieldName + " é obrigatório.");
        }
        try {
            return LocalDate.parse(value.trim(), DATA);
        } catch (DateTimeParseException exception) {
            throw new RegraNegocioException(fieldName + " deve estar no formato dd/MM/aaaa.");
        }
    }

    public static BigDecimal parseMoeda(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RegraNegocioException(fieldName + " é obrigatório.");
        }
        String input = value.trim().replaceFirst("^R\\$\\s*", "").trim();
        if (!input.matches("-?(?:[0-9]+|[0-9]{1,3}(?:\\.[0-9]{3})+)(?:,[0-9]{1,2})?")) {
            throw new RegraNegocioException(fieldName + " deve usar o formato brasileiro, por exemplo 1.234,56.");
        }
        String normalized = input.replace(".", "").replace(",", ".");
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException exception) {
            throw new RegraNegocioException(fieldName + " deve conter um valor válido.");
        }
    }
}
