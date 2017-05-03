package com.orange.clara.cloud.oauthregisterbroker.security;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 15/05/2016
 */
@Component
public class BootSecurity {

    @Value("${encryption.key:MySuperSecretKey}")
    protected String encryptionKey;

    @Value("classpath:properties/encryption_key.txt")
    protected File encryptionKeyFile;

    public void fillEncryptionKeyFile() throws IOException, NoSuchAlgorithmException {
        //force to have always a 32 bytes key (to use AES encryption with 256 bits key length)
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(encryptionKey.getBytes(Charsets.UTF_8));
        byte[] key = md.digest();

        Files.write(key, encryptionKeyFile);
    }

    @PostConstruct
    public void runSequence() {
        try {
            this.fillEncryptionKeyFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
