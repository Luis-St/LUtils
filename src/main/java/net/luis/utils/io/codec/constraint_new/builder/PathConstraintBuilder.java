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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint_new.core.Platform;
import net.luis.utils.io.codec.constraint_new.network.PathConstraint;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Builder class for constructing path-based constraints.<br>
 * <p>
 *     This builder implements {@link PathConstraint} to provide a fluent API for building
 *     constraints on file paths including structure, components, and platform compatibility.<br>
 *     It is typically used as a parameter to constraint builder methods that accept path constraints.
 * </p>
 *
 * @author Luis-St
 *
 */

public class PathConstraintBuilder implements PathConstraint<String, PathConstraintBuilder> {
	
	@Override
	public @NonNull PathConstraintBuilder equalTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notEqualTo(@NonNull String value) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder in(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder notIn(@NonNull Collection<String> values) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder minLength(int minLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder maxLength(int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder exactLength(int exactLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder lengthBetween(int minLength, int maxLength) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder minDepth(int minDepth) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder maxDepth(int maxDepth) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder exactDepth(int exactDepth) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder depthBetween(int minDepth, int maxDepth) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder absolute() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder relative() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder normalized() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder canonical() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder path(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder root(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder parent(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder segment(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder file(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder withoutExtension() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder extension(@NonNull UnaryOperator<StringConstraintBuilder> builder) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull String path) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder ancestorOf(@NonNull Collection<String> paths) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull String path) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder descendantOf(@NonNull Collection<String> paths) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder validFor(@NonNull Platform platform) {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder portable() {
		return null;
	}
	
	@Override
	public @NonNull PathConstraintBuilder separator(@NonNull Platform platform) {
		return null;
	}
}
