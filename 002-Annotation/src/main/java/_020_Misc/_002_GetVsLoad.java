/*
----------------------------------
Hibernate Session get() vs load():
----------------------------------
Hibernate Session provide different methods to fetch data from database. Two of them are: get() and load(). 

At first look both get() and load() seems similar because both of them fetch the data from database, however there are few differences between them.
get() returns the object by fetching it from database or from hibernate cache whereas load() just returns the reference of an object that might not 
actually exists, it loads the data from database or cache only when you access other properties of the object. 
get() loads the data as soon as it's called whereas load() returns a proxy object and loads data only when it's actually required, so load() is 
better because it support lazy loading.
*/

package _020_Misc;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class _002_GetVsLoad{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("020_Misc/002.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_002Dept dept = new _002Dept("Aladdin Product Group");
		_002Emp emp = new _002Emp(dept, "Bimal","Pune",23456);
		session.save(dept);
		session.save(emp);		
		
		_002Emp emp1 = (_002Emp) session.get(_002Emp.class, new Integer(1));
		System.out.println("Employee get called");
		System.out.println("Employee ID= " + emp1.getName());
		System.out.println("Employee Get Details:: "+ emp1 + "\n");
		tx.commit();
		session.close();
		
		//load Example
		session = sf.openSession();
		_002Emp emp2 = (_002Emp) session.load(_002Emp.class, new Integer(1));
		System.out.println("Employee load called");
		System.out.println("Employee ID= " + emp2.getName());
		System.out.println("Employee load Details:: " + emp2 + "\n");
		session.close();
		
		/* OUTPUT:
		Hibernate: insert into DEPT (DNAME) values (?)
		Hibernate: insert into EMP (EADDRESS, DNO, ENAME, ESALARY) values (?, ?, ?, ?)
		
		Hibernate: select emp0_.ENO as ENO1_1_0_, emp0_.EADDRESS as EADDRESS2_1_0_, emp0_.DNO as DNO5_1_0_, emp0_.ENAME as ENAME3_1_0_, emp0_.ESALARY 
		as ESALARY4_1_0_, dept1_.DNO as DNO1_0_1_, dept1_.DNAME as DNAME2_0_1_ 
		from EMP emp0_ inner join DEPT dept1_ on emp0_.DNO=dept1_.DNO 
		where emp0_.ENO=?
		Employee get called
		Employee ID= Bimal
		Employee Get Details:: 1 46 Aladdin Product Group Bimal Pune 23456.0
		
		Employee load called
		Hibernate: select emp0_.ENO as ENO1_1_0_, emp0_.EADDRESS as EADDRESS2_1_0_, emp0_.DNO as DNO5_1_0_, emp0_.ENAME as ENAME3_1_0_, emp0_.ESALARY 
		as ESALARY4_1_0_, dept1_.DNO as DNO1_0_1_, dept1_.DNAME as DNAME2_0_1_ 
		from EMP emp0_ inner join DEPT dept1_ on emp0_.DNO=dept1_.DNO 
		where emp0_.ENO=?
		Employee ID= Bimal
		Employee load Details:: 1 46 Aladdin Product Group Bimal Pune 23456.0
		*/
				
		//Now let's try to fetch data that doesn't exists in the database.
		
		session = sf.openSession();
		_002Emp emp3 = (_002Emp) session.get(_002Emp.class, new Integer(100));
		System.out.println("Employee get called");
		System.out.println("Employee ID= " + emp3.getName());
		System.out.println("Employee Get Details:: "+ emp3 + "\n");
		session.close();
		/*
		Hibernate: select emp0_.ENO as ENO1_1_0_, emp0_.EADDRESS as EADDRESS2_1_0_, emp0_.DNO as DNO5_1_0_, emp0_.ENAME as ENAME3_1_0_, emp0_.ESALARY as ESALARY4_1_0_, dept1_.DNO as DNO1_0_1_, dept1_.DNAME as DNAME2_0_1_ from EMP emp0_ inner join DEPT dept1_ on emp0_.DNO=dept1_.DNO where emp0_.ENO=?
		Employee get called
		Exception in thread "main" java.lang.NullPointerException
		 */
		
		//load Example
		session = sf.openSession();
		_002Emp emp4 = (_002Emp) session.load(_002Emp.class, new Integer(999));
		System.out.println("Employee load called");
		System.out.println("Employee ID= " + emp4.getName());
		System.out.println("Employee load Details:: " + emp4 + "\n");
		session.close();
		
		/*
		Employee load called
		Hibernate: select emp0_.ENO as ENO1_1_0_, emp0_.EADDRESS as EADDRESS2_1_0_, emp0_.DNO as DNO5_1_0_, emp0_.ENAME as ENAME3_1_0_, emp0_.ESALARY as ESALARY4_1_0_, dept1_.DNO as DNO1_0_1_, dept1_.DNAME as DNAME2_0_1_ from EMP emp0_ inner join DEPT dept1_ on emp0_.DNO=dept1_.DNO where emp0_.ENO=?
		Exception in thread "main" org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [_020_Misc._002Emp#999]
		 */
	}
}

@Entity
@Table(name="EMP")
class _002Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="DNO")
	_002Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _002Emp() {	}

	public _002Emp(_002Dept dept, String name, String address, double salary) {
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
	
	public _002Dept getDept() {
		return dept;
	}

	public void setDept(_002Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _002Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _002Dept() {	}

	public _002Dept(String name) {
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
