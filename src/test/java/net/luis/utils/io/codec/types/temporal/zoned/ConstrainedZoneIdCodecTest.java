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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		ZoneId validValue = ZoneOffset.UTC;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validValue);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		Result<String> result = codec.encodeKey(ZoneOffset.UTC);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		ZoneId differentValue = ZoneId.of("America/New_York");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintValid() {
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		Result<String> result = codec.encodeKey(expected);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintValid() {
		ZoneId expected = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.equalTo(expected);
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintValid() {
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		Result<String> result = codec.encodeKey(ZoneId.of("America/New_York"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintValid() {
		ZoneId excluded = ZoneId.of("Europe/Berlin");
		Codec<ZoneId> codec = Codecs.ZONE_ID.notEqualTo(excluded);
		
		Result<ZoneId> result = codec.decodeKey("America/New_York");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		ZoneId notAllowed = ZoneId.of("America/New_York");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintValid() {
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		Result<String> result = codec.encodeKey(ZoneId.of("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintValid() {
		Set<ZoneId> allowed = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.in(allowed);
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ZoneId.of("Europe/Berlin"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintValid() {
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		Result<String> result = codec.encodeKey(ZoneId.of("America/New_York"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintValid() {
		Set<ZoneId> excluded = Set.of(
			ZoneId.of("Europe/Berlin"),
			ZoneId.of("Europe/London")
		);
		Codec<ZoneId> codec = Codecs.ZONE_ID.notIn(excluded);
		
		Result<ZoneId> result = codec.decodeKey("America/New_York");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRegionBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		ZoneId offsetBased = ZoneOffset.of("+02:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), offsetBased);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartRegionBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyRegionBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		Result<String> result = codec.encodeKey(ZoneId.of("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyRegionBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.regionBased();
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOffsetBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		ZoneId regionBased = ZoneId.of("Europe/Berlin");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), regionBased);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOffsetBasedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOffsetBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOffsetBasedConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.offsetBased();
		
		Result<ZoneId> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartUtcConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		ZoneId nonUtc = ZoneId.of("Europe/Berlin");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonUtc);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartUtcConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyUtcConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		Result<String> result = codec.encodeKey(ZoneOffset.UTC);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyUtcConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.utc();
		
		Result<ZoneId> result = codec.decodeKey("Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAvailableConstraintValid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		ZoneId availableZone = ZoneId.of("Europe/Berlin");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), availableZone);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartAvailableConstraintValid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyAvailableConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		Result<String> result = codec.encodeKey(ZoneId.of("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyAvailableConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.available();
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartRegionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));
		ZoneId americanZone = ZoneId.of("America/New_York");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), americanZone);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartRegionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));

		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyRegionConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));

		Result<String> result = codec.encodeKey(ZoneId.of("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyRegionConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.region(builder -> builder.startsWith("Europe/"));

		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> value.getId().contains("Europe") ? Result.success(null) : Result.error("Zone ID must be in Europe"));
		ZoneId americanZone = ZoneId.of("America/New_York");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), americanZone);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> value.getId().contains("Europe") ? Result.success(null) : Result.error("Zone ID must be in Europe"));
		
		Result<ZoneId> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("America/New_York"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> value.getId().contains("Europe") ? Result.success(null) : Result.error("Zone ID must be in Europe"));
		
		Result<String> result = codec.encodeKey(ZoneId.of("Europe/Berlin"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintValid() {
		Codec<ZoneId> codec = Codecs.ZONE_ID.custom(value -> value.getId().contains("Europe") ? Result.success(null) : Result.error("Zone ID must be in Europe"));
		
		Result<ZoneId> result = codec.decodeKey("Europe/Berlin");
		assertTrue(result.isSuccess());
	}
}
