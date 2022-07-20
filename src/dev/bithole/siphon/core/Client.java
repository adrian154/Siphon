package dev.bithole.siphon.core;

import org.bouncycastle.crypto.generators.SCrypt;

import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class Client {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final MessageDigest KEY_DIGEST;
    private static final int KEY_LEN = 32;
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    static {
        try {
            KEY_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            throw new AssertionError("How the heck does your environment not support SHA-256??");
        }
    }

    public final String name;
    private String passwordHash;
    private String salt;
    private String keyHash;
    private Set<String> permissions;

    private transient byte[] passwordHashBuf;
    private transient byte[] saltBuf;
    private transient byte[] keyHashBuf;

    private Client(String name) {
        this.name = name;
        this.permissions = new HashSet<>();
    }


    public Client(String name, String password) {
        this(name);
        this.saltBuf = new byte[16];
        RANDOM.nextBytes(saltBuf);
        this.passwordHashBuf = hashPassword(password);
        this.passwordHash = new String(BASE64_ENCODER.encode(passwordHashBuf));
        this.salt = new String(BASE64_ENCODER.encode(saltBuf));
    }

    public Client(String name, byte[] key) {
        this(name);
        this.keyHashBuf = KEY_DIGEST.digest(key);
        this.keyHash = new String(BASE64_ENCODER.encode(keyHashBuf));
    }

    // This method should be called after the object is deserialized to fill in the transient fields
    public void revive() throws InvalidObjectException {

        if(name == null) {
            throw new InvalidObjectException("Encountered a client with no name");
        }

        if(permissions == null) {
            permissions = new HashSet<>();
        }

        if(passwordHash != null && salt != null) {
            passwordHashBuf = BASE64_DECODER.decode(passwordHash);
            saltBuf = BASE64_DECODER.decode(salt);
        } else if(keyHash != null) {
            keyHashBuf = BASE64_DECODER.decode(keyHash);
        } else {
            throw new InvalidObjectException(String.format("Neither a password hash nor a key hash was provided for client \"%s\"", name));
        }

    }

    private byte[] hashPassword(String password) {
        return SCrypt.generate(password.getBytes(StandardCharsets.UTF_8), saltBuf, 262144, 8, 1, 32);
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void deletePermission(String permission) {
        this.permissions.remove(permission);
    }

    public boolean testPermission(String permission) {

        if(permissions.contains(permission)) return true;

        // check wildcards
        String[] parts = permission.split("\\.");
        for(int i = parts.length; i > 0; i--) {
            parts[i - 1] = "*";
            String node = String.join(".", Arrays.copyOfRange(parts, 0, i));
            if(permissions.contains(node)) {
                return true;
            }
        }

        return false;

    }

    public boolean auth(String secret) {
        if(passwordHashBuf != null) {
            return compare(KEY_DIGEST.digest(BASE64_DECODER.decode(secret)), keyHashBuf);
        } else {
            return compare(hashPassword(secret), passwordHashBuf);
        }
    }

    // Constant-time comparison, using bitwise ops to reduce the chance of JIT introducing an early loop exit.
    private static boolean compare(byte[] buf1, byte[] buf2) {
        if(buf1.length != buf2.length) {
            return false;
        }

        int result = 0;
        for(int i = 0; i < buf1.length; i++) {
            result |= buf1[i] ^ buf2[i];
        }
        return result == 0;
    }

    public static byte[] generateKey() {
        byte[] key = new byte[KEY_LEN];
        RANDOM.nextBytes(key);
        return key;
    }

}
