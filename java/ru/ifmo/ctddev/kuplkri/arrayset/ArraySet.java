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

    @SuppressWarnings({"All"})
    private void createSortedArraySet(final Collection<E> values) {
        NavigableSet<E> arrayToSet = new TreeSet<>(customComparator);
        arrayToSet.addAll(values);
        elements = (E[]) arrayToSet.toArray();

        this.size = elements.length;
    }

    @Override
    public E lower(final E e) {
        int ind = lowerIndex(e);
        return ind == -1 ? null : elements[ind];
    }

    @Override
    public E floor(final E e) {
        int ind = floorIndex(e);
        return ind == -1 ? null : elements[ind];
    }

    @Override
    public E ceiling(final E e) {
        int ind = ceilingIndex(e);
        return ind == -1 ? null : elements[ind];
    }

    @Override
    public E higher(final E e) {
        int ind = higherIndex(e);
        return ind == -1 ? null : elements[ind];
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

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(Arrays.asList(Arrays.copyOf(elements, size)), customComparator.reversed());
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

        int fromInd = fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        int toInd = toInclusive ? floorIndex(toElement) : lowerIndex(toElement);

        if (fromInd == -1 || toInd == -1 || toInd < fromInd) {
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

        return subSet(fromElement, inclusive, elements[size - 1], true);
/*        int fromIndex = inclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        if (fromIndex == -1) {
            return new ArraySet<>(Collections.emptyList(), customComparator);
        }
        return new ArraySet<>(Arrays.asList(Arrays.copyOfRange(elements, fromIndex, size)), customComparator);*/
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
        return find((E) o) != -1;
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
        int i = -1;
        int low = 0;
        int high = size;

        while (low < high) {
            int mid = low + high >>> 1;
            if (compare(x, elements[mid]) == 0) {
                i = mid;
                break;
            } else {
                if (compare(x, elements[mid]) < 0) {
                    high = mid;
                } else {
                    low = mid + 1;
                }
            }
        }

        return i;
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

    private int lowerIndex(final E e) {
        int ind = -1;

        if (size == 0) {
            return ind;
        }

        int high = size;
        if (compare(e, elements[size - 1]) > 0) {
            return size - 1;
        }

        if (compare(e, elements[0]) <= 0) {
            return ind;
        }

        int low = 0;
        while (low < high) {
            int mid = low + high >>> 1;
            if (compare(e, elements[mid]) == 0) {
                ind = mid - 1;
                break;
            } else {
                if (compare(e, elements[mid]) < 0) {
                    high = mid;
                } else {
                    if (compare(e, elements[mid + 1]) > 0) {
                        low = mid + 1;
                    } else {
                        ind = mid;
                        break;
                    }
                }
            }
        }

        return ind;
    }


    private int floorIndex(final E e) {
        int ind = -1;

        if (size == 0) {
            return ind;
        }

        int high = size;
        if (compare(e, elements[0]) < 0) {
            return ind;
        }

        if (compare(e, elements[size - 1]) >= 0) {
            return size - 1;
        }

        int low = 0;
        while (low < high) {
            int mid = low + high >>> 1;
            if (compare(e, elements[mid]) == 0) {
                ind = mid;
                break;
            } else {
                if (compare(e, elements[mid]) < 0) {
                    high = mid;
                } else {
                    if (compare(e, elements[mid + 1]) > 0) {
                        low = mid + 1;
                    } else {
                        if (compare(e, elements[mid + 1]) == 0) {
                            ind = mid + 1;
                        } else {
                            ind = mid;
                        }
                        break;
                    }
                }
            }
        }

        return ind;
    }


    private int ceilingIndex(final E e) {
        int ind = -1;

        if (size == 0) {
            return ind;
        }

        int high = size;
        if (compare(e, elements[size - 1]) > 0) {
            return ind;
        }

        if (compare(e, elements[0]) <= 0) {
            return 0;
        }

        int low = 0;
        while (low < high) {
            int mid = low + high >>> 1;
            if (compare(e, elements[mid]) == 0) {
                ind = mid;
                break;
            } else if (compare(e, elements[mid]) > 0) {
                low = mid + 1;
            } else {
                if (compare(e, elements[mid - 1]) < 0) {
                    high = mid;
                } else {
                    if (compare(e, elements[mid - 1]) == 0) {
                        ind = mid - 1;
                    } else {
                        ind = mid;
                    }
                    break;
                }
            }
        }

        return ind;
    }

    private int higherIndex(final E e) {
        int ind = -1;

        if (size == 0) {
            return ind;
        }

        int high = size;
        if (compare(e, elements[size - 1]) >= 0) {
            return ind;
        }

        if (compare(e, elements[0]) < 0) {
            return 0;
        }

        int low = 0;
        while (low < high) {
            int mid = low + high >>> 1;
            if (compare(e, elements[mid]) == 0) {
                if (size > mid + 1) {
                    ind = mid + 1;
                }
                break;
            } else {
                if (compare(e, elements[mid]) > 0) {
                    low = mid + 1;
                } else {
                    if (compare(e, elements[mid - 1]) < 0) {
                        high = mid;
                    } else {
                        ind = mid;
                        break;
                    }
                }
            }
        }

        return ind;
    }
}
