/*
Fetching Strategies:
Hibernate have fetch type and fetch mode settings. Fetch type specifies *WHEN* related collections and entities are retrieved, and fetch mode specifies
*HOW* Hibernate retrieves them.

Hibernate has few fetching strategies to optimize the Hibernate generated select statement, so that it can be as efficient as possible. The fetching 
strategy is declared in the mapping relationship to define how Hibernate fetch its related collections and entities. Hibernate defines the following 
fetching strategies: SELECT, SUBSELECT, JOIN, BATCH. 

FetchMode.SELECT and FetchMode.SUBSELECT are legal with both FetchType.EAGER and FetchType.LAZY. The difference is that with FetchType.EAGER an 
additional select query is executed immediately, whereas with FetchType.LAZY it's executed after the first access to the collection. 

FetchMode.JOIN, however, doesn't make sense with FetchType.LAZY. It's always FetchType.EAGER.

Batch size is an additional optimization for FetchMode.SELECT, so that it should be configured by its own annotation (@BatchSize) and has nothing to 
do with FetchMode enumeration itself.

------------------------
@Fetch(FetchMode.SELECT)
------------------------
A second SELECT is used to retrieve the associated entity or collection. Unless you explicitly disable lazy fetching by specifying lazy="false", this 
second select will only be executed when you access the association. This is the default fetching strategy.

Hibernate generated atleast two select statements:
1. Select statement to retrieve all the DEPT
2. Select the related collections (EMP) for each DEPT

Hibernate: select dept0_.DNO as DNO1_0_, dept0_.DNAME as DNAME2_0_ from DEPT dept0_

Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, 
emplist0_.DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
[_003Emp [eid=1, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=2, name=Bimal, address=Pune, salary=23456.0]]

Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, 
emplist0_.DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
[_003Emp [eid=3, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=4, name=Bimal, address=Pune, salary=23456.0]]

---------------------------
@Fetch(FetchMode.SUBSELECT)
---------------------------
A second SELECT is used to retrieve the associated collections for *ALL* entities retrieved in a previous query or fetch. Unless you explicitly disable 
lazy fetching by specifying lazy="false", this second select will only be executed when you access the association.

Hibernate generated two select statements:
1. Select statement to retrieve all the DEPT
2. Select all the related collections in a sub select query.

Hibernate: select dept0_.DNO as DNO1_0_, dept0_.DNAME as DNAME2_0_ from DEPT dept0_

Hibernate: select emplist0_.DNO as DNO5_1_1_, emplist0_.ENO as ENO1_1_1_, emplist0_.ENO as ENO1_1_0_, emplist0_.EADDRESS as EADDRESS2_1_0_, 
emplist0_.DNO as DNO5_1_0_, emplist0_.ENAME as ENAME3_1_0_, emplist0_.ESALARY as ESALARY4_1_0_ from EMP emplist0_ where emplist0_.DNO in 
(select dept0_.DNO from DEPT dept0_)
[_003Emp [eid=1, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=2, name=Bimal, address=Pune, salary=23456.0]]
[_003Emp [eid=3, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=4, name=Bimal, address=Pune, salary=23456.0]]

----------------------
@Fetch(FetchMode.JOIN)
----------------------
Hibernate retrieves the associated instance or collection in the same SELECT, using an OUTER JOIN. The join fetching strategy will disabled the lazy 
loading of all its related collections.
Hibernate generated only one select statement, it retrieve all its related collections (EMP) when the DEPT is initialized.

Hibernate: select dept0_.DNO as DNO1_0_, dept0_.DNAME as DNAME2_0_ from DEPT dept0_
Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, 
emplist0_.DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, 
emplist0_.DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
[_003Emp [eid=1, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=2, name=Bimal, address=Pune, salary=23456.0]]
[_003Emp [eid=3, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=4, name=Bimal, address=Pune, salary=23456.0]]

-----
BATCH
-----
An optimization strategy for select fetching. Hibernate retrieves a batch of entity instances or collections in a single SELECT by specifying a list of 
primary or foreign keys. The batch-size fetching strategy is not to define how many records inside in the collections are loaded. Instead, it defines 
how many collections should be loaded.

If you have 5 DEPT records in the database, the Hibernate's default fetching strategies will generate 5+1 select statements and hit the database.
1. Select statement to retrieve all the Stock records.
2. Select its related collection
3. Select its related collection
4. Select its related collection
5. Select its related collection
6. Select its related collection
The generated queries are not efficient and caused a serious performance issue.

Enabled the batch-size=3 fetching strategy:
Now, Hibernate will per-fetch the collections, with a select *in* statement. If you have 5 DEPT records, it will generate 3 select statements.
1. Select statement to retrieve all the DEPT records.
2. Select In statement to per-fetch its related collections (3 collections a time)
3. Select In statement to per-fetch its related collections (next 2 collections a time)
With batch-size enabled, it simplify the select statements from 6 select statements to 3 select statements.

Hibernate: select dept0_.DNO as DNO1_0_, dept0_.DNAME as DNAME2_0_ from DEPT dept0_
Hibernate: select emplist0_.DNO as DNO5_1_1_, emplist0_.ENO as ENO1_1_1_, emplist0_.ENO as ENO1_1_0_, emplist0_.EADDRESS as EADDRESS2_1_0_, 
emplist0_.DNO as DNO5_1_0_, emplist0_.ENAME as ENAME3_1_0_, emplist0_.ESALARY as ESALARY4_1_0_ from EMP emplist0_ where emplist0_.DNO in (?, ?)
[_003Emp [eid=1, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=2, name=Bimal, address=Pune, salary=23456.0]]
[_003Emp [eid=3, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=4, name=Bimal, address=Pune, salary=23456.0]]

Hibernate: select emplist0_.DNO as DNO5_1_1_, emplist0_.ENO as ENO1_1_1_, emplist0_.ENO as ENO1_1_0_, emplist0_.EADDRESS as EADDRESS2_1_0_, 
emplist0_.DNO as DNO5_1_0_, emplist0_.ENAME as ENAME3_1_0_, emplist0_.ESALARY as ESALARY4_1_0_ from EMP emplist0_ where emplist0_.DNO in (?, ?)
[_003Emp [eid=5, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=6, name=Bimal, address=Pune, salary=23456.0]]
[_003Emp [eid=7, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=8, name=Bimal, address=Pune, salary=23456.0]]

Hibernate: select emplist0_.DNO as DNO5_1_1_, emplist0_.ENO as ENO1_1_1_, emplist0_.ENO as ENO1_1_0_, emplist0_.EADDRESS as EADDRESS2_1_0_, 
emplist0_.DNO as DNO5_1_0_, emplist0_.ENAME as ENAME3_1_0_, emplist0_.ESALARY as ESALARY4_1_0_ from EMP emplist0_ where emplist0_.DNO=?
[_003Emp [eid=9, name=Bimal, address=Pune, salary=23456.0], _003Emp [eid=10, name=Bimal, address=Pune, salary=23456.0]]

-----------
DB DETAILS:
-----------
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

package _020_Misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.cfg.Configuration;

public class _003_FetchingStrategy{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("020_Misc/003.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_003Dept dept = new _003Dept();
		dept.setName("Aladdin Product Group");
		_003Emp emp1 = new _003Emp(dept, "Bimal","Pune",23456);
		_003Emp emp2 = new _003Emp(dept, "Bimal","Pune",23456);
		List<_003Emp> empList = new ArrayList<_003Emp>();
		empList.add(emp1);
		empList.add(emp2);
		dept.setEmpList(empList);			
		session.save(dept);	
		tx.commit();
		session.close();

		session = sf.openSession();
		List<_003Dept> deptList = (List<_003Dept>)session.createQuery(" FROM _003Dept").list();
		for(_003Dept dept1 : deptList)
			System.out.println(dept1.empList.toString());
		session.close();

	}
}

@Entity
@Table(name="EMP")
class _003Emp {

	@Id
	@Column(name = "ENO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;

	@ManyToOne()
	@JoinColumn(name="DNO")
	_003Dept dept;

	@Column(name = "ENAME")
	String name;

	@Column(name = "EADDRESS")
	String address;

	@Column(name = "ESALARY")
	double salary;

	public _003Emp() {	}

	public _003Emp(_003Dept dept, String name, String address, double salary) {
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

	public _003Dept getDept() {
		return dept;
	}

	public void setDept(_003Dept dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "_003Emp [eid=" + eid + ", name=" + name + ", address=" + address + ", salary=" + salary + "]";
	}
}

@Entity
@Table(name="DEPT")
class _003Dept {

	@Id
	@Column(name = "DNO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;

	@Column(name = "DNAME")
	String name;

	@Fetch(FetchMode.SELECT)
//	@Fetch(FetchMode.JOIN)
//	@BatchSize(size = 2)
//	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy="dept", cascade=CascadeType.ALL)
	List<_003Emp> empList;

	public _003Dept() {	}

	public _003Dept(String name) {
		this.name = name;
	}

	public List<_003Emp> getEmpList() {
		return empList;
	}

	public void setEmpList(List<_003Emp> empList) {
		this.empList = empList;
	}

	public int getEid() { return did; }
	public void setEid(int did) { this.did = did; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Override
	public String toString() {
		return "_003Dept [did=" + did + ", name=" + name + ", empList=" + empList + "]";
	}
}
