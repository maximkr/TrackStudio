package com.trackstudio.tools.tree;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Дерево основано на трех массивах.
 */
@ThreadSafe
public class OrderedTree<T extends Comparable> {

	final ReadWriteLock rwl = new ReentrantReadWriteLock();

	private final Log log = LogFactory.getLog(this.getClass());

	/**
	 * хранилище объектов
	 */
	@GuardedBy("rwl") private T[] elementData;

	/**
	 * координаты родительских узлов. Соответствуют индексам массива elementData
	 */
	@GuardedBy("rwl") private int[] parents;

	/**
	 * число потомков для каждого узла. Индекс массива соответствует индексу elementData. Число потомков нужно, чтобы знать, когда останавливать поиск.
	 */
	@GuardedBy("rwl") private int[] childrenCounter;

	/**
	 * Индексный массив. Нужен для быстрого бинарного поиска объектов. Содержимое массива - индексы elementData, отсортированные по compareTo
	 */
	@GuardedBy("rwl") private int[] index;

	@GuardedBy("rwl") private int[] freeCells;

	private static int INITIAL_CAPACITY = 500;

	private static int FREE_CELL_CAPACITY = 100;

	/**
	 * размер массива elementData и смежных массивов
	 */
	@GuardedBy("rwl") private int size = 0;

	/**
	 * реальное количество элементов в массивах. Отличается от size после удаления элементов.
	 */
	@GuardedBy("rwl") private int realSize = 0;

	@GuardedBy("rwl") private int lastFree = 0;

	public OrderedTree(T o) {
		this.elementData = (T[])new Comparable[INITIAL_CAPACITY];
		this.parents = new int[INITIAL_CAPACITY];
		this.childrenCounter = new int[INITIAL_CAPACITY];
		this.index = new int[INITIAL_CAPACITY];
		this.freeCells = new int[FREE_CELL_CAPACITY];
		Arrays.fill(childrenCounter, 0);
		Arrays.fill(parents, -1);
		Arrays.fill(index, -1);
		Arrays.fill(freeCells, -1);

		// нулевой элемент - всегда корень
		this.elementData[0] = o;
		// родительских у него нет, но он сам себе родитель, получается. Если parents<0, то элемент удален
		this.parents[0] = 0;
		this.index[0] = 0;
		size = 1;
		realSize = 1;
	}

	/**
	 * Обеспечиваем необходимую емкость массивов
	 *
	 * @return емкость
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private int calcFreeCellUnlocked() {
		if (realSize < size) {
			// has free cells, search for it
			if (lastFree == 0) {
				realSize++;
				return realSize - 1;
			} else {
				int pos = freeCells[lastFree - 1];
				freeCells[lastFree - 1] = -1;
				childrenCounter[pos] = 0;
				lastFree--;
				realSize++;
				return pos;
			}
		} else {
			// expand arrays
			if (size >= parents.length - 1) {
				Object[] oldData = elementData;
				int newCapacity = (size * 3 >> 1) + 1;
				childrenCounter = expandArrayUnlocked(childrenCounter, size, newCapacity, 0);
				parents = expandArrayUnlocked(parents, size, newCapacity, -1);
				index = expandArrayUnlocked(index, size, newCapacity, -1);
				elementData = (T[])new Comparable[newCapacity];
				System.arraycopy(oldData, 0, elementData, 0, size);
			}
			size++;
			realSize++;
			return realSize - 1;
		}
	}

	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private int[] expandArrayUnlocked(int[] array, int fromSize, int toSize, int fill) {
		int[] oldArray = array;
		array = new int[toSize];
		Arrays.fill(array, fill);
		System.arraycopy(oldArray, 0, array, 0, fromSize);
		return array;
	}

	/**
	 * Поиск перебором. Нужен на случай, если бинарный поиск не работает (для неComparable элементов).
	 *
	 * @param elem сравниваемый элемент
	 * @return позиция найденного элемента
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private int searchUnlocked(Comparable elem) {
		if (elem == null) {
			for (int i = size - 1; i >= 0; i--) {
				if (elementData[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = size - 1; i >= 0; i--) {
				if (elem.equals(elementData[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Возвращает позицию объекта в хранилище
	 *
	 * @param elem объект, чуью позицию ищем
	 * @return позиция
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private int getElementsPositionUnlocked(T elem) {
		// fint
		if (elem == null) {
			return searchUnlocked(elem);
		} else {
			return binarySearchUnlocked(elem);
		}
	}

	/**
	 * Быстрый бинарный поиск по индексу.
	 * Не блокируется, может вызываться только из методов с установленной rwl lock
	 *
	 * @param elem элемент, который ищем
	 * @return позиция элемента
	 */

	@SuppressWarnings("FieldAccessNotGuarded")
	private int binarySearchUnlocked(T elem) {
		int ind = searchIndexUnlocked(elem);
		if (ind >= 0) {
			return index[ind];
		} else {
			return -1;
		}
	}

	/**
	 * Быстрый бинарный поиск по индексу. Используется классический алгоритм.
	 * Не блокируется, может вызываться только из методов с установленной rwl lock
	 *
	 * @param elem элемент, который изем
	 * @return если возвращается индекс >0, то элемент есть и находится на указанной позиции. Если возвращается отрицательное число, то такого элемента нет, но если бы он был, то находился бы на указанной позиции
	 */
	@SuppressWarnings("FieldAccessNotGuarded")
	private int searchIndexUnlocked(T elem) {
		int bottom = realSize - 1;
		int top = 0;

		int localIndex = (bottom + top) >> 1;
		while (bottom - top >= 0) {
			int pos = index[localIndex];
			if (pos >= 0) {
				int res = elementData[pos].compareTo(elem);
				if (res > 0) {
					bottom = localIndex - 1;
				} else if (res < 0) {
					top = localIndex + 1;

				} else {
					// next position
					return localIndex;
				}
				localIndex = (bottom + top) >> 1;
			} else {
				// experimental: nulls
				bottom = localIndex - 1;
			}
		}
		return -top - 1;
	}

	/**
	 * Переиндексация для одного элемента
	 *
	 * @param ind индекс
	 * @param pos позиция
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private void setIndexUnlocked(int ind, int pos) {
		System.arraycopy(index, ind, index, ind + 1, size - ind);
		index[ind] = pos;
	}

	/**
	 * сжимаем индекс
	 *
	 * @param ind позиция, которую надо затереть.
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private void resetIndexUnlocked(int ind) {
		System.arraycopy(index, ind + 1, index, ind, size - ind);
		index[size - 1] = -1;
	}

	/**
	 * Добавить объект в дерево
	 *
	 * @param to узел, к которому добавляется объект
	 * @param o  объект
	 */
	public void add(T to, T o) {
		rwl.writeLock().lock();
		try {
			int pos = this.getElementsPositionUnlocked(to);
			if (pos >= 0) {
				int freeCell = calcFreeCellUnlocked();
				//ищем позицию для вставки
				int place = searchIndexUnlocked(o);
				if (place < 0) {
					//втыкаем в индекс
					setIndexUnlocked(-1 - place, freeCell);
				} else {
					System.out.println("Not found position for index");
				}

				// помещаем в хранилище, в самый конец
				elementData[freeCell] = o;
				// возвращаем номер элемента в массиве

				int in = freeCell;
				parents[in] = pos;
				childrenCounter[pos]++;
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

    /**
     * Возвращает количество непосредственных потомков у узла
     *
     * @param o узел, для которого возвращаются потомки
     * @return количество потомков или -1, если такого узла нет
     */
    public int getChildrenCount(T o) {
        rwl.readLock().lock();
        try {
            int j = getElementsPositionUnlocked(o);
            if (j >= 0)
                return childrenCounter[j];
            else
                return -1;
        } finally {
            rwl.readLock().unlock();
        }
    }

    /**
     * Возвращает список узлов, дочерних к заданному
     *
     * @param o заданный узел
     * @return список узлов или null, если указанного узла не существует
     */
    public List<T> getChildren(T o) {
        rwl.readLock().lock();
        try {
            int j = getElementsPositionUnlocked(o);
            int[] childrenPositions = getChildrenPositionUnlocked(j);
            List<T> children = new ArrayList<T>(childrenPositions.length);

			for (int pos : childrenPositions) {
				children.add(elementData[pos]);
			}
			return children;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public List<Map.Entry<T, Integer>> getChildrenWithSubchildrenCount(T o) {
		rwl.readLock().lock();
		try {
			int j = getElementsPositionUnlocked(o);
			int[] childrenPositions = getChildrenPositionUnlocked(j);
			List<Map.Entry<T, Integer>> children = new ArrayList<>(childrenPositions.length);

			for (int pos : childrenPositions) {
				children.add(new AbstractMap.SimpleEntry<T, Integer>(elementData[pos], childrenCounter[pos]));
			}
			return children;
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Возвращает позиции дочерних элементов
	 *
	 * @param j позиция заданного узла
	 * @return список позиций
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private int[] getChildrenPositionUnlocked(int j) {
		if (j >= 0) {
			if (childrenCounter[j] == 0) {
				// empty list
				return new int[0];
			} else {
				// сколько элементов искать
				int found = childrenCounter[j];
				int[] ret = new int[found];
				// ищем всех детей за один проход. Поэтому мы не можем за один проход найти всех потомков (только детей)
				for (int i = parents.length - 1; i >= 1; i--) {
					if (parents[i] == j) {
						found--;
						ret[found] = i;
					}
					if (found == 0) {
						break;
					}
				}
				return ret;
			}
		}
		return null;
	}

	/**
	 * Возвращает все дочернии узлы для зааднного
	 *
	 * @param o узел, для которого возвращаем дочерние узлы
	 * @return список дочерних узлов
	 */
	public List<T> getDescendents(T o) {
		rwl.readLock().lock();
		try {

			ArrayList<T> arr = new ArrayList<T>(1000);
			for (Iterator<T> it = new DescendentsIterator(o); it.hasNext(); ) {
				arr.add(it.next());
			}
			return arr;

		} finally {
			rwl.readLock().unlock();
		}

	}

	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private class DescendentsIterator implements Iterator {

		private final boolean[] bitmap;

		int nextPosition = -1;

		public DescendentsIterator(T o) {
			bitmap = new boolean[size];
			int pos = getElementsPositionUnlocked(o);

			if (pos >= 0 && childrenCounter[pos] > 0) { // element which has childrens
				// mark source element
				bitmap[pos] = true;

				// find first element (it's always righter than parent)
				// and mark all already added elements
				for (int j = pos + 1; j < size; j++) {
					int parentsj = parents[j];
					if (parentsj >= 0 && bitmap[parentsj]) {
						bitmap[j] = true;
						nextPosition = j;
						break;
					}
				}
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return nextPosition >= 0;
		}

		public T next() {
			if (nextPosition < 0) {
				throw new NoSuchElementException();
			}

			T ret = elementData[nextPosition];

			// find next element
			int futureNextPosition = -1;
			for (int j = nextPosition + 1; j < size; j++) {
				int parentsj = parents[j];

				// parents set lower then children
				if (parentsj > j) {
					int up = parentsj;
					while (up > j) {
						up = parents[up];
						if (up >= 0 && bitmap[up]) {
							bitmap[parentsj] = true;
							break;
						}
					}
				}

				if (parentsj >= 0 && bitmap[parentsj]) {
					bitmap[j] = true;
					futureNextPosition = j;
					break;
				}
			}
			nextPosition = futureNextPosition;
			return ret;
		}
	}

	/**
	 * Возвращаем родительский узел.
	 *
	 * @param o узел
	 * @return Родительский узел или null, если элемента нет в хранилище, либо это root (черт, тут вилка получается, надо как-то оговаривать заранее)
	 */
	public T getParent(T o) {
		rwl.readLock().lock();
		try {
			int j = getElementsPositionUnlocked(o);
			if (j > 0) {
				return (elementData[parents[j]]);
			} else {
				return null;
			}
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Возвращает предков для указанного узла
	 *
	 * @param o узел
	 * @return список узлов
	 */
	public ArrayList<T> getAncestors(T o) {
		rwl.readLock().lock();
		try {
			int objectPosition = getElementsPositionUnlocked(o);
			ArrayList<T> c = new ArrayList<T>();

			if (objectPosition > 0) {
				objectPosition = parents[objectPosition];
				// Now objectPosition store item parent, add all ancestors to the list
				while (objectPosition > 0) {
					c.add(elementData[objectPosition]);
					objectPosition = parents[objectPosition];
				}
				c.add(elementData[0]);
				Collections.reverse(c);
			}
			return c;
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Возвращает список всех элементов дерева
	 *
	 * @return список элементов
	 */
	public List<T> getContents() {
		rwl.readLock().lock();
		try {
			ArrayList<T> list = new ArrayList<T>(size);
			for (int i = 0; i < size; i++) {
				if (elementData[i] != null) {
					list.add(elementData[i]);
				}
			}
			return list;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public Optional<T> findAny(Predicate<T> pred)
	{
		rwl.readLock().lock();
		try {
			return Arrays.asList(elementData).parallelStream().filter(pred).findAny();
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Иерархическое удаление (или просто вычленение) куска дерева (МЕДЛЕННО)
	 *
	 * @param t               Дерево, в которое складываются удаленные узлы
	 * @param current         текущий узел
	 * @param elementPosition текущая позиция
	 * @param remove          если true, то удаляем, если false - просто обходим
	 */
	@SuppressWarnings("FieldAccessNotGuarded") //This class can be used inside locked methods only
	private void visitLevelUnlocked(OrderedTree<T> t, T current, int elementPosition, boolean remove) {
		int found = childrenCounter[elementPosition];
		if (found > 0) {
			for (int i = 1; i < parents.length; i++) {
				if (parents[i] == elementPosition) {
					found--;
					t.add(current, elementData[i]);
					visitLevelUnlocked(t, elementData[i], i, remove);
				}
				if (found == 0) {
					break;
				}
			}
		}
		if (remove) {
			if (elementData[elementPosition] instanceof Comparable) {
				resetIndexUnlocked(searchIndexUnlocked(elementData[elementPosition]));
			}
			elementData[elementPosition] = null;
			parents[elementPosition] = -1;
			childrenCounter[elementPosition] = -1;
			if (lastFree == freeCells.length) {
				freeCells = expandArrayUnlocked(freeCells, freeCells.length, freeCells.length + FREE_CELL_CAPACITY, -1);
			}
			lastFree++;
			freeCells[lastFree - 1] = elementPosition;

			realSize--;
		}
	}

	/**
	 * Удаляет элемент из дерева
	 *
	 * @param o удаляемый элемент
	 * @return новое дерево, без элемента
	 */
	public OrderedTree<T> remove(T o) {
		rwl.writeLock().lock();
		try {
			int j = getElementsPositionUnlocked(o);
			if (j >= 0) {
				T root = elementData[j];
				childrenCounter[parents[j]]--;
				OrderedTree<T> n = new OrderedTree<T>(root);
				visitLevelUnlocked(n, root, j, true);
				return n;
			}
			return null;
		} finally {
			rwl.writeLock().unlock();
		}
	}

	/**
	 * Возвращает размер дерева
	 *
	 * @return размер дерева
	 */
	public int size() {
		rwl.readLock().lock();
		try {
			return realSize;
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Замещает один узел другим, при этом никакого перемещения под-деревьев не происходит. Узлы, конечно, должны быть уникальными (Быстро)
	 *
	 * @param o    узел, которые замещаем
	 * @param with узел, которым замещаем
	 */
	public void replaceWith(T o, T with) {
		rwl.writeLock().lock();
		try {
			int insertionPoint = getElementsPositionUnlocked(o);
			if (insertionPoint >= 0) {
				elementData[insertionPoint] = with;
				if (o.compareTo(with) != 0) {
					// переиндексация
					setIndexUnlocked(searchIndexUnlocked(with), insertionPoint);
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}
	}

	/**
	 * Возвращает элемент дерева
	 *
	 * @param o элемент
	 * @return элемент дерева
	 */
	public T get(T o) {
		rwl.readLock().lock();
		try {
			int pos = getElementsPositionUnlocked(o);
			if (pos >= 0) {
				return (elementData[pos]);
			} else {
				return null;
			}
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Перемещает ветку дерева из одного узла в другой
	 *
	 * @param from начальный узел
	 * @param to   конечный узел
	 */
	public void moveTree(T from, T to) {
		rwl.writeLock().lock();
		try {
			int j = getElementsPositionUnlocked(from);
			int n = getElementsPositionUnlocked(to);
			if (j >= 0 && n >= 0) {
				childrenCounter[parents[j]]--;
				childrenCounter[n]++;
				parents[j] = n;
			}
		} finally {
			rwl.writeLock().unlock();
		}
	}
}



