/* 
---------------
TABLE SEQUENCE:
---------------
Use Oracle DB

drop table EMP;
drop table GENERATOR_TABLE;

CREATE TABLE EMP (
  ENO INT,
  ENAME VARCHAR(255),
  EADDRESS VARCHAR(100),
  ESALARY INT,
  CONSTRAINT ENO_PK PRIMARY KEY (ENO)
);

select * from EMP;
select * from GENERATOR_TABLE;

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
import javax.persistence.TableGenerator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _002Emp{
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="EMPLOYEE_TABLE_SEQUENCE")
	@TableGenerator(
		    name="EMPLOYEE_TABLE_SEQUENCE", //generator name
		    table="GENERATOR_TABLE",		//table name which stores the sequence
		    pkColumnName = "key", 			//stores the table name for which next sequence is saved
		    valueColumnName = "next",		//stores the next sequence
		    pkColumnValue="EMP",
		    allocationSize=1
		)
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

public class _002_TableSequence {
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/002.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_002_TableSequence demo = new _002_TableSequence();
		demo.insert(sf);
		demo.selectAll(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_002Emp emp1 = new _002Emp("Bimal","Pune",23456);
			_002Emp emp2 = new _002Emp("meghna","Pune",98765);
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
	Hibernate: select tbl.next from GENERATOR_TABLE tbl where tbl.key=? for update
	Hibernate: update GENERATOR_TABLE set next=?  where next=? and key=?
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY, ENO) values (?, ?, ?, ?)
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY, ENO) values (?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void selectAll(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_002Emp> employees = (List<_002Emp>)session.createQuery(" FROM _002Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp_t0_.ENO as ENO1_0_, emp_t0_.EADDRESS as EADDRESS2_0_, emp_t0_.ENAME as ENAME3_0_, emp_t0_.ESALARY as ESALARY4_0_ from EMP emp_t0
	_
	[4 Bimal Pune 23456.0, 5 meghna Pune 98765.0, 7 Bimal Pune 23456.0, 8 meghna Pune 98765.0, 10 Bimal Pune 23456.0, 11 meghna Pune 98765.0, 13 Bimal Pun
	e 23456.0, 14 meghna Pune 98765.0, 16 Bimal Pune 23456.0, 17 meghna Pune 98765.0, 19 Bimal Pune 23456.0, 20 meghna Pune 98765.0, 21 Bimal Pune 23456.0
	, 22 meghna Pune 98765.0, 23 Bimal Pune 23456.0, 24 meghna Pune 98765.0, 25 Bimal Pune 23456.0, 26 meghna Pune 98765.0]
	 */
}
