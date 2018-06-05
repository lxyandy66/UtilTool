package tool.data.processor;

public interface EntityCreatable <E>{
	
	  /**从特定对象中获取实体对象，如从Excel行中获取对象
	 * @param log 可以获取到实体对象的原始记录s
	 * @return
	 * @throws Exception
	 */
	E getEntityFromObject(Object log);
	  
	/**将实体转换为字符串数组
	 * @param entity
	 * @return
	 */
	String[] toStringArray(E entity);
}
