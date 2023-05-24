package net.luis.utils.collection;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.Ignored.Always;
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
	
	public SortedList(Comparator<E> comparator) {
		this(Lists.newArrayList(), comparator);
	}
	
	@SafeVarargs
	public SortedList(E... elements) {
		this(Lists.newArrayList(elements));
	}
	
	@SafeVarargs
	public SortedList(Comparator<E> comparator, E... elements) {
		this(Lists.newArrayList(elements), comparator);
	}
	
	public SortedList(List<E> elements) {
		this.internalList = elements;
	}
	
	public SortedList(List<E> elements, Comparator<E> comparator) {
		this.internalList = elements;
		this.comparator = comparator;
	}
	
	public void setComparator(@Nullable Comparator<E> comparator) {
		this.comparator = comparator;
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
	public boolean remove(Object object) {
		boolean removed = this.internalList.remove(object);
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
	public void sort(@Always Comparator<? super E> comparator) {
		super.sort(this.comparator);
	}
}
