package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Tuple like class that holds two values of different types.<br>
 *
 * @author Luis-St
 *
 * @param <F> The type of the first value
 * @param <S> The type of the second value
 */
public class Pair<F, S> {
	
	/**
	 * The first value of the pair.<br>
	 */
	private final F first;
	/**
	 * The second value of the pair.<br>
	 */
	private final S second;
	
	/**
	 * Constructs a new {@link Pair} with the specified first and second value.<br>
	 * @param first The first value
	 * @param second The second value
	 * @see #of(Object, Object)
	 */
	private Pair(@Nullable F first, @Nullable S second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Creates a new {@link Pair} with the specified first and second value.<br>
	 * @param first The first value
	 * @param second The second value
	 * @return The created pair instance
	 * @param <T> The type of the first value
	 * @param <U> The type of the second value
	 */
	public static <T, U> @NotNull Pair<T, U> of(@Nullable T first, @Nullable U second) {
		return new Pair<>(first, second);
	}
	
	/**
	 * @return The first value of the pair
	 */
	public F getFirst() {
		return this.first;
	}
	
	/**
	 * @return The second value of the pair
	 */
	public S getSecond() {
		return this.second;
	}
	
	/**
	 * Swaps the first and second value of the pair.<br>
	 * @return A new pair with the swapped values
	 */
	public @NotNull Pair<S, F> swap() {
		return Pair.of(this.second, this.first);
	}
	
	/**
	 * Maps the first value of the pair to a new value using the specified mapper.<br>
	 * @param mapper The mapper function
	 * @return A new pair with the mapped first value
	 * @param <T> The new type of the first value
	 */
	public <T> @NotNull Pair<T, S> mapFirst(@NotNull Function<? super F, ? extends T> mapper) {
		return Pair.of(Objects.requireNonNull(mapper, "Mapper must not be null").apply(this.first), this.second);
	}
	
	/**
	 * Maps the second value of the pair to a new value using the specified mapper.<br>
	 * @param mapper The mapper function
	 * @return A new pair with the mapped second value
	 * @param <T> The new type of the second value
	 */
	public <T> @NotNull Pair<F, T> mapSecond(@NotNull Function<? super S, ? extends T> mapper) {
		return Pair.of(this.first, Objects.requireNonNull(mapper, "Mapper must not be null").apply(this.second));
	}
	
	/**
	 * Creates a new pair with the specified first value.<br>
	 * @param first The new first value replacing the old one
	 * @return A new pair with the specified first value
	 * @param <T> The new type of the first value
	 */
	public <T> @NotNull Pair<T, S> withFirst(@Nullable T first) {
		return Pair.of(first, this.second);
	}
	
	/**
	 * Creates a new pair with the specified second value.<br>
	 * @param second The new second value replacing the old one
	 * @return A new pair with the specified second value
	 * @param <T> The new type of the second value
	 */
	public <T> @NotNull Pair<F, T> withSecond(@Nullable T second) {
		return Pair.of(this.first, second);
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
