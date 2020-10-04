package current;

import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapTest {

	public static void main(String[] args) {
		ConcurrentSkipListMap<String,Object> concurrentSkipListMap = new ConcurrentSkipListMap<String, Object>();
		concurrentSkipListMap.pollFirstEntry();
	}

}
