package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class Result<T> implements Supplier<Either<T, String>> {
	
	private final Either<T, String> either;
	
	private Result(Either<T, String> either) {
		this.either = either;
	}
	
	public static <T> @NotNull Result<T> success(T value) {
		return new Result<>(Either.left(value));
	}
	
	public static <T> @NotNull Result<T> error(String error) {
		return new Result<>(Either.right(Objects.requireNonNull(error, "Error must not be null")));
	}
	
	@Override
	public @NotNull Either<T, String> get() {
		return this.either;
	}
	
	public boolean isSuccess() {
		return this.either.isLeft();
	}
	
	public @NotNull Optional<T> result() {
		return this.either.left();
	}
	
	public @NotNull T orThrow() {
		return this.orThrow(() -> new NoSuchElementException("No value available"));
	}
	
	public <X extends RuntimeException> @NotNull T orThrow(Supplier<? extends X> exceptionSupplier) {
		if (this.either.isLeft()) {
			return this.either.leftOrThrow();
		}
		throw Objects.requireNonNull(exceptionSupplier, "Supplied exception must not be null").get();
	}
	
	public boolean isError() {
		return this.either.isRight();
	}
	
	public @NotNull Optional<String> error() {
		return this.either.right();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Result<?> result)) return false;
		
		return this.either.equals(result.either);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.either);
	}
	
	@Override
	public String toString() {
		return "Result{either=" + this.either + "}";
	}
	//endregion
}
