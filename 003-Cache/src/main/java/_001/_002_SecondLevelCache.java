
package _001;

import java.io.IOException;

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
//If we are going to use second-level caching for Employee class, let us add the mapping element required to tell Hibernate to cache Employee 
//instances using read-write strategy.
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
class _002Emp{
	
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
	
	public _002Emp() {	}

	public _002Emp(String name, String address, double salary) {
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

public class _002_SecondLevelCache {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("002.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		System.out.println("1-Session");
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_002Emp emp1 = new _002Emp("Bimal","Pune",23456);
		session.save(emp1);
		tx.commit();
		session.close();
		
		System.out.println("2-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		_002Emp employees=(_002Emp)session.get(_002Emp.class, new Integer(1));
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("3-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees=(_002Emp)session.get(_002Emp.class, new Integer(1));
		employees.setName("BJ");
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("4-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees=(_002Emp)session.get(_002Emp.class, new Integer(1));
		System.out.println(employees);
		tx.commit();
		session.close();
		
	}
	/*
	OUTPUT:
	1-Session
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	
	2-Session
	Hibernate: select emp0_.ENO as ENO1_0_0_, emp0_.EADDRESS as EADDRESS2_0_0_, emp0_.ENAME as ENAME3_0_0_, emp0_.ESALARY as ESALARY4_0_0_ from EMP emp0_ where emp0_.ENO=?
	1 Bimal Pune 23456.0
	
	3-Session
	1 BJ Pune 23456.0
	
	4-Session
	1 BJ Pune 23456.0
	 */
}
