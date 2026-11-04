package br.com.debcontrol.util;

import br.com.debcontrol.exception.RegraNegocioException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Hash de senha com PBKDF2-HMAC-SHA256 e sal aleatório, usando apenas o JDK.
 * Formato armazenado: pbkdf2-sha256$iteracoes$sal$hash (sal e hash em Base64).
 * Hashes SHA-256 antigos (64 caracteres hexadecimais) continuam verificáveis.
 */
public final class SenhaUtil {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "pbkdf2-sha256";
    private static final int ITERACOES = 600_000;
    private static final int TAMANHO_SAL = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final int TAMANHO_MINIMO = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtil() { }

    public static String hash(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO) {
            throw new RegraNegocioException("A senha deve possuir pelo menos " + TAMANHO_MINIMO + " caracteres.");
        }
        byte[] sal = new byte[TAMANHO_SAL];
        RANDOM.nextBytes(sal);
        Base64.Encoder base64 = Base64.getEncoder();
        return PREFIXO + "$" + ITERACOES + "$" + base64.encodeToString(sal)
                + "$" + base64.encodeToString(derivar(senha, sal, ITERACOES, TAMANHO_HASH_BITS));
    }

    public static boolean verificar(String senha, String armazenado) {
        if (senha == null || armazenado == null) return false;
        if (armazenado.matches("[0-9a-fA-F]{64}")) return verificarLegado(senha, armazenado);
        String[] partes = armazenado.split("\\$");
        if (partes.length != 4 || !PREFIXO.equals(partes[0])) return false;
        try {
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtido = derivar(senha, sal, Integer.parseInt(partes[1]), esperado.length * 8);
            return MessageDigest.isEqual(esperado, obtido);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean verificarLegado(String senha, String armazenado) {
        try {
            byte[] obtido = MessageDigest.getInstance("SHA-256").digest(senha.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(HexFormat.of().parseHex(armazenado), obtido);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }

    private static byte[] derivar(String senha, byte[] sal, int iteracoes, int tamanhoBits) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, tamanhoBits);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(ALGORITMO + " não está disponível.", exception);
        } finally {
            spec.clearPassword();
        }
    }
}
