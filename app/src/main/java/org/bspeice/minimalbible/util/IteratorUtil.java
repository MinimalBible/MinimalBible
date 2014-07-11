package org.bspeice.minimalbible.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bspeice on 7/11/14.
 */
public class IteratorUtil {
    public static <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<T>();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy;
    }
}
