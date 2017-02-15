/* 
---------
IDENTITY:
--------- 
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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
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

public class _003_Identity {
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/003.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_003_Identity demo = new _003_Identity();
		demo.insert(sf);
		demo.selectAll(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_003Emp emp1 = new _003Emp("Bimal","Pune",23456);
			_003Emp emp2 = new _003Emp("meghna","Pune",98765);
			session.save(emp1);
			session.save(emp2);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		}finally {
			session.close(); 
		}
	}
	/*
	OUTPUT:
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void selectAll(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_003Emp> employees = (List<_003Emp>)session.createQuery(" FROM _003Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp_i0_.ENO as ENO1_0_, emp_i0_.EADDRESS as EADDRESS2_0_, emp_i0_.ENAME as ENAME3_0_, emp_i0_.ESALARY as ESALARY4_0_ from EMP emp_i0
	_
	[1 Bimal Pune 23456.0, 2 meghna Pune 98765.0]
	 */
}
