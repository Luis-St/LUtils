package net.luis.utils.collection;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SortedList<E> extends AbstractList<E> {
	
	private final List<E> internalList;
	private Comparator<E> comparator;
	
	public SortedList() {
		this(Lists.newArrayList());
	}
	
	public SortedList(@Nullable Comparator<E> comparator) {
		this(Lists.newArrayList(), comparator);
	}
	
	@SafeVarargs
	public SortedList(E... elements) {
		this(Lists.newArrayList(elements));
	}
	
	@SafeVarargs
	public SortedList(@Nullable Comparator<E> comparator, E... elements) {
		this(Lists.newArrayList(elements), comparator);
	}
	
	public SortedList(@NotNull List<E> elements) {
		this.internalList = elements;
	}
	
	public SortedList(@NotNull List<E> elements, @Nullable Comparator<E> comparator) {
		this.internalList = elements;
		this.comparator = comparator;
	}
	
	public void setComparator(@Nullable Comparator<E> comparator) {
		this.comparator = Objects.requireNonNull(comparator);
		this.internalList.sort(comparator);
	}
	
	@Override
	public E set(int index, E element) {
		E old = this.internalList.set(index, element);
		this.internalList.sort(this.comparator);
		return old;
	}
	
	@Override
	public void add(int index, E element) {
		this.internalList.add(index, element);
		this.internalList.sort(this.comparator);
	}
	
	@Override
	public boolean remove(Object o) {
		boolean removed = this.internalList.remove(o);
		this.internalList.sort(this.comparator);
		return removed;
	}
	
	@Override
	public E get(int index) {
		return this.internalList.get(index);
	}
	
	@Override
	public int size() {
		return this.internalList.size();
	}
	
	@Override
	public void sort(Comparator<? super E> comparator) {
		throw new UnsupportedOperationException("Use setComparator() instead");
	}
}
