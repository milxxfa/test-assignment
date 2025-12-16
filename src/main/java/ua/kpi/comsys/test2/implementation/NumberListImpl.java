package ua.kpi.comsys.test2.implementation;

import ua.kpi.comsys.test2.NumberList;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class NumberListImpl implements NumberList {

    private final List<Byte> digits = new ArrayList<>();
    private int base = 10;

    public NumberListImpl() {}

    public NumberListImpl(String value) {
        if (value == null || value.trim().isEmpty()) return;
        if (!value.matches("\\d+")) return;

        for (char c : value.toCharArray()) {
            digits.add((byte) (c - '0'));
        }
    }

    public NumberListImpl(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) return;
            if (!line.matches("\\d+")) return;

            for (char c : line.toCharArray()) {
                digits.add((byte) (c - '0'));
            }
        } catch (IOException ignored) {}
    }

    private BigInteger toDecimal() {
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(base);

        for (Byte d : digits) {
            result = result.multiply(b).add(BigInteger.valueOf(d));
        }
        return result;
    }

    public String toDecimalString() {
        return toDecimal().toString();
    }

    @Override
    public String toString() {
        if (digits.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Byte d : digits) sb.append(d);
        return sb.toString();
    }

    public NumberListImpl changeScale() {
        BigInteger decimal = toDecimal();
        int newBase = 8;

        String converted = decimal.toString(newBase);

        NumberListImpl result = new NumberListImpl();
        result.base = newBase;

        for (char c : converted.toCharArray()) {
            result.digits.add((byte) (c - '0'));
        }
        return result;
    }

    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger a = this.toDecimal();
        BigInteger b = new BigInteger(arg.toString());
        BigInteger res = a.add(b);
        return new NumberListImpl(res.toString());
    }

    public void saveList(File file) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRecordBookNumber() {
        return 3101;
    }

    @Override public int size() { return digits.size(); }
    @Override public boolean isEmpty() { return digits.isEmpty(); }
    @Override public boolean add(Byte aByte) { return digits.add(aByte); }
    @Override public Byte get(int index) { return digits.get(index); }
    @Override public Byte remove(int index) { return digits.remove(index); }
    @Override public void clear() { digits.clear(); }
    @Override public Iterator<Byte> iterator() { return digits.iterator(); }

    @Override
    public boolean swap(int i, int j) {
        if (i < 0 || j < 0 || i >= size() || j >= size()) return false;
        Collections.swap(digits, i, j);
        return true;
    }

    @Override public void sortAscending() { Collections.sort(digits); }
    @Override public void sortDescending() { digits.sort(Collections.reverseOrder()); }
    @Override public void shiftLeft() { digits.add((byte) 0); }
    @Override public void shiftRight() {
        if (!digits.isEmpty()) digits.remove(digits.size() - 1);
    }

    @Override public boolean remove(Object o) { return digits.remove(o); }
    @Override public boolean contains(Object o) { return digits.contains(o); }
    @Override public Object[] toArray() { return digits.toArray(); }
    @Override public <T> T[] toArray(T[] a) { return digits.toArray(a); }
    @Override public boolean containsAll(Collection<?> c) { return digits.containsAll(c); }
    @Override public boolean addAll(Collection<? extends Byte> c) { return digits.addAll(c); }
    @Override public boolean removeAll(Collection<?> c) { return digits.removeAll(c); }
    @Override public boolean retainAll(Collection<?> c) { return digits.retainAll(c); }
    @Override public Byte set(int index, Byte element) { return digits.set(index, element); }
    @Override public void add(int index, Byte element) { digits.add(index, element); }
    @Override public int indexOf(Object o) { return digits.indexOf(o); }
    @Override public int lastIndexOf(Object o) { return digits.lastIndexOf(o); }
    @Override public ListIterator<Byte> listIterator() { return digits.listIterator(); }
    @Override public ListIterator<Byte> listIterator(int index) { return digits.listIterator(index); }
    @Override public List<Byte> subList(int fromIndex, int toIndex) {
        return digits.subList(fromIndex, toIndex);
    }
}

