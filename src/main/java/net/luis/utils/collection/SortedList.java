package net.luis.utils.collection;

import com.google.common.collect.Lists;
import net.luis.utils.annotation.Ignored;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A list which is after every modification sorted<br>
 * using the set comparator or if it is {@code null} the natural order.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the elements
 */
public class SortedList<E> extends AbstractList<E> {
	
	/**
	 * The internal list.
	 */
	private final List<E> internalList;
	/**
	 * The comparator used to sort the list.
	 */
	private Comparator<E> comparator;
	
	/**
	 * Constructs a new empty sorted list.<br>
	 * The comparator is set to {@code null}.<br>
	 * Internally, a {@link ArrayList} is used.<br>
	 */
	public SortedList() {
		this(Lists.newArrayList());
	}
	
	/**
	 * Constructs a new empty sorted list with the given comparator.<br>
	 * Internally, a {@link ArrayList} is used.<br>
	 * @param comparator The comparator used to sort the list
	 */
	public SortedList(@Nullable Comparator<E> comparator) {
		this(Lists.newArrayList(), comparator);
	}
	
	/**
	 * Constructs a new sorted list with the given elements.<br>
	 * The comparator is set to {@code null}.<br>
	 * The elements are wrapped in an internal {@link ArrayList}.<br>
	 * @param elements The elements to add
	 */
	@SafeVarargs
	public SortedList(E @NotNull ... elements) {
		this(Lists.newArrayList(elements));
	}
	
	/**
	 * Constructs a new sorted list with the given elements and the comparator.<br>
	 * The elements are wrapped in an internal {@link ArrayList}.<br>
	 * @param comparator The comparator used to sort the list
	 * @param elements The elements to add
	 */
	@SafeVarargs
	public SortedList(@Nullable Comparator<E> comparator, E @NotNull ... elements) {
		this(Lists.newArrayList(elements), comparator);
	}
	
	/**
	 * Constructs a new sorted list with from the given list.<br>
	 * The comparator is set to {@code null}.<br>
	 * The list is not copied, it is used directly.<br>
	 * @param elements The list to use
	 */
	public SortedList(@NotNull List<E> elements) {
		this(elements, null);
	}
	
	/**
	 * Constructs a new sorted list from the given list and the comparator.<br>
	 * The list is not copied, it is used directly.<br>
	 * @param elements The list to use
	 * @param comparator The comparator used to sort the list
	 */
	public SortedList(@NotNull List<E> elements, @Nullable Comparator<E> comparator) {
		this.internalList = elements;
		this.comparator = comparator;
	}
	
	/**
	 * Sets the comparator used to sort the list.<br>
	 * @param comparator The comparator to set
	 */
	public void setComparator(@Nullable Comparator<E> comparator) {
		this.comparator = comparator;
		this.internalList.sort(comparator);
	}
	
	/**
	 * {@inheritDoc}<br>
	 * The list is sorted after the element is added.
	 */
	@Override
	public E set(int index, @NotNull E element) {
		Objects.requireNonNull(element, "Element must not be null");
		E old = this.internalList.set(index, element);
		this.internalList.sort(this.comparator);
		return old;
	}
	
	/**
	 * {@inheritDoc}<br>
	 * The list is sorted after the element is added.
	 */
	@Override
	public void add(int index, @NotNull E element) {
		Objects.requireNonNull(element, "Element must not be null");
		this.internalList.add(index, element);
		this.internalList.sort(this.comparator);
	}
	
	/**
	 * {@inheritDoc}<br>
	 * The list is sorted after the element is removed.
	 */
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
	public int size() {
		return this.internalList.size();
	}
	
	/**
	 * The list is sorted with the comparator of this list.<br>
	 * @param comparator The ignored comparator
	 */
	@Override
	public void sort(@Ignored.Always @Nullable Comparator<? super E> comparator) {
		super.sort(this.comparator);
	}
}
