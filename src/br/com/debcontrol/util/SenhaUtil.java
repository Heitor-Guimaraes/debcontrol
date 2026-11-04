package br.com.debcontrol.util;

import br.com.debcontrol.exception.RegraNegocioException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class SenhaUtil {
    private SenhaUtil() { }

    public static String hash(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new RegraNegocioException("A senha deve possuir pelo menos 6 caracteres.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(senha.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }
}
