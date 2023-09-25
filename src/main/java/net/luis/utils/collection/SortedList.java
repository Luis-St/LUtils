package net.luis.utils.collection;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.Ignored;
import org.jetbrains.annotations.*;

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
	public SortedList(E @NotNull ... elements) {
		this(Lists.newArrayList(elements));
	}
	
	@SafeVarargs
	public SortedList(@Nullable Comparator<E> comparator, E @NotNull ... elements) {
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
		this.comparator = comparator;
		this.internalList.sort(comparator);
	}
	
	@Override
	public E set(@Range(from = 0, to = Integer.MAX_VALUE) int index, @Nullable E element) {
		E old = this.internalList.set(index, element);
		this.internalList.sort(this.comparator);
		return old;
	}
	
	@Override
	public void add(@Range(from = 0, to = Integer.MAX_VALUE) int index, @Nullable E element) {
		this.internalList.add(index, element);
		this.internalList.sort(this.comparator);
	}
	
	@Override
	public boolean remove(@Nullable Object object) {
		boolean removed = this.internalList.remove(object);
		this.internalList.sort(this.comparator);
		return removed;
	}
	
	@Override
	public @NotNull E get(int index) {
		return this.internalList.get(index);
	}
	
	@Override
	public @Range(from = 0, to = Integer.MAX_VALUE) int size() {
		return this.internalList.size();
	}
	
	@Override
	public void sort(@Ignored.Always @Nullable Comparator<? super E> comparator) {
		super.sort(this.comparator);
	}
}
