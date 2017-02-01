/* 
drop table EMP;
drop table DEPT;

CREATE TABLE DEPT (
  DNO INT NOT NULL AUTO_INCREMENT,
  DNAME VARCHAR(255) NOT NULL,
  PRIMARY KEY (DNO)
  );

CREATE TABLE EMP (
  ENO INT NOT NULL AUTO_INCREMENT,
  DNO INT NOT NULL,
  ENAME VARCHAR(15) NOT NULL,
  EADDRESS VARCHAR(100) NOT NULL,
  ESALARY INT,
  PRIMARY KEY (ENO),
  CONSTRAINT EMP_DEPT FOREIGN KEY (DNO) REFERENCES DEPT (DNO) ON UPDATE CASCADE ON DELETE CASCADE
);

select * from EMP;
select * from DEPT;
*/

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _015Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false, fetch=FetchType.EAGER)
    @JoinColumn(name="DNO")
	_015Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _015Emp() {	}

	public _015Emp(_015Dept dept, String name, String address, double salary) {
		this.dept=dept;
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
	
	public _015Dept getDept() {
		return dept;
	}

	public void setDept(_015Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _015Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _015Dept() {	}

	public _015Dept(String name) {
		this.name = name;
	}

	public int getEid() { return did; }
	public void setEid(int did) { this.did = did; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String toString(){
		return this.did + " " + this.name;
	}
}

public class _015_EagerInitialized{
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/015.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_015_EagerInitialized demo = new _015_EagerInitialized();
		demo.insert(sf);
		demo.selectAll(sf);
	}
	
	public void insert(SessionFactory sf ){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_015Dept dept = new _015Dept("Aladdin Product Group");
			_015Emp emp = new _015Emp(dept, "Bimal","Pune",23456);
			session.save(dept);
			session.save(emp);
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
	Hibernate: insert into DEPT (DNAME) values (?)
	Hibernate: insert into EMP (EADDRESS, DNO, ENAME, ESALARY) values (?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void selectAll(SessionFactory sf ) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_015Emp> employees = (List<_015Emp>)session.createQuery(" FROM _001._015Emp").list();
			System.out.println(employees.get(0).getName());
			//closing the session
			session.close();
			System.out.println(employees.get(0).getDept());
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp_e0_.ENO as ENO1_1_, emp_e0_.EADDRESS as EADDRESS2_1_, emp_e0_.DNO as DNO5_1_, emp_e0_.ENAME as ENAME3_1_, emp_e0_.ESALARY as ESA
	LARY4_1_ from EMP emp_e0_
	Hibernate: select dept_0_.DNO as DNO1_0_0_, dept_0_.DNAME as DNAME2_0_0_ from DEPT dept_0_ where dept_0_.DNO=?
	Bimal
	2 Aladdin Product Group
	 */
}
