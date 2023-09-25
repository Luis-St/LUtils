package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public abstract class Either<L, R> {
	
	private Either() {
	
	}
	
	public static <L, R> @NotNull Either<L, R> left(@Nullable L value) {
		return new Left<>(value);
	}
	
	public static <L, R> @NotNull Either<L, R> right(@Nullable R value) {
		return new Right<>(value);
	}
	
	public abstract <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper);
	
	public abstract <T> T map(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper);
	
	public abstract boolean isLeft();
	
	public abstract boolean isRight();
	
	public abstract @NotNull Either<L, R> ifLeft(@NotNull Consumer<? super L> action);
	
	public abstract @NotNull Either<L, R> ifRight(@NotNull Consumer<? super R> action);
	
	public abstract @NotNull Optional<L> left();
	
	public abstract @NotNull Optional<R> right();
	
	public <T> Either<T, R> mapLeft(@NotNull Function<? super L, ? extends T> mapper) {
		return this.map((value) -> Either.left(Objects.requireNonNull(mapper, "Mapper must not be null").apply(value)), Either::right);
	}
	
	public <T> Either<L, T> mapRight(@NotNull Function<? super R, ? extends T> mapper) {
		return this.map(Either::left, (value) -> Either.right(Objects.requireNonNull(mapper, "Mapper must not be null").apply(value)));
	}
	
	public @NotNull L leftOrThrow() {
		return this.left().orElseThrow();
	}
	
	public @NotNull R rightOrThrow() {
		return this.right().orElseThrow();
	}
	
	public @NotNull Either<R, L> swap() {
		return this.map(Either::right, Either::left);
	}
	
	private static final class Left<L, R> extends Either<L, R> {
		
		//region Left implementation
		private final L value;
		
		private Left(@Nullable L value) {
			this.value = value;
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper) {
			return new Left<>(Objects.requireNonNull(leftMapper, "Left mapper must nut be null").apply(this.value));
		}
		
		@Override
		public <T> T map(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper) {
			return Objects.requireNonNull(leftMapper, "Left mapper must nut be null").apply(this.value);
		}
		
		@Override
		public boolean isLeft() {
			return true;
		}
		
		@Override
		public boolean isRight() {
			return false;
		}
		
		@Override
		public @NotNull Either<L, R> ifLeft(@NotNull Consumer<? super L> action) {
			Objects.requireNonNull(action, "Action must not be null").accept(this.value);
			return this;
		}
		
		@Override
		public @NotNull Either<L, R> ifRight(@NotNull Consumer<? super R> action) {
			return this;
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.ofNullable(this.value);
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.empty();
		}
		//endregion
		
		//region Object overrides
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Left<?, ?> left)) return false;
			
			return Objects.equals(this.value, left.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		
		@Override
		public String toString() {
			return "Left{value=" + this.value + "}";
		}
		//endregion
	}
	
	private static final class Right<L, R> extends Either<L, R> {
		
		//region Right implementation
		private final R value;
		
		private Right(@Nullable R value) {
			this.value = value;
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(@NotNull Function<? super L, ? extends C> leftMapper, @NotNull Function<? super R, ? extends D> rightMapper) {
			return new Right<>(Objects.requireNonNull(rightMapper, "Right mapper must nut be null").apply(this.value));
		}
		
		@Override
		public <T> T map(@NotNull Function<? super L, ? extends T> leftMapper, @NotNull Function<? super R, ? extends T> rightMapper) {
			return Objects.requireNonNull(rightMapper, "Right mapper must nut be null").apply(this.value);
		}
		
		@Override
		public boolean isLeft() {
			return false;
		}
		
		@Override
		public boolean isRight() {
			return true;
		}
		
		@Override
		public @NotNull Either<L, R> ifLeft(@NotNull Consumer<? super L> action) {
			return this;
		}
		
		@Override
		public @NotNull Either<L, R> ifRight(@NotNull Consumer<? super R> action) {
			Objects.requireNonNull(action, "Action must not be null").accept(this.value);
			return this;
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.empty();
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.ofNullable(this.value);
		}
		//endregion
		
		//region Object overrides
		@Override
		public String toString() {
			return "Right{value=" + this.value + "}";
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Right<?, ?> right)) return false;
			
			return Objects.equals(this.value, right.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		//endregion
	}
}
