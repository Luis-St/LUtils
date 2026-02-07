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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link ZoneIdCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedZoneIdCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		ZoneId validValue = ZoneOffset.UTC;
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validValue));
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.UTC));
	}
	
	@Test
	void decodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z")));
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		ZoneId differentValue = ZoneId.of("America/New_York");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York")));
	}
	
	@Test
	void encodeKeyEqualToConstraintValid() {
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		assertDoesNotThrow(() -> codec.encodeKey(expected));
	}
	
	@Test
	void decodeKeyEqualToConstraintValid() {
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintValid() {
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("America/New_York")));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintValid() {
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.decodeKey("America/New_York"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		ZoneId notAllowed = ZoneId.of("America/New_York");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York")));
	}
	
	@Test
	void encodeKeyInConstraintValid() {
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeKeyInConstraintValid() {
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void encodeKeyNotInConstraintValid() {
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("America/New_York")));
	}
	
	@Test
	void decodeKeyNotInConstraintValid() {
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		assertDoesNotThrow(() -> codec.decodeKey("America/New_York"));
	}
	
	@Test
	void encodeRegionBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		ZoneId offsetBased = ZoneOffset.of("+02:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), offsetBased));
	}
	
	@Test
	void decodeRegionBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void encodeKeyRegionBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeKeyRegionBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void encodeOffsetBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		ZoneId regionBased = ZoneId.of("Europe/Berlin");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), regionBased));
	}
	
	@Test
	void decodeOffsetBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void encodeKeyOffsetBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyOffsetBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeUtcConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		ZoneId nonUtc = ZoneId.of("Europe/Berlin");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonUtc));
	}
	
	@Test
	void decodeUtcConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void encodeKeyUtcConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.UTC));
	}
	
	@Test
	void decodeKeyUtcConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		assertDoesNotThrow(() -> codec.decodeKey("Z"));
	}
	
	@Test
	void encodeAvailableConstraintValid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		ZoneId availableZone = ZoneId.of("Europe/Berlin");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), availableZone));
	}
	
	@Test
	void decodeAvailableConstraintValid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin")));
	}
	
	@Test
	void encodeKeyAvailableConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeKeyAvailableConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void encodeRegionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));
		ZoneId americanZone = ZoneId.of("America/New_York");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), americanZone));
	}
	
	@Test
	void decodeRegionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York")));
	}
	
	@Test
	void encodeKeyRegionConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeKeyRegionConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> {
			if (!(value.getId().contains("Europe"))) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Zone ID must be in Europe");
		});
		ZoneId americanZone = ZoneId.of("America/New_York");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), americanZone));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> {
			if (!(value.getId().contains("Europe"))) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Zone ID must be in Europe");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York")));
	}
	
	@Test
	void encodeKeyCustomConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> {
			if (!(value.getId().contains("Europe"))) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Zone ID must be in Europe");
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneId.of("Europe/Berlin")));
	}
	
	@Test
	void decodeKeyCustomConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> {
			if (!(value.getId().contains("Europe"))) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Zone ID must be in Europe");
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("Europe/Berlin"));
	}
}
