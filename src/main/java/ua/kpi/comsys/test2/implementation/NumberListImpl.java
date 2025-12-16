/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import ua.kpi.comsys.test2.NumberList;

/**
 * Implementation for NumberList.
 */
public class NumberListImpl implements NumberList {

    // Doubly Linked List Node
    private static class Node {
        Byte value;
        Node next;
        Node prev;

        Node(Byte value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    
    // Default constructor
    public NumberListImpl() {
        this.size = 0;
    }

    // Constructor from File
    public NumberListImpl(File file) {
        this();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            initFromDecimalString(line);
        } catch (IOException e) {
            // Ignored
        }
    }

    // Constructor from String
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    // Helper: Strictly parses Decimal String
    private void initFromDecimalString(String value) {
        if (value == null) return;
        String cleanNum = value.trim();
        
        if (cleanNum.isEmpty()) return;

        // Validation: contains only digits
        for (char c : cleanNum.toCharArray()) {
            if (!Character.isDigit(c)) {
                return; // Invalid input -> empty list
            }
        }

        for (char c : cleanNum.toCharArray()) {
            add((byte) (c - '0'));
        }
    }

    public void saveList(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }

    public static int getRecordBookNumber() {
        return 3101;
    }

    /**
     * CRITICAL FIX HERE:
     * This method now modifies 'this' list instead of returning a new one.
     */
    public NumberListImpl changeScale() {
        // 1. Get current value (Decimal)
        String decimalStr = this.getRawString();
        
        if (decimalStr.isEmpty()) decimalStr = "0";

        BigInteger decimalVal = new BigInteger(decimalStr);
        
        // 2. Convert to Base 3 (Ternary)
        String ternaryStr = decimalVal.toString(3);

        // 3. CLEAR the current list
        this.clear();

        // 4. Fill THIS list with new digits
        for (char c : ternaryStr.toCharArray()) {
            this.add((byte) (c - '0'));
        }
        
        // 5. Return this instance
        return this;
    }

    public NumberListImpl additionalOperation(NumberList arg) {
        String s1 = this.toDecimalString();
        String s2;
        
        if (arg instanceof NumberListImpl) {
            s2 = ((NumberListImpl) arg).toDecimalString();
        } else {
            s2 = arg.toString();
        }
        
        if (s1.isEmpty()) s1 = "0";
        if (s2 == null || s2.isEmpty()) s2 = "0";

        BigInteger val1 = new BigInteger(s1);
        BigInteger val2 = new BigInteger(s2);

        // For addition, we usually return a NEW result
        return new NumberListImpl(val1.add(val2).toString());
    }

    public String toDecimalString() {
        // Since we are just storing digits, for this task usually 
        // toDecimalString simply returns the raw representation for simple storage
        return getRawString();
    }

    // Returns raw digits stored in the list
    private String getRawString() {
        if (head == null) return "";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.value);
            current = current.next;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getRawString();
    }

    // --- Standard List Methods ---

    @Override
    public boolean add(Byte e) {
        if (e == null) throw new NullPointerException();
        Node newNode = new Node(e);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (Node x = head; x != null; x = x.next) {
                if (x.value == null) { unlink(x); return true; }
            }
        } else {
            for (Node x = head; x != null; x = x.next) {
                if (o.equals(x.value)) { unlink(x); return true; }
            }
        }
        return false;
    }
    
    private void unlink(Node x) {
        final Node next = x.next;
        final Node prev = x.prev;

        if (prev == null) head = next;
        else prev.next = next;

        if (next == null) tail = prev;
        else next.prev = prev;

        x.value = null;
        size--;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        return getNode(index).value;
    }

    @Override
    public Byte set(int index, Byte element) {
        if (element == null) throw new NullPointerException();
        Node x = getNode(index);
        Byte oldVal = x.value;
        x.value = element;
        return oldVal;
    }

    @Override
    public Byte remove(int index) {
        Node x = getNode(index);
        Byte element = x.value;
        unlink(x);
        return element;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node x = head;
        if (index < (size >> 1)) {
            for (int i = 0; i < index; i++) x = x.next;
        } else {
            x = tail;
            for (int i = size - 1; i > index; i--) x = x.prev;
        }
        return x;
    }

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public boolean contains(Object o) { return indexOf(o) != -1; }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            @Override public boolean hasNext() { return current != null; }
            @Override public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (Node x = head; x != null; x = x.next) arr[i++] = x.value;
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node x = head; x != null; x = x.next) result[i++] = x.value;
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) if (!contains(e)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) if (add(e)) modified = true;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (contains(e)) { remove(e); modified = true; }
        }
        return modified;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (Node x = head; x != null; x = x.next) {
            if (o == null ? x.value == null : o.equals(x.value)) return index;
            index++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        for (Node x = tail; x != null; x = x.prev) {
            if (o == null ? x.value == null : o.equals(x.value)) return index;
            index--;
        }
        return -1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List)) return false;
        List<?> other = (List<?>) o;
        if (this.size() != other.size()) return false;
        Iterator<Byte> it1 = this.iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object o1 = it1.next();
            Object o2 = it2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
        }
        return true;
    }

    // Unimplemented methods
    @Override public boolean addAll(int index, Collection<? extends Byte> c) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public void add(int index, Byte element) { throw new UnsupportedOperationException(); }
    @Override public ListIterator<Byte> listIterator(int index) { throw new UnsupportedOperationException(); }
    @Override public List<Byte> subList(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }

    @Override
    public ListIterator<Byte> listIterator() {
        return new ListIterator<Byte>() {
             private Node current = head;
             private int index = 0;
            @Override public boolean hasNext() { return index < size; }
            @Override public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                index++;
                return val;
            }
            @Override public boolean hasPrevious() { return false; }
            @Override public Byte previous() { return null; }
            @Override public int nextIndex() { return index; }
            @Override public int previousIndex() { return index - 1; }
            @Override public void remove() { throw new UnsupportedOperationException(); }
            @Override public void set(Byte e) { throw new UnsupportedOperationException(); }
            @Override public void add(Byte e) { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        Byte temp = node1.value;
        node1.value = node2.value;
        node2.value = temp;
        return true;
    }

    @Override
    public void sortAscending() {
        ArrayList<Byte> temp = new ArrayList<>();
        for (Byte b : this) temp.add(b);
        Collections.sort(temp);
        this.clear();
        for (Byte b : temp) this.add(b);
    }

    @Override
    public void sortDescending() {
        ArrayList<Byte> temp = new ArrayList<>();
        for (Byte b : this) temp.add(b);
        Collections.sort(temp, Collections.reverseOrder());
        this.clear();
        for (Byte b : temp) this.add(b);
    }

    @Override
    public void shiftLeft() {
        add((byte) 0);
    }

    @Override
    public void shiftRight() {
        if (size > 0) remove(size - 1);
    }
}
