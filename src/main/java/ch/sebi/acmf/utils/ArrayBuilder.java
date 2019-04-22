package ch.sebi.acmf.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Sebastian on 20.07.2017.
 */
public class ArrayBuilder<T> {
    private List<T> arrayList = new ArrayList<>();

    public ArrayBuilder() {
    }

    public ArrayBuilder(T[] currentArray) {
        arrayList.addAll(Arrays.asList(currentArray));
    }

    public ArrayBuilder<T> append(T[] a) {
        arrayList.addAll(Arrays.asList(a));
        return this;
    }

    public ArrayBuilder<T> append(T a) {
        arrayList.add(a);
        return this;
    }

    public ArrayBuilder<T> insert(int offset, T[] a) {
        List<T> newArrayList = new ArrayList<>();
        newArrayList.addAll(arrayList.subList(0, offset));
        newArrayList.addAll(Arrays.asList(a));
        newArrayList.addAll(arrayList.subList(offset, arrayList.size()));
        arrayList = newArrayList;
        return this;
    }

    public ArrayBuilder<T> insert(int offset, T a) {
        List<T> newArrayList = new ArrayList<>();
        newArrayList.addAll(arrayList.subList(0, offset));
        newArrayList.add(a);
        newArrayList.addAll(arrayList.subList(offset, arrayList.size()));
        arrayList = newArrayList;
        return this;
    }

    public T[] toArray() {
        return (T[]) arrayList.toArray();
    }

    public List<T> toList() {
        return arrayList;
    }

    public Stream<T> stream(){
        return arrayList.stream();
    }

    public Stream<T> parallelStream() {
        return arrayList.parallelStream();
    }

    public static <T> ArrayBuilder<T> create(T[] a) {
        return new ArrayBuilder(a);
    }

    public static <T> ArrayBuilder<T> create() {
        return new ArrayBuilder<>();
    }
}
