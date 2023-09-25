package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class Pair<F, S> {
	
	private final F first;
	private final S second;
	
	private Pair(@Nullable F first, @Nullable S second) {
		this.first = first;
		this.second = second;
	}
	
	public static <T, U> @NotNull Pair<T, U> of(@Nullable T first, @Nullable U second) {
		return new Pair<>(first, second);
	}
	
	public @Nullable F getFirst() {
		return this.first;
	}
	
	public @Nullable S getSecond() {
		return this.second;
	}
	
	public @NotNull Pair<S, F> swap() {
		return Pair.of(this.second, this.first);
	}
	
	public <T> @NotNull Pair<T, S> withFirst(@Nullable T first) {
		return Pair.of(first, this.second);
	}
	
	public <T> @NotNull Pair<F, T> withSecond(@Nullable T second) {
		return Pair.of(this.first, second);
	}
	
	public <T> @NotNull Pair<T, S> mapFirst(@NotNull Function<? super F, ? extends T> mapper) {
		return Pair.of(Objects.requireNonNull(mapper, "Mapper must not be null").apply(this.first), this.second);
	}
	
	public <T> @NotNull Pair<F, T> mapSecond(@NotNull Function<? super S, ? extends T> mapper) {
		return Pair.of(this.first, Objects.requireNonNull(mapper, "Mapper must not be null").apply(this.second));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pair<?, ?> pair)) return false;
		
		if (!Objects.equals(this.first, pair.first)) return false;
		return Objects.equals(this.second, pair.second);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second);
	}
	
	@Override
	public String toString() {
		return "Pair{first=" + this.first + ", second=" + this.second + "}";
	}
	//endregion
}
