package net.luis.utils.util;

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
	
	public static <L, R> Either<L, R> left(L value) {
		return new Left<>(value);
	}
	
	public static <L, R> Either<L, R> right(R value) {
		return new Right<>(value);
	}
	
	public abstract <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction);
	
	public abstract <T> T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction);
	
	public abstract boolean isLeft();
	
	public abstract boolean isRight();
	
	public abstract Either<L, R> ifLeft(Consumer<? super L> consumer);
	
	public abstract Either<L, R> ifRight(Consumer<? super R> consumer);
	
	public abstract Optional<L> left();
	
	public abstract Optional<R> right();
	
	public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> function) {
		return this.map((value) -> {
			return Either.left(function.apply(value));
		}, Either::right);
	}
	
	public <T> Either<L, T> mapRight(Function<? super R, ? extends T> function) {
		return this.map(Either::left, (value) -> {
			return Either.right(function.apply(value));
		});
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
		public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction) {
			return new Left<>(leftFunction.apply(this.value));
		}
		
		@Override
		public <T> T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
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
		public Either<L, R> ifLeft(Consumer<? super L> consumer) {
			consumer.accept(this.value);
			return this;
		}
		
		@Override
		public Either<L, R> ifRight(Consumer<? super R> consumer) {
			return this;
		}
		
		@Override
		public Optional<L> left() {
			return Optional.of(this.value);
		}
		
		@Override
		public Optional<R> right() {
			return Optional.empty();
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof Left<?, ?> left) {
				return Objects.equals(this.value, left.value);
			}
			return false;
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
		public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> leftFunction, Function<? super R, ? extends D> rightFunction) {
			return new Right<>(rightFunction.apply(this.value));
		}
		
		@Override
		public <T> T map(Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction) {
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
		public Either<L, R> ifLeft(Consumer<? super L> consumer) {
			return this;
		}
		
		@Override
		public Either<L, R> ifRight(Consumer<? super R> consumer) {
			consumer.accept(this.value);
			return this;
		}
		
		@Override
		public Optional<L> left() {
			return Optional.empty();
		}
		
		@Override
		public Optional<R> right() {
			return Optional.of(this.value);
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof Right<?, ?> right) {
				return Objects.equals(this.value, right.value);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}
		
	}
	
}
