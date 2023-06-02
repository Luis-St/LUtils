package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Luis-st
 *
 */

public abstract class Either<L, R> {
	
	private Either() {
		
	}
	
	public static <L, R> @NotNull Either<L, R> left(L value) {
		return new Left<>(value);
	}
	
	public static <L, R> @NotNull Either<L, R> right(R value) {
		return new Right<>(value);
	}
	
	public abstract <C, D> @NotNull Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction);
	
	public abstract <T> @NotNull T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction);
	
	public abstract boolean isLeft();
	
	public abstract boolean isRight();
	
	public abstract @NotNull Either<L, R> ifLeft(Consumer<? super L> consumer);
	
	public abstract @NotNull Either<L, R> ifRight(Consumer<? super R> consumer);
	
	public abstract @NotNull Optional<L> left();
	
	public abstract @NotNull Optional<R> right();
	
	public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> function) {
		return this.map((value) -> Either.left(function.apply(value)), Either::right);
	}
	
	public <T> Either<L, T> mapRight(Function<? super R, ? extends T> function) {
		return this.map(Either::left, (value) -> Either.right(function.apply(value)));
	}
	
	public L leftOrThrow() {
		return this.left().orElseThrow();
	}
	
	public R rightOrThrow() {
		return this.right().orElseThrow();
	}
	
	public Either<R, L> swap() {
		return map(Either::right, Either::left);
	}
	
	private static final class Left<L, R> extends Either<L, R> {
		
		private final L value;
		
		public Left(L value) {
			this.value = value;
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction) {
			return new Left<>(leftFunction.apply(this.value));
		}
		
		@Override
		public <T> @NotNull T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
			return leftFunction.apply(this.value);
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
		public @NotNull Either<L, R> ifLeft(Consumer<? super L> consumer) {
			consumer.accept(this.value);
			return this;
		}
		
		@Override
		public @NotNull Either<L, R> ifRight(Consumer<? super R> consumer) {
			return this;
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.of(this.value);
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.empty();
		}
		
		@Override
		public String toString() {
			return ToString.toString(this);
		}
		
		@Override
		public boolean equals(Object object) {
			return Equals.equals(this, object);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
	}
	
	private static final class Right<L, R> extends Either<L, R> {
		
		private final R value;
		
		public Right(R value) {
			this.value = value;
		}
		
		@Override
		public <C, D> @NotNull Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction) {
			return new Right<>(rightFunction.apply(this.value));
		}
		
		@Override
		public <T> @NotNull T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
			return rightFunction.apply(this.value);
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
		public @NotNull Either<L, R> ifLeft(Consumer<? super L> consumer) {
			return this;
		}
		
		@Override
		public @NotNull Either<L, R> ifRight(Consumer<? super R> consumer) {
			consumer.accept(this.value);
			return this;
		}
		
		@Override
		public @NotNull Optional<L> left() {
			return Optional.empty();
		}
		
		@Override
		public @NotNull Optional<R> right() {
			return Optional.of(this.value);
		}
		
		@Override
		public String toString() {
			return ToString.toString(this);
		}
		
		@Override
		public boolean equals(Object object) {
			return Equals.equals(this, object);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		
	}
	
}
