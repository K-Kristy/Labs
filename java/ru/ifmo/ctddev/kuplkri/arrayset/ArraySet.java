package ru.ifmo.ctddev.kuplkri.arrayset;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private E[] elements;
    private int size;
    private Comparator<? super E> customComparator;


    @SuppressWarnings({"All"})
    public ArraySet() {
        elements = (E[]) new Object[0];
    }

    public ArraySet(Collection<E> values) {
        createSortedArraySet(values);
    }

    public ArraySet(Collection<E> values, Comparator<? super E> customComparator) {
        this.customComparator = customComparator;
        createSortedArraySet(values);
    }

    private ArraySet(E[] values, Comparator<? super E> customComparator) {
        this.customComparator = customComparator;
        elements = values;

        this.size = values.length;
    }

    @SuppressWarnings({"All"})
    private void createSortedArraySet(final Collection<E> values) {
        NavigableSet<E> arrayToSet = new TreeSet<>(customComparator);
        arrayToSet.addAll(values);
        elements = (E[]) arrayToSet.toArray();

        this.size = elements.length;
    }

    @Override
    public E lower(final E e) {
        Integer ind = lowerIndex(e);
        return ind == null ? null : elements[ind];
    }

    @Override
    public E floor(final E e) {
        Integer ind = floorIndex(e);
        return ind == null ? null : elements[ind];
    }

    @Override
    public E ceiling(final E e) {
        Integer ind = ceilingIndex(e);
        return ind == null ? null : elements[ind];
    }

    @Override
    public E higher(final E e) {
        Integer ind = higherIndex(e);
        return ind == null ? null : elements[ind];
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("ReturnOfInnerClass")
    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public NavigableSet<E> descendingSet() {
        Comparator<? super E> comparator = customComparator == null ?
                ((Comparator<E>) (ob1, ob2) -> ((Comparable<? super E>) ob1).compareTo((E) ob2)).reversed()
                : customComparator.reversed();
        return new ArraySet<>(Arrays.asList(Arrays.copyOf(elements, size)), comparator);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive,
                                  final E toElement, final boolean toInclusive) {
        if (size == 0) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }

        Integer fromInd = fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        Integer toInd = toInclusive ? floorIndex(toElement) : lowerIndex(toElement);

        if (fromInd == null || toInd == null || toInd < fromInd) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }

        return new ArraySet<>(Arrays.asList(Arrays.copyOfRange(elements, fromInd, toInd + 1)), customComparator);
    }

    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        if (size == 0) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }

        return subSet(elements[0], true, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        if (size == 0) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }

        Integer fromIndex = inclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        if (fromIndex == null) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }

        return new ArraySet<>(Arrays.copyOfRange(elements, fromIndex, size), customComparator);
    }


    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(final E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (size == 0) {
            throw new NoSuchElementException("ArraySet is empty!");
        }

        return elements[0];
    }

    @Override
    public E last() {
        if (size == 0) {
            throw new NoSuchElementException("ArraySet is empty!");
        }

        return elements[size - 1];
    }

    @Override
    public Comparator<? super E> comparator() {
        return customComparator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if (size == 0) {
            return false;
        }
        return find((E) o) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (E e : elements) {
            if (!contains(e)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("");
    }

    private int find(E x) {
        int low = 0;
        int high = size;
        int mid = 0;

        while (low < high) {
            mid = low + high >>> 1;
            if (compare(x, elements[mid]) == 0) {
                return mid;
            } else {
                if (compare(x, elements[mid]) < 0) {
                    high = mid;
                } else {
                    low = mid + 1;
                }
            }
        }

        return (compare(x, elements[mid]) > 0 ? mid + 1 : mid) * -1 - 1;
    }

    private class ArraySetIterator<T> implements Iterator<T> {
        private int nextElement;

        @Override
        public boolean hasNext() {
            return nextElement != size;
        }

        @SuppressWarnings("All")
        @Override
        public T next() {
            return (T) elements[nextElement++];
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(Object ob1, Object ob2) {
        return customComparator == null ? ((Comparable<? super E>) ob1).compareTo((E) ob2)
                : customComparator.compare((E) ob1, (E) ob2);
    }

    private Integer lowerIndex(final E e) {
        if (size == 0) {
            return null;
        }

        int higherIndex = find(e);

        if (higherIndex == -1 || higherIndex == 0) {
            return null;
        }

        if (higherIndex > 0) {
            return higherIndex - 1;
        } else {
            int index = -higherIndex - 2;
            return index == size ? null : index;
        }
    }


    private Integer floorIndex(final E e) {
        if (size == 0) {
            return null;
        }

        int higherIndex = find(e);

        if (higherIndex == -1) {
            return null;
        }

        if (higherIndex >= 0) {
            return higherIndex;
        } else {
            int index = -higherIndex - 2;
            return index == size ? null : index;
        }
    }

    private Integer ceilingIndex(final E e) {
        if (size == 0) {
            return null;
        }

        int higherIndex = find(e);

        if (higherIndex > size - 1) {
            return null;
        }

        if (higherIndex >= 0) {
            return higherIndex;
        } else {
            int index = -higherIndex - 1;
            return index == size ? null : index;
        }
    }

    private Integer higherIndex(final E e) {
        if (size == 0) {
            return null;
        }

        int higherIndex = find(e);

        if (higherIndex >= size - 1) {
            return null;
        }

        if (higherIndex >= 0) {
            return higherIndex + 1;
        } else {
            int index = -higherIndex - 1;
            return index == size ? null : index;
        }
    }
}
