/* 
---------
IDENTITY:
--------- 
Use MySQL DB. 
The IDENTITY type is supported by MySQL but not by ORACLE. 
GenerationType.IDENTITY will use AUTO_INCREMENT

drop table EMP;

CREATE TABLE EMP (
  ENO INT NOT NULL AUTO_INCREMENT,
  ENAME VARCHAR(15) NOT NULL,
  EADDRESS VARCHAR(100) NOT NULL,
  ESALARY INT,
  PRIMARY KEY (ENO)
)

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
class _020Emp{
	
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
	
	public _020Emp() {	}

	public _020Emp(String name, String address, double salary) {
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

public class _020_SecondLevelCache {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/020.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		System.out.println("1-Session");
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_020Emp emp1 = new _020Emp("Bimal","Pune",23456);
		session.save(emp1);
		tx.commit();
		session.close();
		
		System.out.println("2-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		List<_020Emp> employees = (List<_020Emp>)session.createQuery(" FROM _020Emp").list();
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("3-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_020Emp>)session.createQuery(" FROM _020Emp").list();
		employees.get(employees.size()-1).setName("BJ");
		System.out.println(employees);
		tx.commit();
		session.close();
		
		System.out.println("4-Session");
		session = sf.openSession();
		tx = session.beginTransaction();
		employees = (List<_020Emp>)session.createQuery(" FROM _020Emp").list();
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
