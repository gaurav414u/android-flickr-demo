package com.gauravbhola.gcache.customlru;

import java.util.HashMap;

/**
 * A custom made LRUCache with item remove callback support
 */
public class LRUCache {
    private int capacity;
    DoublyLinkedList dll;
    HashMap<Integer, DoublyLinkedList.Node> map;
    private ItemRemoveCallback mItemRemoveCallback;

    public interface ItemRemoveCallback {
        void onRemove(int item);
    }


    public LRUCache(int capacity, ItemRemoveCallback itemRemoveCallback) {
        this.capacity = capacity;
        dll = new DoublyLinkedList();
        map = new HashMap<>();
        mItemRemoveCallback = itemRemoveCallback;
    }

    public void print(String message) {
        System.out.println(message);
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }

        //invalidate the map.get(key) node in linked list
        this.invalidate(key);

        return map.get(key).element;
    }

    private void invalidate(int key) {
        DoublyLinkedList.Node node = map.get(key);
        dll.remove(node);
        dll.addFirst(node);
    }

    public void printMap() {
        System.out.println(map.toString());
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            //invalidate the map.get(key) node in linked list
            this.invalidate(key);
            //update the value of map.get(key)
            map.get(key).element = value;
            return;
        }
        if (map.size() == capacity) {
            //remove the last value
            DoublyLinkedList.Node node = dll.removeLast();
            //need the key for the last value to clean up the map
            map.remove(node.key);
            if (mItemRemoveCallback != null) {
                mItemRemoveCallback.onRemove(node.key);
            }
        }

        DoublyLinkedList.Node node  = dll.addFirst(value, key);
        map.put(key, node);
    }
}
