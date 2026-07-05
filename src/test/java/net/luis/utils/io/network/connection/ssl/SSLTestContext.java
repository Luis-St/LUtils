/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.network.connection.ssl;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Test helper that builds {@link SSLContext} instances from a bundled self-signed test keystore.<br>
 * The keystore ({@code /ssl/keystore.p12}) contains a single {@code CN=localhost} certificate with
 * {@code SAN=dns:localhost,ip:127.0.0.1} and a 100 year validity, so it never expires during tests.<br>
 * <p>
 *     Both the client and server contexts load the same keystore as key and trust material, so they
 *     trust each other and can be used for mutual TLS as well as standard one-way TLS.
 * </p>
 *
 * @author Luis-St
 */
final class SSLTestContext {
	
	/**
	 * The password protecting the test keystore and its key entry.<br>
	 */
	static final String PASSWORD = "changeit";
	
	private SSLTestContext() {}
	
	/**
	 * Loads the bundled test keystore from the classpath.<br>
	 *
	 * @return The loaded keystore
	 * @throws Exception If the keystore cannot be loaded
	 */
	static KeyStore loadKeyStore() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream stream = SSLTestContext.class.getResourceAsStream("/ssl/keystore.p12")) {
			if (stream == null) {
				throw new IllegalStateException("Test keystore /ssl/keystore.p12 not found on the classpath");
			}
			keyStore.load(stream, PASSWORD.toCharArray());
		}
		return keyStore;
	}
	
	/**
	 * Creates a server-side SSL context holding the test certificate and private key.<br>
	 * The context also trusts the same certificate so it can require client authentication (mutual TLS).<br>
	 *
	 * @return A server SSL context
	 * @throws Exception If the context cannot be created
	 */
	static SSLContext serverContext() throws Exception {
		return createContext(true, true);
	}
	
	/**
	 * Creates a client-side SSL context that trusts the test certificate.<br>
	 * The context also provides the certificate and private key so it can present a client certificate for mutual TLS.<br>
	 *
	 * @return A client SSL context
	 * @throws Exception If the context cannot be created
	 */
	static SSLContext clientContext() throws Exception {
		return createContext(true, true);
	}
	
	/**
	 * Creates a client-side SSL context that only trusts the test certificate without providing key material.<br>
	 * Such a client cannot present a certificate and is rejected by servers requiring client authentication.<br>
	 *
	 * @return A trust-only client SSL context
	 * @throws Exception If the context cannot be created
	 */
	static SSLContext trustOnlyClientContext() throws Exception {
		return createContext(false, true);
	}
	
	/**
	 * Creates an SSL context from the test keystore with the requested key and trust material.<br>
	 *
	 * @param withKeyManager Whether to initialize key managers from the keystore
	 * @param withTrustManager Whether to initialize trust managers from the keystore
	 * @return The initialized SSL context
	 * @throws Exception If the context cannot be created
	 */
	private static SSLContext createContext(boolean withKeyManager, boolean withTrustManager) throws Exception {
		KeyStore keyStore = loadKeyStore();
		
		KeyManager[] keyManagers = null;
		if (withKeyManager) {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, PASSWORD.toCharArray());
			keyManagers = keyManagerFactory.getKeyManagers();
		}
		
		TrustManager[] trustManagers = null;
		if (withTrustManager) {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			trustManagers = trustManagerFactory.getTrustManagers();
		}
		
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagers, trustManagers, null);
		return context;
	}
}
