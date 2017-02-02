

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
class _004Emp{
	
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
	
	public _004Emp() {	}

	public _004Emp(String name, String address, double salary) {
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

public class _004_QueryCache {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("004.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		System.out.println("1-Session");
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_004Emp emp1 = new _004Emp("Bimal","Pune",23456);
		session.save(emp1);
		tx.commit();
		session.close();
		
		System.out.println("2-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		List<_004Emp> employees = (List<_004Emp>)session.createQuery(" FROM _004Emp where id=1").setCacheable(true).list();
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("3-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_004Emp>)session.createQuery(" FROM _004Emp  where id=1").setCacheable(true).list();
		employees.get(employees.size()-1).setName("BJ");
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("4-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_004Emp>)session.createQuery(" FROM _004Emp  where id=1").setCacheable(true).list();
		System.out.println(employees);
		tx.commit();
		session.close();
		
	}
	/*
	OUTPUT:
	1-Session
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	
	2-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_ where emp0_.ENO=1
	[1 BJ Pune 23456.0]
	
	3-Session
	[1 BJ Pune 23456.0]
	
	4-Session
	[1 BJ Pune 23456.0]
	 */
}
