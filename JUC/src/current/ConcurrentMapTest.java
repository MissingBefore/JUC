package current;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapTest {

	public static void main(String[] args) {
		ConcurrentMap<Integer,String> concurrentHashMap = new ConcurrentHashMap<Integer,String>();
		concurrentHashMap.putIfAbsent(1, "小明");
		/*
		 * putIfAbsent如果已经存在key则返回已有的node
		 */
		String putIfAbsent = concurrentHashMap.putIfAbsent(1, "小红");
		System.out.println(putIfAbsent);
		Set<Entry<Integer, String>> entrySet = concurrentHashMap.entrySet();
		for (Entry<Integer, String> entry : entrySet) {
			System.out.println(entry);
		}
	}

}
