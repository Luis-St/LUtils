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

import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codec.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecBuilder}.<br>
 *
 * @author Luis-St
 */
class CodecBuilderTest {
	
	@Test
	void create() {
		assertThrows(NullPointerException.class, () -> CodecBuilder.create(null));
	}
	
	@Test
	void group() {
		//region One parameter
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null));
			return null;
		});
		//endregion
		//region Two parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Three parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Four parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Five parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Six parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Seven parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)));
			assertThrows(NullPointerException.class, () -> builder.group(STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null));
			return null;
		});
		//endregion
		//region Eight parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Nine parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Ten parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Eleven parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Twelve parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder),
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null,
				STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Thirteen parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null,
				STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Fourteen parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null,
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Fifteen parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null,
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
		//region Sixteen parameters
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.group(
				null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null,
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder), STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null, STRING.bind(builder)
			));
			assertThrows(NullPointerException.class, () -> builder.group(
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder),
				STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), STRING.bind(builder), null
			));
			return null;
		});
		//endregion
	}
	
	@Test
	void bind() {
		CodecBuilder.create(builder -> {
			assertThrows(NullPointerException.class, () -> builder.bind(null));
			assertNotNull(builder.bind(STRING));
			return null;
		});
	}
}
