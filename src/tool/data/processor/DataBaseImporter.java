package tool.data.processor;

import java.io.IOException;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public abstract class DataBaseImporter {

	private final String ERROR_NOT_INIT = "没有初始化管理连接";
	private final String ERROR_EXIST = "已经存在一个活跃的连接";
	private final String ERRROR_NOT_ACTIVE = "连接不是活跃的";

	private EntityManager em;
	private EntityTransaction transaction;

	public DataBaseImporter() {
	}

	public void initDataBase(EntityManagerFactory factory) {
		em = factory.createEntityManager();
	}

	public void startTransaction() throws Exception {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		if (transaction.isActive())
			throw new Exception(ERROR_EXIST);
		transaction = em.getTransaction();
	}
	
	public void commitTransaction() throws Exception {
		if (!isExistTransaction())
			throw new IOException();
		if (!transaction.isActive())
			throw new Exception(ERRROR_NOT_ACTIVE);
		transaction.commit();
	}

	public abstract void persistTool(EntityManager em);

	public <E> void persistEntity(ArrayList<E> array,boolean hasTransaction) throws Exception {
		if(!hasTransaction)
			persistEntity(array);//如果不存在连接则调用自动建立连接并提交的方法
		if (!isExistTransaction())
			throw new IOException();
		if (!transaction.isActive())
			throw new Exception(ERRROR_NOT_ACTIVE);
		for (E e : array) {
			em.persist(e);
		}
	}

	/**
	 * 用于将已处理的数据以ArrayList的形式逐条持久化
	 * 
	 * @param array
	 * @throws Exception
	 */
	public <E> void persistEntity(ArrayList<E> array) throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		em.getTransaction().begin();
		for (E e : array)
			em.persist(e);
		em.getTransaction().commit();
	}

	/**
	 * 用于实现persistTool实现的流程化持久，本方法将自动启用并提交连接
	 * 
	 * @throws Exception
	 */
	public void persistEntity() throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		em.getTransaction().begin();
		persistTool(em);
		em.getTransaction().commit();
		em.close();
	}

	public void closeConnect() throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		em.close();
	}

	private boolean isInit() {
		return em != null;
	}

	private boolean isExistTransaction() {
		return transaction != null;
	}

}
