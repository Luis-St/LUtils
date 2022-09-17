package net.luis.utils.collection;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

/**
 *
 * @author Luis-st
 *
 */

public class StreamList<T> extends AbstractList<T> implements Stream<T> {
	
	private final List<T> list;
	
	public StreamList() {
		this(Lists.newArrayList());
	}
	
	public StreamList(Stream<T> stream) {
		this(stream.collect(Collectors.toList()));
	}
	
	public StreamList(List<T> list) {
		this.list = list;
	}
	
	@Override
	public T get(int index) {
		return this.list.get(index);
	}
	
	@Override
	public int size() {
		return this.list.size();
	}
	
	private Stream<T> asStream() {
		return this.list.stream();
	}
	
	@Override
	public Spliterator<T> spliterator() {
		return this.asStream().spliterator();
	}
	
	@Override
	public boolean isParallel() {
		return this.asStream().isParallel();
	}
	
	@Override
	public StreamList<T> sequential() {
		return new StreamList<>(this.asStream().sequential());
	}
	
	@Override
	public StreamList<T> parallel() {
		return new StreamList<>(this.asStream().parallel());
	}
	
	@Override
	public StreamList<T> unordered() {
		return new StreamList<>(this.asStream().unordered());
	}
	
	@Override
	public StreamList<T> onClose(Runnable closeHandler) {
		return new StreamList<>(this.asStream().onClose(closeHandler));
	}
	
	@Override
	public void close() {
		this.asStream().close();
	}
	
	@Override
	public StreamList<T> filter(Predicate<? super T> predicate) {
		return new StreamList<>(this.asStream().filter(predicate));
	}
	
	@Override
	public <R> StreamList<R> map(Function<? super T, ? extends R> mapper) {
		return new StreamList<>(this.asStream().map(mapper));
	}
	
	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return this.asStream().mapToInt(mapper);
	}
	
	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return this.asStream().mapToLong(mapper);
	}
	
	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return this.asStream().mapToDouble(mapper);
	}
	
	@Override
	public <R> StreamList<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return new StreamList<>(this.asStream().flatMap(mapper));
	}
	
	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return this.asStream().flatMapToInt(mapper);
	}
	
	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return this.asStream().flatMapToLong(mapper);
	}
	
	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return this.asStream().flatMapToDouble(mapper);
	}
	
	@Override
	public StreamList<T> distinct() {
		return new StreamList<>(this.asStream().distinct());
	}
	
	@Override
	public StreamList<T> sorted() {
		return new StreamList<>(this.asStream().sorted());
	}
	
	@Override
	public StreamList<T> sorted(Comparator<? super T> comparator) {
		return new StreamList<>(this.asStream().sorted(comparator));
	}
	
	@Override
	public StreamList<T> peek(Consumer<? super T> action) {
		return new StreamList<>(this.asStream().peek(action));
	}
	
	@Override
	public StreamList<T> limit(long maxSize) {
		return new StreamList<>(this.asStream().limit(maxSize));
	}
	
	@Override
	public StreamList<T> skip(long n) {
		return new StreamList<>(this.asStream().skip(n));
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		super.forEach(action);
	}
	
	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		this.asStream().forEachOrdered(action);
	}
	
	@Override
	@SuppressWarnings("hiding")
	public <T> T[] toArray(IntFunction<T[]> generator) {
		return super.toArray(generator);
	}
	
	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return this.asStream().reduce(identity, accumulator);
	}
	
	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		return this.asStream().reduce(accumulator);
	}
	
	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return this.asStream().reduce(identity, accumulator, combiner);
	}
	
	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return this.asStream().collect(supplier, accumulator, combiner);
	}
	
	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return this.asStream().collect(collector);
	}
	
	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		return this.asStream().min(comparator);
	}
	
	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		return this.asStream().max(comparator);
	}
	
	@Override
	public long count() {
		return this.asStream().count();
	}
	
	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		return this.asStream().anyMatch(predicate);
	}
	
	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		return this.asStream().allMatch(predicate);
	}
	
	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		return this.asStream().noneMatch(predicate);
	}
	
	@Override
	public Optional<T> findFirst() {
		return this.asStream().findFirst();
	}
	
	@Override
	public Optional<T> findAny() {
		return this.asStream().findAny();
	}
	
}
