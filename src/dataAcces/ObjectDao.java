package dataAcces;

import java.sql.SQLException;
import java.util.List;

import objeto.Clase;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import procesamiento.clasificacion.Configuracion;

public class ObjectDao {
	private SessionFactory sessionFactory;
	
	//Session session = null;
	/**
	 * Instancia singleton
	 */
	private static ObjectDao instance;

	private ObjectDao() {
		try {
			System.out.println("Inicalizando Hibernate");
			sessionFactory = new Configuration().configure().buildSessionFactory();
			//session = getSessionFactory().openSession();
			System.out.println("terminado la inicializacion de Hibernate");
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}
	
	public static ObjectDao getInstance() {
		if (instance == null)
			instance = new ObjectDao();
		return instance;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void save(Object object) {
		try {
			Session session = getSessionFactory().openSession();
			session.beginTransaction();

			session.saveOrUpdate(object);

			session.getTransaction().commit();
			session.connection().commit();
			session.connection().close();
			session.close();

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public List qryAll(String className) {
		try {
			Session session = getSessionFactory().openSession();
			session.beginTransaction();

			List result = session.createQuery("from " + className).list();

			session.getTransaction().commit();
			session.connection().close();
			session.close();

			return result;
		} 
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * Retorna todas las clases excepto la que tiene el nombre pasado como parametro
	 * @return
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List qryAllClases(String excepClass) throws Exception {
		if (excepClass != null){
				Session session = getSessionFactory().openSession();
				//session.beginTransaction();
				String queryStr = "from Clase where nombre != '"+excepClass+"'";
				
				List result = session.createQuery(queryStr).list();
	
				//session.getTransaction().commit();
				session.connection().close();
				session.close();
	
				return result;

		}
		return qryAll(Clase.class.getName());
	}

	/**
	 * Busca una clase con un nombre dado
	 * @param nombreClase
	 * @return
	 */
	public Clase findClase(String nombreClase) {
		try {
			Session session = getSessionFactory().openSession();
			//session.beginTransaction();

			Clase result = (Clase) session.createQuery("from Clase where nombre = '" + nombreClase + "'").uniqueResult();

			//session.getTransaction().commit();
			session.connection().close();
			session.close();

			return result;
		} 
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Busca la configuracion para un sistema dado
	 * @param nombreClase
	 * @return
	 */
	public Configuracion findConfiguracion(String nombreSistema) {
		try {
			Session session = getSessionFactory().openSession();
			//session.beginTransaction();

			Configuracion result = (Configuracion) session.createQuery("from Configuracion where nombreSistema = '" + nombreSistema + "'").uniqueResult();

			//session.getTransaction().commit();
			session.connection().close();
			session.close();

			return result;
		} 
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
