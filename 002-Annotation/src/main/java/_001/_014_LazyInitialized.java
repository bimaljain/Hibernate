/*
----------------------------
Lazy & Eager Initialization:
----------------------------
1.Lazy initialization in hibernate means hibernate does not initialize the entire object graph of entity object but only initialize member variables 
which are at the first level. Second level (and so on) of member variables are only initialized when you access them using getters. 
2. Hibernate hands over a proxy object instead of returning the actual entity. Proxy object is a subclass of the actual entity. When we attempt to 
access the second level of member variables, the proxy will query DB, populate the member variable and finally call the super methods, to run our code.
3. Lazy initialization is the default behaviour. You can configure hibernate to do Eager initialization.

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

import javax.persistence.CascadeType;
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
class _014Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="DNO")
	_014Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _014Emp() {	}

	public _014Emp(_014Dept dept, String name, String address, double salary) {
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
	
	public _014Dept getDept() {
		return dept;
	}

	public void setDept(_014Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}

}

@Entity
@Table(name="DEPT")
class _014Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _014Dept() {	}

	public _014Dept(String name) {
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

class _014_LazyInitialized{
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/014.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_014_LazyInitialized demo = new _014_LazyInitialized();
		demo.insert(sf);
		demo.selectAll(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_014Dept dept = new _014Dept("Aladdin Product Group");
			_014Emp emp = new _014Emp(dept, "Bimal","Pune",23456);
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
	public void selectAll(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_014Emp> employees = (List<_014Emp>)session.createQuery(" FROM _001._014Emp").list();
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
	Hibernate: select emp_l0_.ENO as ENO1_1_, emp_l0_.EADDRESS as EADDRESS2_1_, emp_l0_.DNO as DNO5_1_, emp_l0_.ENAME as ENAME3_1_, emp_l0_.ESALARY as ESA
	LARY4_1_ from EMP emp_l0_
	Bimal
	org.hibernate.LazyInitializationException: could not initialize proxy - no Session
	 */
}
