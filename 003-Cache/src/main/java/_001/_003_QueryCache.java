/*
------------
Query Cache:
------------
1. If we use query instead of using session.get(), hibernate treats queries differently. So simply configuring 2nd level cache will not get the desired caching. 
You have to enable query cache. Remember that Query level cache is different from 2nd level cache.
2. This is an optional feature and requires two additional physical cache regions that hold the cached query results and the timestamps when a table was 
last updated. This is only useful for queries that are run frequently with the same parameters.
3. In order to use query cache:
Include cache provider jar into your project
Enable query cache in hibernate configuration file
Make EVERY query cacheable (not just the first query)
4. Query.setCacheable(true) performs 2 roles:
- If the query cache does not have value, go to the DB, pull up the records and set it in the query cache.
- If the query cache does have value, pull up the records from the query cache.

Below example has NO query caching.
 */
package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
class _003Emp{
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _003Emp() {	}

	public _003Emp(String name, String address, double salary) {
		this.name = name;
		this.address = address;
		this.salary = salary;	}

	public int getEid() { return eid; }
	public void setEid(int eid) { this.eid = eid; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public double getSalary() { return salary; }
	public void setSalary(double salary) { this.salary = salary; }
	
	public String toString(){
		return this.eid + " " + this.name + " " + this.address + " " + this.salary;
	}
}

public class _003_QueryCache {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("003.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		System.out.println("1-Session");
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_003Emp emp1 = new _003Emp("Bimal","Pune",23456);
		session.save(emp1);
		tx.commit();
		session.close();
		
		System.out.println("2-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		List<_003Emp> employees = (List<_003Emp>)session.createQuery(" FROM _003Emp  where id=1").list();
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("3-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_003Emp>)session.createQuery(" FROM _003Emp where id=1").list();
		employees.get(employees.size()-1).setName("BJ");
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("4-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_003Emp>)session.createQuery(" FROM _003Emp where id=1").list();
		System.out.println(employees);
		tx.commit();
		session.close();
		
	}
	/*
	OUTPUT:
	1-Session
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	
	2-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 Bimal Pune 23456.0]
	
	3-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 BJ Pune 23456.0]
	Hibernate: update EMP set EADDRESS=?, ENAME=?, ESALARY=? where ENO=?
	
	4-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 BJ Pune 23456.0]
	 */
}
