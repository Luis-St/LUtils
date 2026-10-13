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

package net.luis.utils.crypto;

import net.luis.utils.crypto.key.Secret;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * An encapsulation and the shared secret it carries.<br>
 * Close it when done, which wipes the secret.<br>
 * The encapsulation is public and is not wiped.<br>
 *
 * @author Luis-St
 *
 * @param encapsulation The encapsulation to send to the recipient
 * @param sharedSecret The shared secret the recipient will recover
 */
public record KemEncapsulation(
	byte @NonNull [] encapsulation,
	@NonNull Secret sharedSecret
) implements AutoCloseable {
	
	/**
	 * Constructs a new encapsulation.<br>
	 * @throws NullPointerException If the encapsulation or the shared secret is null
	 */
	public KemEncapsulation {
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		Objects.requireNonNull(sharedSecret, "Shared secret must not be null");
	}
	
	/**
	 * Wipes the shared secret.<br>
	 */
	@Override
	public void close() {
		this.sharedSecret.close();
	}
}
