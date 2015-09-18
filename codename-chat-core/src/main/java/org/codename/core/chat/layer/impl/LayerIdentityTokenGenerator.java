package org.codename.core.chat.layer.impl;

import com.google.gson.JsonObject;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.RsaSHA256Signer;

import org.joda.time.Instant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import org.apache.commons.io.IOUtils;

public class LayerIdentityTokenGenerator {

    /**
     * Expiration time for this token, set to 2 minutes
     */
    private static final long TWO_MINUTES_IN_MILLISECONDS = 1000L * 60L * 2L;

    /**
     * The Layer application's configuration variables
     */
    private LayerConfig layerConfig;

    /**
     * Creates a generator that uses the default configuration
     */
    public LayerIdentityTokenGenerator() {
        layerConfig = new LayerConfig();
    }

    /**
     * Creates a generator with a custom configuration
     *
     * @param config the Layer app configuration to use
     */
    public LayerIdentityTokenGenerator(LayerConfig config) {
        layerConfig = config;
    }

    /**
     * Gets a Layer Identity Token for the given userId and nonce pair.
     *
     * The generated identity token has an expiration date of `now` + two
     * minutes.
     *
     * @param nonce the nonce generated by the Layer SDK
     * @param userId your (the Provider) representation of the authenticated
     * user
     * @return an encoded identity token
     * @throws SignatureException see: {@link JsonToken#serializeAndSign}
     * @throws InvalidKeyException see: {@link RsaSHA256Signer}
     * @throws NoSuchAlgorithmException see: {@link #getPrivateKey()}
     * @throws InvalidKeySpecException see: {@link #getPrivateKey()}
     * @throws IOException if reading the key from disk failed
     */
    public String getToken(String nonce, String userId) throws SignatureException,
            InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        final Calendar cal = Calendar.getInstance();
        final RsaSHA256Signer signer = new RsaSHA256Signer(null, null, getPrivateKey());
        final JsonToken token = new JsonToken(signer);
        final JsonObject header = token.getHeader();

        header.addProperty("typ", "JWT");
        header.addProperty("alg", "RS256");
        header.addProperty("cty", "layer-eit;v=1");
        header.addProperty("kid", layerConfig.getLayerKeyId());
        
        token.setParam("iss", layerConfig.getProviderId());
        token.setParam("prn", userId);
        token.setIssuedAt(new Instant(cal.getTimeInMillis()));
        token.setExpiration(new Instant(cal.getTimeInMillis() + TWO_MINUTES_IN_MILLISECONDS));
        token.setParam("nce", nonce);
        
        return token.serializeAndSign();
    }

    /**
     * Reads the contents of the local private key file
     *
     * @param path the path of the local certificate
     * @return a byte array of the key's contents
     * @throws FileNotFoundException if the given path is not found
     * @throws IOException if an error occurs reading from the file
     */
    private byte[] readPrivateKeyFromDisk(final String path) throws IOException {

        InputStream fileInputStream = LayerIdentityTokenGenerator.class.getResourceAsStream(path);
        return IOUtils.toByteArray(fileInputStream);
    }

    /**
     * @return the private key from disk represented as a RSAPrivateKey object
     * @throws NoSuchAlgorithmException see: {@link KeyFactory#getInstance}
     * @throws InvalidKeySpecException see:
     * {@link java.security.KeyFactory#generatePrivate}
     * @throws IOException reading private key from disk failed
     */
    private RSAPrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException {

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final byte[] encodedKey = readPrivateKeyFromDisk(layerConfig.getRsaKeyPath());
        final EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return (RSAPrivateKey) privateKey;
    }

}
