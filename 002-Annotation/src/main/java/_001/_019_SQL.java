package _001;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.transform.Transformers;

public class _019_SQL{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/019.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_019Dept dept = new _019Dept("Aladdin Product Group");
		_019Emp emp1 = new _019Emp(dept, "Bimal","Pune",23456);
		session.save(dept);
		session.save(emp1);
		
		SQLQuery query = session.createSQLQuery("SELECT * FROM EMP WHERE ENO = :eno");
		query.setParameter("eno", 1);
		query.addEntity(_019Emp.class);
		List<_019Emp> employees1 = (List<_019Emp>)query.list();
		System.out.println(employees1);
		
		query = session.createSQLQuery("SELECT E.*, A.* FROM EMP E, DEPT A WHERE E.DNO=A.DNO AND E.ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addEntity(_019Emp.class);
		query.addEntity(_019Dept.class);
		List<Object[]> result1= query.list(); 
		for (Object[] obj : result1) System.out.println(obj[0] + " " + obj[1]);
		
		query = session.createSQLQuery("SELECT ENO, ENAME FROM EMP WHERE ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addScalar("ENO");
		query.addScalar("ENAME");
		List<Object[]> result2= query.list(); 
		for (Object[] obj : result2) System.out.println(obj[0] + " " + obj[1]);
		
		query = session.createSQLQuery("SELECT ENO, ENAME FROM EMP WHERE ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addScalar("ENO");
		query.addScalar("ENAME");
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> result3= query.list(); 
		for (Map obj : result3) System.out.println(obj.get("ENO") + " " + obj.get("ENAME"));
		
		query = session.createSQLQuery("SELECT ENO, ENAME FROM EMP WHERE ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addScalar("ENO");
		query.addScalar("ENAME");
		query.setResultTransformer(Transformers.TO_LIST);
		List<List> result4= query.list(); for (List obj : result4) System.out.println(obj.get(0) + " " + obj.get(1));
		
		query = session.createSQLQuery("SELECT E.*, A.DNAME as DEPTNAME FROM EMP E, DEPT A WHERE E.DNO=A.DNO AND E.ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addEntity(_019Emp.class);
		query.addScalar("DEPTNAME");
		List<Object[]> result6= query.list(); 
		for (Object[] obj : result6) System.out.println(obj[0] + " " + obj[1]);
		
		query = session.createSQLQuery("SELECT ENAME FROM EMP WHERE ENO = :employee_id");
		query.setParameter("employee_id", 1);
		query.addScalar("ENAME");
		query.setResultTransformer(Transformers.aliasToBean(_019Emp.class));
		List<_019Emp> employees5 = query.list();
		System.out.println(employees5);
		
		tx.commit();
		session.close();
	}
}
