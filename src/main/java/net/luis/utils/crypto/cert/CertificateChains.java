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

import net.luis.utils.crypto.exception.CryptoException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.security.GeneralSecurityException;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;

/**
 * Validation and construction of certificate chains.<br>
 * <p>
 *     Both go through the PKIX implementation of the JDK and never through {@link net.luis.utils.crypto.Signatures}.<br>
 *     A chain from a public authority is usually signed with RSA, which this library deliberately does not model, and the JDK verifies it all the same.
 * </p>
 * <p>
 *     A chain is written leaf first.<br>
 *     The trust anchor may be left out or included at the end, both validate the same way.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * ChainValidationResult result = CertificateChains.validate(chain, TrustStore.of(root), Instant.now(), new RevocationPolicy.Crl(List.of(crl)));
 * if (result instanceof ChainValidationResult.Invalid(String reason, int index)) {
 *     LOGGER.warn("Certificate {} of the chain is not valid: {}", index, reason);
 * }
 *
 * // Put a chain together from an unordered pool of intermediates
 * Optional<List<X509Certificate>> built = CertificateChains.build(leaf, intermediates, TrustStore.system());
 * }</pre>
 *
 * @author Luis-St
 */
public final class CertificateChains {
	
	/**
	 * The type of an in-memory certificate store.<br>
	 */
	private static final String COLLECTION = "Collection";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CertificateChains() {}
	
	/**
	 * Validates the given chain against the given trust store.<br>
	 * <p>
	 *     Validation checks every signature along the chain, the validity period of every certificate at the given instant, the basic constraints, the key usages of the issuers and the revocation status as the policy demands.<br>
	 *     It does not check what the leaf may be used for, so a client certificate validates as well as a server certificate.
	 * </p>
	 * <p>
	 *     A leaf that is itself an anchor of the store is valid whenever the instant lies within its validity period.
	 * </p>
	 *
	 * @param chain The chain to validate, leaf first
	 * @param trustStore The anchors the chain has to end in
	 * @param at The instant to validate at
	 * @param policy How revocation is checked
	 * @return The outcome of the validation
	 * @throws NullPointerException If any parameter is null, or the chain holds null
	 * @throws IllegalArgumentException If the chain is empty
	 * @throws CryptoException If the runtime provides no PKIX validator
	 */
	public static @NonNull ChainValidationResult validate(@NonNull List<X509Certificate> chain, @NonNull TrustStore trustStore, @NonNull Instant at, @NonNull RevocationPolicy policy) {
		Objects.requireNonNull(chain, "Chain must not be null");
		Objects.requireNonNull(trustStore, "Trust store must not be null");
		Objects.requireNonNull(at, "Instant must not be null");
		Objects.requireNonNull(policy, "Policy must not be null");
		List<X509Certificate> certificates = List.copyOf(chain);
		if (certificates.isEmpty()) {
			throw new IllegalArgumentException("Chain must not be empty");
		}
		if (trustStore.isEmpty()) {
			return new ChainValidationResult.Invalid("The trust store holds no anchor", -1);
		}
		
		int end = certificates.size();
		while (end > 0 && trustStore.contains(certificates.get(end - 1))) {
			end--;
		}
		if (end == 0) {
			X509Certificate leaf = certificates.getFirst();
			return Certificates.isValidAt(leaf, at) ? new ChainValidationResult.Valid(leaf) : new ChainValidationResult.Invalid("Certificate is not valid at " + at, 0);
		}
		
		try {
			CertPathValidator validator = CertPathValidator.getInstance(TrustStore.PKIX);
			PKIXParameters parameters = new PKIXParameters(trustStore.anchors());
			parameters.setDate(Date.from(at));
			configureRevocation(parameters, validator, policy);
			
			CertPath path = CertificateFactory.getInstance("X.509").generateCertPath(certificates.subList(0, end));
			try {
				PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator.validate(path, parameters);
				return new ChainValidationResult.Valid(result.getTrustAnchor().getTrustedCert());
			} catch (CertPathValidatorException e) {
				return new ChainValidationResult.Invalid(Objects.requireNonNullElse(e.getMessage(), String.valueOf(e.getReason())), Math.max(-1, e.getIndex()));
			}
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot set up PKIX validation", e);
		}
	}
	
	/**
	 * Builds a chain from the given leaf to an anchor of the given trust store.<br>
	 * <p>
	 *     The certificates are taken from the pool in whatever order they are, so an unordered bundle of intermediates is enough.<br>
	 *     The chain found is validated at the current instant without revocation checking.<br>
	 *     The returned chain starts with the leaf and ends with the certificate the anchor issued, the anchor itself is not part of it.
	 * </p>
	 *
	 * @param leaf The certificate to build a chain for
	 * @param pool The certificates to build the chain from
	 * @param trustStore The anchors the chain has to end in
	 * @return The chain, leaf first, or empty if no chain to an anchor can be built
	 * @throws NullPointerException If any parameter is null, or the pool holds null
	 * @throws CryptoException If the runtime provides no PKIX builder
	 */
	public static @NonNull Optional<@Unmodifiable List<X509Certificate>> build(@NonNull X509Certificate leaf, @NonNull Collection<X509Certificate> pool, @NonNull TrustStore trustStore) {
		Objects.requireNonNull(leaf, "Leaf must not be null");
		Objects.requireNonNull(pool, "Pool must not be null");
		Objects.requireNonNull(trustStore, "Trust store must not be null");
		if (trustStore.contains(leaf)) {
			return Optional.of(List.of(leaf));
		}
		if (trustStore.isEmpty()) {
			return Optional.empty();
		}
		
		try {
			X509CertSelector selector = new X509CertSelector();
			selector.setCertificate(leaf);
			PKIXBuilderParameters parameters = new PKIXBuilderParameters(trustStore.anchors(), selector);
			parameters.setRevocationEnabled(false);
			
			List<X509Certificate> certificates = new ArrayList<>(List.copyOf(pool));
			certificates.add(leaf);
			parameters.addCertStore(CertStore.getInstance(COLLECTION, new CollectionCertStoreParameters(certificates)));
			
			CertPathBuilder builder = CertPathBuilder.getInstance(TrustStore.PKIX);
			try {
				CertPath path = builder.build(parameters).getCertPath();
				return Optional.of(path.getCertificates().stream().map(X509Certificate.class::cast).toList());
			} catch (CertPathBuilderException e) {
				return Optional.empty();
			}
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Cannot set up PKIX chain building", e);
		}
	}
	
	/**
	 * Configures the revocation checking of the given parameters as the given policy demands.<br>
	 *
	 * @param parameters The parameters to configure
	 * @param validator The validator the parameters are for, which supplies the revocation checker
	 * @param policy The policy to apply
	 * @throws NullPointerException If any parameter is null
	 * @throws GeneralSecurityException If the certificate store holding the revocation lists cannot be created
	 */
	private static void configureRevocation(@NonNull PKIXParameters parameters, @NonNull CertPathValidator validator, @NonNull RevocationPolicy policy) throws GeneralSecurityException {
		Objects.requireNonNull(parameters, "Parameters must not be null");
		Objects.requireNonNull(validator, "Validator must not be null");
		Objects.requireNonNull(policy, "Policy must not be null");
		
		switch (policy) {
			case RevocationPolicy.None _ -> parameters.setRevocationEnabled(false);
			case RevocationPolicy.Crl crl -> {
				PKIXRevocationChecker checker = (PKIXRevocationChecker) validator.getRevocationChecker();
				checker.setOptions(EnumSet.of(PKIXRevocationChecker.Option.PREFER_CRLS, PKIXRevocationChecker.Option.NO_FALLBACK));
				parameters.addCertStore(CertStore.getInstance(COLLECTION, new CollectionCertStoreParameters(crl.crls())));
				parameters.setRevocationEnabled(true);
				parameters.addCertPathChecker(checker);
			}
			case RevocationPolicy.Ocsp ocsp -> {
				PKIXRevocationChecker checker = (PKIXRevocationChecker) validator.getRevocationChecker();
				EnumSet<PKIXRevocationChecker.Option> options = EnumSet.of(PKIXRevocationChecker.Option.NO_FALLBACK);
				if (ocsp.softFail()) {
					options.add(PKIXRevocationChecker.Option.SOFT_FAIL);
				}
				
				checker.setOptions(options);
				
				if (ocsp.responder() != null) {
					checker.setOcspResponder(ocsp.responder());
				}
				
				parameters.setRevocationEnabled(true);
				parameters.addCertPathChecker(checker);
			}
		}
	}
}
