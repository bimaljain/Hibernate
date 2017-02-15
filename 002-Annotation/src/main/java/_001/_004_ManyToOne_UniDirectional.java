/* 
In Many-To-One Unidirectional mapping, one table has a foreign key column that references the primary key of associated table.
By Unidirectional relationship means only one side navigation is possible (EMP to DEPT in this example).

Here we have first created DEPT table followed by EMP table as EMP table contains a foreign key referring to DEPT table.
drop table DEPT;
drop table EMP;

CREATE TABLE DEPT (
  DNO INT NOT NULL AUTO_INCREMENT,
  DNAME VARCHAR(255) NOT NULL,
  PRIMARY KEY (DNO)
  )

CREATE TABLE EMP (
  ENO INT NOT NULL AUTO_INCREMENT,
  DNO INT NOT NULL,
  ENAME VARCHAR(15) NOT NULL,
  EADDRESS VARCHAR(100) NOT NULL,
  ESALARY INT,
  PRIMARY KEY (ENO),
  CONSTRAINT EMP_DEPT FOREIGN KEY (DNO) REFERENCES DEPT (DNO) ON UPDATE CASCADE ON DELETE CASCADE
)

select * from EMP;
select * from DEPT;

@ManyToOne indicates that Many EMP can refer to one DEPT. 
optional=false means this relationship becomes mandatory , no EMP row can be saved without a DEPT reference. 
@JoinColumn says that there is a column DNO in EMP table which will refer(foreign key) to primary key of the DEPT table. 
Here we have persisted DEPT class first in order to meet foreign key constraint (not null), then we have persisting EMP.
*/

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
class _004Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="DNO")
	_004Dept dept;
	
//	If we set cascading on dept property, then we don't have to save dept separately.
// 	@ManyToOne(optional = false, cascade=CascadeType.ALL)
//	@JoinColumn(name="DNO")
//	_004Dept dept;

	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _004Emp() {	}

	public _004Emp(_004Dept dept, String name, String address, double salary) {
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
	
	public _004Dept getDept() {
		return dept;
	}

	public void setDept(_004Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _004Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _004Dept() {	}

	public _004Dept(String name) {
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

public class _004_ManyToOne_UniDirectional{
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/004.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_004_ManyToOne_UniDirectional demo = new _004_ManyToOne_UniDirectional();
		demo.insert(sf);
		demo.select(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_004Dept dept = new _004Dept("Aladdin Product Group");
			_004Emp emp1 = new _004Emp(dept, "Bimal","Pune",23456);
			session.save(dept); // not needed incase you set cascade
			session.save(emp1);
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
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		try {
			List<_004Emp> employees = (List<_004Emp>)session.createQuery(" FROM _001._004Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_1_, emp0_.EADDRESS as EADDRESS2_1_, emp0_.DNO as DNO5_1_, emp0_.ENAME as ENAME3_1_, emp0_.ESALARY as ESALARY4_1_ from EMP emp0_
	Hibernate: select dept0_.DNO as DNO1_0_0_, dept0_.DNAME as DNAME2_0_0_ from DEPT dept0_ where dept0_.DNO=?
	
	[1 1 Aladdin Product Group Bimal Pune 23456.0]
	 */
}
