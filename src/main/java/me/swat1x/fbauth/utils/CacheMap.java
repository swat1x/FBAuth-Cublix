package me.swat1x.fbauth.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private final Map<K, Long> timeMap = new ConcurrentHashMap<K, Long>();
    private long expiryInMillis = 1000;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");


    public CacheMap(long dur, TimeUnit tu) {
        this.expiryInMillis = tu.toMillis(dur);
        initialize();
    }

    void initialize() {
        new CleanerThread().start();
    }

    public V getAndRemove(Object key) {
        V value = super.get(key);
        remove(key);
        return value;
    }

    @Override
    public V put(K key, V value) {
        Date date = new Date();
        timeMap.put(key, date.getTime());
        V returnVal = super.put(key, value);
        return returnVal;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key))
            return put(key, value);
        else
            return get(key);
    }

    class CleanerThread extends Thread {
        @Override
        public void run() {
            System.out.println("Initiating Cleaner Thread..");
            while (true) {
                cleanMap();
                try {
                    Thread.sleep(expiryInMillis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanMap() {
            long currentTime = new Date().getTime();
            for (K key : timeMap.keySet()) {
                if (currentTime > (timeMap.get(key) + expiryInMillis)) {
                    V value = remove(key);
                    timeMap.remove(key);
                    System.out.println("Removing : " + sdf.format(new Date()) + " : " + key + " : " + value);
                }
            }
        }
    }
}
