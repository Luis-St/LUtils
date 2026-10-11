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

package net.luis.utils.crypto.cert;

import com.google.common.collect.Lists;
import net.luis.utils.crypto.Hashes;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * A set of public key pins, digests of the subject public key info a trusted chain has to contain.<br>
 * <p>
 *     A pin binds trust to a key rather than to a certificate, so it survives a renewal as long as the key stays the same.<br>
 *     Pinning the key of an intermediate or root authority instead of the leaf survives a change of the leaf key as well.<br>
 *     Always pin a backup key too, since losing every pinned key locks all clients out.
 * </p>
 * <p>
 *     Pinning adds to chain validation and never replaces it.<br>
 *     The trust manager of {@link #trustManager(X509TrustManager)} runs the full PKIX check of its delegate first and checks the pins only on a chain that passed.<br>
 *     The pins are matched against every certificate the peer sent and against the anchor the chain ends in.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * CertificatePins pins = CertificatePins.pinSpki(HashAlgorithm.SHA_256, CertificatePins.spki(HashAlgorithm.SHA_256, intermediate), backupPin);
 *
 * SSLContext context = SSLContext.getInstance("TLS");
 * context.init(null, new TrustManager[] { pins.trustManager(TrustStore.system().trustManager()) }, null);
 * }</pre>
 *
 * @author Luis-St
 */
public final class CertificatePins {
	
	/**
	 * The hash the pins are digests of.<br>
	 */
	private final HashAlgorithm algorithm;
	/**
	 * The pinned digests.<br>
	 */
	private final List<byte[]> pins;
	
	/**
	 * Constructs a new pin set.<br>
	 *
	 * @param algorithm The hash the pins are digests of
	 * @param pins The pinned digests, already copied
	 * @throws NullPointerException If the algorithm or the pins are null
	 */
	private CertificatePins(@NonNull HashAlgorithm algorithm, @NonNull List<byte[]> pins) {
		this.algorithm = Objects.requireNonNull(algorithm, "Algorithm must not be null");
		this.pins = Objects.requireNonNull(pins, "Pins must not be null");
	}
	
	/**
	 * Creates a pin set from the given digests of subject public key infos.<br>
	 * A digest in the form HTTP public key pinning used is the base64 decoding of its {@code pin-sha256} value.<br>
	 *
	 * @param algorithm The hash the pins are digests of
	 * @param pins The pinned digests
	 * @return The created pin set
	 * @throws NullPointerException If the algorithm or the pins are null, or the pins hold null
	 * @throws IllegalArgumentException If no pin is given, or a pin does not have the digest length of the algorithm
	 */
	public static @NonNull CertificatePins pinSpki(@NonNull HashAlgorithm algorithm, byte @NonNull [] @NonNull ... pins) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(pins, "Pins must not be null");
		if (pins.length == 0) {
			throw new IllegalArgumentException("A pin set needs at least one pin");
		}
		
		List<byte[]> copies = new ArrayList<>(pins.length);
		for (byte[] pin : pins) {
			Objects.requireNonNull(pin, "Pin must not be null");
			if (pin.length != algorithm.digestLength()) {
				throw new IllegalArgumentException("A " + algorithm.jcaName() + " pin must be " + algorithm.digestLength() + " bytes long, was " + pin.length);
			}
			
			copies.add(pin.clone());
		}
		return new CertificatePins(algorithm, List.copyOf(copies));
	}
	
	/**
	 * Computes the pin of the given certificate, the digest of its subject public key info.<br>
	 *
	 * @param algorithm The hash to compute the pin with
	 * @param certificate The certificate to pin
	 * @return The computed pin
	 * @throws NullPointerException If the algorithm or the certificate is null
	 * @throws MalformedDataException If the certificate cannot be read
	 */
	public static byte @NonNull [] spki(@NonNull HashAlgorithm algorithm, @NonNull X509Certificate certificate) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		return Hashes.hash(algorithm, X509Support.spki(certificate));
	}
	
	/**
	 * Returns the hash the pins are digests of.<br>
	 * @return The hash algorithm
	 */
	public @NonNull HashAlgorithm algorithm() {
		return this.algorithm;
	}
	
	/**
	 * Returns copies of the pinned digests.<br>
	 * @return The pins
	 */
	public @NonNull @Unmodifiable List<byte[]> pins() {
		return this.pins.stream().map(byte[]::clone).toList();
	}
	
	/**
	 * Returns whether any certificate of the given chain carries a pinned key.<br>
	 *
	 * @param chain The chain to check
	 * @return True if at least one certificate matches a pin
	 * @throws NullPointerException If the chain is null or holds null
	 */
	public boolean matches(@NonNull List<X509Certificate> chain) {
		Objects.requireNonNull(chain, "Chain must not be null");
		
		for (X509Certificate certificate : chain) {
			byte[] digest = spki(this.algorithm, certificate);
			for (byte[] pin : this.pins) {
				if (CryptoBytes.equalsConstantTime(pin, digest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Creates a trust manager that runs the given delegate and then requires a pinned key in the chain.<br>
	 * <p>
	 *     The delegate should be a PKIX trust manager such as {@link TrustStore#trustManager()}.<br>
	 *     Where the delegate is an extended trust manager, the socket and engine variants are passed on to it, so hostname verification and algorithm constraints keep working.
	 * </p>
	 *
	 * @param delegate The trust manager to run first
	 * @return The pinning trust manager
	 * @throws NullPointerException If the delegate is null
	 */
	public @NonNull X509TrustManager trustManager(@NonNull X509TrustManager delegate) {
		Objects.requireNonNull(delegate, "Delegate must not be null");
		return new PinningTrustManager(delegate, this);
	}
	
	@Override
	public String toString() {
		return "CertificatePins[algorithm=" + this.algorithm + ", pins=" + this.pins.size() + "]";
	}
	
	/**
	 * A trust manager that runs a delegate and then requires a pinned key in the chain.<br>
	 *
	 * @author Luis-St
	 */
	private static final class PinningTrustManager extends X509ExtendedTrustManager {
		
		/**
		 * The trust manager running the chain validation.<br>
		 */
		private final X509TrustManager delegate;
		/**
		 * The pins a validated chain has to match.<br>
		 */
		private final CertificatePins pins;
		
		/**
		 * Constructs a new pinning trust manager.<br>
		 *
		 * @param delegate The trust manager running the chain validation
		 * @param pins The pins a validated chain has to match
		 * @throws NullPointerException If the delegate or the pins are null
		 */
		private PinningTrustManager(@NonNull X509TrustManager delegate, @NonNull CertificatePins pins) {
			this.delegate = Objects.requireNonNull(delegate, "Delegate must not be null");
			this.pins = Objects.requireNonNull(pins, "Pins must not be null");
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.delegate.checkClientTrusted(chain, authType);
			this.requirePin(chain);
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, @Nullable Socket socket) throws CertificateException {
			if (this.delegate instanceof X509ExtendedTrustManager extended) {
				extended.checkClientTrusted(chain, authType, socket);
			} else {
				this.delegate.checkClientTrusted(chain, authType);
			}
			this.requirePin(chain);
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, @Nullable SSLEngine engine) throws CertificateException {
			if (this.delegate instanceof X509ExtendedTrustManager extended) {
				extended.checkClientTrusted(chain, authType, engine);
			} else {
				this.delegate.checkClientTrusted(chain, authType);
			}
			this.requirePin(chain);
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.delegate.checkServerTrusted(chain, authType);
			this.requirePin(chain);
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, @Nullable Socket socket) throws CertificateException {
			if (this.delegate instanceof X509ExtendedTrustManager extended) {
				extended.checkServerTrusted(chain, authType, socket);
			} else {
				this.delegate.checkServerTrusted(chain, authType);
			}
			this.requirePin(chain);
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, @Nullable SSLEngine engine) throws CertificateException {
			if (this.delegate instanceof X509ExtendedTrustManager extended) {
				extended.checkServerTrusted(chain, authType, engine);
			} else {
				this.delegate.checkServerTrusted(chain, authType);
			}
			this.requirePin(chain);
		}
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return this.delegate.getAcceptedIssuers();
		}
		
		/**
		 * Requires that the given chain or the anchor it ends in carries a pinned key.<br>
		 *
		 * @param chain The chain the delegate accepted
		 * @throws NullPointerException If the chain is null
		 * @throws CertificateException If no certificate of the chain carries a pinned key
		 */
		private void requirePin(X509Certificate @NonNull [] chain) throws CertificateException {
			Objects.requireNonNull(chain, "Chain must not be null");
			
			List<X509Certificate> candidates = Lists.newArrayList(Arrays.asList(chain));
			if (chain.length > 0) {
				X509Certificate last = chain[chain.length - 1];
				Arrays.stream(this.delegate.getAcceptedIssuers()).filter(issuer -> issuer.getSubjectX500Principal().equals(last.getIssuerX500Principal())).forEach(candidates::add);
			}
			if (!this.pins.matches(candidates)) {
				throw new CertificateException("No certificate of the chain carries a pinned public key");
			}
		}
	}
}
