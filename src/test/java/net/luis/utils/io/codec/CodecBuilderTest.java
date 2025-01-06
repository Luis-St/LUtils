/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.codec;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codec.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecBuilder}.<br>
 *
 * @author Luis-St
 */
class CodecBuilderTest {
	
	private static @NotNull ConfiguredCodec<Optional<Integer>, TestObject> create() {
		return new ConfiguredCodec<>(INTEGER.optional(), TestObject::age);
	}
	
	@Test
	void group() {
		//region One parameter
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null));
		//endregion
		//region Two parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null));
		//endregion
		//region Three parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), null));
		//endregion
		//region Four parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null, create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), null));
		;
		//endregion
		//region Five parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null, create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), null, create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), null));
		//endregion
		//region Six parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create(), create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null, create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), null, create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), null, create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), create(), null));
		//endregion
		//region Seven parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(null, create(), create(), create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), null, create(), create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), null, create(), create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), null, create(), create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), null, create(), create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), create(), null, create()));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(create(), create(), create(), create(), create(), create(), null));
		//endregion
		//region Eight parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null
		));
		//endregion
		//region Nine parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null
		));
		//endregion
		//region Ten parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null
		));
		//endregion
		//region Eleven parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null
		));
		//endregion
		//region Twelve parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create(),
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null,
			create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), null
		));
		//endregion
		//region Thirteen parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create(),
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null,
			create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), null
		));
		//endregion
		//region Fourteen parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create(),
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null,
			create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), null
		));
		//endregion
		//region Fifteen parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create(),
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null,
			create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), null
		));
		//endregion
		//region Sixteen parameters
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			null, create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), null, create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), null, create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), null, create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), null, create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), null, create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), null, create(), create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), null, create(), create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), null, create(), create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), null, create(),
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(), null,
			create(), create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), null, create(), create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), null, create(), create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), null, create(), create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), null, create()
		));
		assertThrows(NullPointerException.class, () -> CodecBuilder.group(
			create(), create(), create(), create(), create(), create(), create(), create(), create(), create(),
			create(), create(), create(), create(), create(), null
		));
		//endregion
	}
	
	//region Internal
	private record TestObject(@NotNull Optional<Integer> age) {}
	//endregion
}
