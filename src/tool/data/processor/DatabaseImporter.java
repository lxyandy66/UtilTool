package tool.data.processor;

import java.io.IOException;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public abstract class DatabaseImporter {

	private final String ERROR_NOT_INIT = "没有初始化管理连接";
	private final String ERROR_EXIST = "已经存在一个活跃的连接";
	private final String ERRROR_NOT_ACTIVE = "连接不是活跃的";

	private EntityManager em;
	private EntityTransaction transaction;

	public DatabaseImporter() {
	}

	public void initDataBase(EntityManagerFactory factory) {
		em = factory.createEntityManager();
	}

	public void startTransaction() throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		if (transaction.isActive())
			throw new IOException(ERROR_EXIST);
		transaction = em.getTransaction();
	}

	public void commitTransaction() throws Exception {
		if (!isExistTransaction())
			throw new IOException();
		if (!transaction.isActive())
			throw new Exception(ERRROR_NOT_ACTIVE);
		transaction.commit();
	}

	/**
	 * 提供一个直接的接口，直接通过对EntityManager的处理，进行复杂的操作
	 * 
	 * @param em
	 */
	public abstract void persistTool(EntityManager em);

	public <E> void persistEntity(ArrayList<E> array, boolean hasTransaction) throws Exception {
		if (!hasTransaction)
			persistEntity(array);// 如果不存在连接则调用自动建立连接并提交的方法
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
		if (transaction == null)
			this.startTransaction();

//		em.getTransaction().begin();
		persistTool(em);
		transaction.commit();
//		em.close();
	}

	/**
	 * 用于实现persistTool实现的流程化持久，本方法将自动启用并提交连接
	 * 
	 * @throws Exception
	 */
	public <E> void persistEntity(E obj) throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);

		if (transaction == null)
			this.startTransaction();

		em.persist(obj);
		transaction.commit();
//		if(!em.getTransaction().isActive()) {
//			//不检测多线程同时操作发生抢占会报异常 Transaction is currently active
//			em.getTransaction().begin();
//		}
//		em.persist(obj);
//		em.getTransaction().commit();
	}

	public void closeConnect() throws IOException {
		if (!isInit())
			throw new IOException(ERROR_NOT_INIT);
		em.close();
	}

	public boolean isInit() {
		return em != null;
	}

	public boolean isExistTransaction() {
		return transaction != null;
	}

	/**
	 * 简单的实体持久化方法，使用多线程
	 * 
	 * @param emf    工厂类
	 * @param entity
	 * 
	 *               EntityManager是一种廉价的，非线程安全的对象，应针对单个业务流程，单个工作单元使用一次，然后丢弃。
	 *               不要通过线程共享EM。除非一个事务处理是 工作单元的一部分，就可以对多个事务使用一个EM 。
	 */
	public static <E> void simpleImportThread(EntityManagerFactory emf, E entity) {
		if (entity == null)
			throw new NullPointerException("Entity is null");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				EntityManager em = emf.createEntityManager();
				em.getTransaction().begin();
				;
				em.persist(entity);
				em.getTransaction().commit();
				em.close();
			}
		}).start();
	}

	/**
	 * 简单的实体持久化方法
	 * 
	 * @param emf    工厂类
	 * @param entity
	 * 
	 *               EntityManager是一种廉价的，非线程安全的对象，应针对单个业务流程，单个工作单元使用一次，然后丢弃。
	 *               不要通过线程共享EM。除非一个事务处理是 工作单元的一部分，就可以对多个事务使用一个EM 。
	 */
	public static <E> void simpleImport(EntityManagerFactory emf, E entity) {
		if (entity == null)
			throw new NullPointerException("Entity is null");

		// TODO Auto-generated method stub
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
		em.close();

	}

}
