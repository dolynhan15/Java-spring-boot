package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.utils.CipherKeys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Getter
@Setter
@NoArgsConstructor
public class PublicKeyEmbedded {

    private String publicKey;
    private String encryptedPublicKey;

    public PublicKeyEmbedded(String publicKeyString, String aesSecretKey) {
        this.publicKey = publicKeyString;
        if (StringUtils.isNotBlank(publicKey)) {
            byte[] publicKeyInBytes = Base64.getDecoder().decode(publicKey);
            try {
                PublicKey userPublicKey = CipherKeys.getPublicKey(publicKeyInBytes);
                encryptedPublicKey = CipherKeys.encryptByRSA(aesSecretKey, userPublicKey);
            } catch (NoSuchAlgorithmException | IllegalBlockSizeException
                    | UnsupportedEncodingException | BadPaddingException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
    }
}
