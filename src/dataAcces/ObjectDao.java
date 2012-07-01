package dataAcces;

import java.sql.SQLException;
import java.util.List;

import objeto.Clase;
import objeto.Grado;
import objeto.RubroCalidad;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import procesamiento.clasificacion.Configuracion;
import sun.font.CreatedFontTracker;

public class ObjectDao {
	public static final String NULL_VALUE = "null";
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
	
	public Object save(Object object) {
		try {
			Session session = getSessionFactory().openSession();
			session.beginTransaction();

			session.saveOrUpdate(object);

			session.getTransaction().commit();
			session.connection().commit();
			session.connection().close();
			session.close();
			
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
	}
	
	public List<RubroCalidad> qryAllRubroCalidad(String idSistema) {
		try {
			Session session = getSessionFactory().openSession();
			session.beginTransaction();
			String qryString = "from " + RubroCalidad.class.getName() + " where sistema = :idSistema";
			Query query = session.createQuery(qryString);
			query.setParameter("idSistema", idSistema, Hibernate.STRING);
			List<RubroCalidad> result = query.list();

			session.getTransaction().commit();
			session.connection().close();
			session.close();

			return result;
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Busca clases con los filtros pasados como parámetro
	 * @return
	 * @throws SQLException 
	 */
	public List qryClases(String nombreClase, Boolean indeterminado, Boolean objetoReferencia) throws Exception {
		Session session = getSessionFactory().openSession();
		//session.beginTransaction();
		String qryStr = "from Clase where nombre = COALESCE(:nombre, nombre)" 
			+ "AND indeterminado = COALESCE(:indeterminado, indeterminado) "
			+ "AND objetoReferencia = COALESCE(:objetoReferencia, objetoReferencia)";
		Query query = session.createQuery(qryStr);
		query.setParameter("nombre", nombreClase, Hibernate.STRING);
		query.setParameter("indeterminado", indeterminado, Hibernate.BOOLEAN);
		query.setParameter("objetoReferencia", objetoReferencia, Hibernate.BOOLEAN);
		List result = query.list();

		//session.getTransaction().commit();
		session.connection().close();
		session.close();

		return result;
	}

	/**
	 * Busca una clase con un nombre dado
	 * @param nombreClase
	 * @return
	 */
	public Clase findClase(String nombreClase, Boolean indeterminado, Boolean objetoReferencia) {
		try{
			List<Clase> clases = qryClases(nombreClase, indeterminado, objetoReferencia);
			if (clases != null && clases.size() > 0)
				return clases.get(0);
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Retorna la cantidad de objetos clasificados en la base de datos
	 * @return
	 */
	public Integer getCantidadObjetos() {
		try {
			Session session = getSessionFactory().openSession();
			//session.beginTransaction();

			Integer result = (Integer) session.createQuery("select count(*) from Objeto").uniqueResult();

			//session.getTransaction().commit();
			session.connection().close();
			session.close();

			return result;
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void delete(Object object) {
		try {
			Session session = getSessionFactory().openSession();
			session.beginTransaction();

			session.delete(object);

			session.getTransaction().commit();
			session.connection().commit();
			session.connection().close();
			session.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Grado> qryAllGrados(String idSistema) {
		try{
			Session session = getSessionFactory().openSession();
			//session.beginTransaction();
			String qryStr = "from Grado where sistema = :idSistema order by nombre";
			Query query = session.createQuery(qryStr);
			query.setParameter("idSistema", idSistema, Hibernate.STRING);
			List<Grado> result = query.list();
			session.connection().close();
			session.close();

			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}
