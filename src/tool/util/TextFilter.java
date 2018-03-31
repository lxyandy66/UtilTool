package tool.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class TextFilter<K,V> {
	public K[] flagArray;
	public K[] withOutArray;
	public Map<K, LinkedList<V>> hashMap=new HashMap<K, LinkedList<V>>();

	public TextFilter(K[] flag) {
		flagArray=flag;
		for (K k : flag) 
			hashMap.put(k, new LinkedList<V>());
	}
	public TextFilter(K[] flag,K[] withOutFlag) {
		flagArray=flag;
		withOutArray=withOutFlag;
		for (K k : flag) 
			hashMap.put(k, new LinkedList<V>());
	}
	
	/**获取该元素对应的HashMap的位置
	 * @param elem
	 */
	private LinkedList<V> getLinkedList(V elem) {
		for(K k:hashMap.keySet()) {
			if(isMatch(elem,k))
				return hashMap.get(k);
		}
		return null;
	}
	
	/**实现根据键值判断元素分类的方法
	 * @param elem 待添加的元素
	 * @param key 分类标记
	 * @return
	 */
	public abstract boolean isMatch(V elem,K key);
	
//	public abstract boolean withOut(V elem,K key);
	
	/**将元素加入过滤器中，并根据分类归类
	 * @param elem
	 */
	public void pull(V elem) {
		LinkedList<V> list=getLinkedList(elem);
		if(list!=null)
			list.add(elem);
	}
	
	public void printAll(){
		for(K key:hashMap.keySet()) {
			outputKey(key);
			for(V elem:hashMap.get(key))
				outputElement(elem);
		}
	}
	
	/** 获取总元素数量
	 * @return
	 */
	public int getTotalElemCount() {
		int count=0;
		for(LinkedList<V> list:hashMap.values())
			count+=list.size();
		return count;
	}
	
	/**实现输出元素的方法
	 * @param elem 
	 */
	public abstract void outputElement(V elem);
	
	
	/**实现输出键值的方法
	 * @param key
	 */
	public abstract void outputKey(K key);
}
