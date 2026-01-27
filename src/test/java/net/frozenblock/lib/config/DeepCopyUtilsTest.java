/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config;

import net.frozenblock.lib.config.api.instance.util.DeepCopyUtils;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DeepCopyUtilsTest {

    static class SimplePojo {
        public int a;
        public String b;
        public NestedPojo nested;

        public SimplePojo() {}

        public SimplePojo(int a, String b, NestedPojo nested) {
            this.a = a;
            this.b = b;
            this.nested = nested;
        }
    }

    static class NestedPojo {
        public double x;
        public List<String> list;

        public NestedPojo() {}

        public NestedPojo(double x, List<String> list) {
            this.x = x;
            this.list = list;
        }
    }

    static class Node {
        public String name;
        public Node next;

        public Node() {}

        public Node(String name) { this.name = name; }
    }

    @Test
    public void testDeepCopyPrimitivesAndImmutable() {
        String s = "hello";
        String s2 = DeepCopyUtils.deepCopy(s);
        assertSame(s, s2);

        Integer i = 42;
        Integer i2 = DeepCopyUtils.deepCopy(i);
        assertSame(i, i2);
    }

    @Test
    public void testDeepCopySimplePojo() {
        NestedPojo nested = new NestedPojo(3.14, new ArrayList<>(List.of("a", "b")));
        SimplePojo original = new SimplePojo(5, "test", nested);

        SimplePojo copy = DeepCopyUtils.deepCopy(original);
        assertNotNull(copy);
        assertNotSame(original, copy);
        assertEquals(original.a, copy.a);
        assertEquals(original.b, copy.b);
        assertNotNull(copy.nested);
        assertNotSame(original.nested, copy.nested);
        assertEquals(original.nested.x, copy.nested.x);
        assertEquals(original.nested.list, copy.nested.list);
    }

    @Test
    public void testDeepCopyArray() {
        String[] arr = new String[] {"x", "y"};
        String[] copy = DeepCopyUtils.deepCopy(arr);
        assertNotSame(arr, copy);
        assertArrayEquals(arr, copy);

        int[] prim = new int[] {1,2,3};
        int[] primCopy = DeepCopyUtils.deepCopy(prim);
        assertNotSame(prim, primCopy);
        assertArrayEquals(prim, primCopy);
    }

    @Test
    public void testDeepCopyCollectionAndMap() {
        List<SimplePojo> list = new ArrayList<>();
        list.add(new SimplePojo(1, "a", new NestedPojo(1.0, List.of("z"))));
        list.add(new SimplePojo(2, "b", new NestedPojo(2.0, List.of("y"))));

        List<?> copyList = DeepCopyUtils.deepCopy(list);
        assertNotSame(list, copyList);
        assertEquals(list.size(), copyList.size());

        Map<String, SimplePojo> map = new HashMap<>();
        map.put("k", new SimplePojo(7, "v", new NestedPojo(7.0, List.of("p"))));

        Map<?, ?> copyMap = DeepCopyUtils.deepCopy(map);
        assertNotSame(map, copyMap);
        assertEquals(map.size(), copyMap.size());
    }

    @Test
    public void testDeepCopyCyclicGraph() {
        Node a = new Node("A");
        Node b = new Node("B");
        a.next = b;
        b.next = a; // cycle

        Node copy = DeepCopyUtils.deepCopy(a);
        assertNotSame(a, copy);
        assertNotNull(copy.next);
        assertNotSame(b, copy.next);
        assertSame(copy, copy.next.next);
    }

    @Test
    public void testDeepCopyIntoExistingDestination() {
        NestedPojo nested = new NestedPojo(9.9, new ArrayList<>(List.of("x")));
        SimplePojo original = new SimplePojo(10, "orig", nested);
        SimplePojo dest = new SimplePojo();

        DeepCopyUtils.deepCopyInto(original, dest, false);
        assertEquals(10, dest.a);
        assertEquals("orig", dest.b);
        assertNotNull(dest.nested);
        assertNotSame(original.nested, dest.nested);
    }
}

