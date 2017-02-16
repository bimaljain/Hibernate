/*
1. In Bidirectional relationship, both side navigation is possible. For bidirectional relationships, we have a concept of ownership, means who is the owner 
of this relationship. Put simply, who is responsible for updating the column in DB on which this relationship depends on.

2. @JoinColumn is used to represent the owner of the relationship.

3. mappedBy attribute are always put on the inverse side of relationship. Here the relationship is managed by “dept” property of Emp class annotated with 
@JoinColumn.

4. @OneToMany on list property here denotes that one DEPT can have multiple EMP. With EMP property defined in DEPT class, we can now navigate from DEPT to 
EMP. mappedBy says that it's the inverse side of relationship. 

5. Note the cascade attribute, which means the child object(EMP) will be persisted/updated/deleted automatically on subsequent persist/update/delete of 
DEPT object (parent). No need to perform operation separately on EMP. 
[We can insert/update records from DEPT side as well, but there are lot of additional steps needed. Fewer steps are needed to maintain the relationship 
from the EMP side. Remember that EMP is the relationship owner. So insert/update should be done from the EMP side. Also remember that Bidirectional
relationship means you can do select from both side.]

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

package _001;

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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _005Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false, cascade=CascadeType.ALL)
    @JoinColumn(name="DNO")
	_005Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _005Emp() {	}

	public _005Emp(String name, String address, double salary) {
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
	
	public _005Dept getDept() {
		return dept;
	}

	public void setDept(_005Dept dept) {
		this.dept = dept;
	}

// this is needed if you do select from EMP side
//	@Override
//	public String toString() {
//		return "_005Emp [eid=" + eid + ", dept=" + dept + ", name=" + name + ", address=" + address + ", salary=" + salary + "]";
//	}

// this is needed if you do select from DEPT side
	@Override
	public String toString() {
		return "_005Emp [eid=" + eid + ", name=" + name + ", address=" + address + ", salary=" + salary + "]";
	}
	
}

@Entity
@Table(name="DEPT")
class _005Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
//	@OneToMany(mappedBy="dept", cascade=CascadeType.ALL)
	@OneToMany(mappedBy="dept")
	List<_005Emp> empList;
	
	public _005Dept() {	}

	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public List<_005Emp> getEmpList() {
		return empList;
	}
	public void setEmpList(List<_005Emp> empList) {
		this.empList = empList;
	}
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

// this is needed if you do select from DEPT side
	@Override
	public String toString() {
		return "_005Dept [did=" + did + ", name=" + name + ", empList=" + empList + "]";
	}
	
// this is needed if you do select from EMP side
//	@Override
//	public String toString() {
//		return "_005Dept [did=" + did + ", name=" + name + "]";
//	}
}

public class _005_ManyToOne_BiDirectional {
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/005.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_005_ManyToOne_BiDirectional demo = new _005_ManyToOne_BiDirectional();
		demo.insertEmpToDept(sf);
//		demo.selectEmpToDept(sf); //uncomment toString() as well.
		demo.selectDeptToEmp(sf);
//		demo.insertDeptToEmp(sf);
	}
	
	public void insertEmpToDept(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_005Dept dept = new _005Dept();
			dept.setName("Aladdin Product Group");
			_005Emp emp1 = new _005Emp("Bimal","Pune",23456);
			emp1.setDept(dept);
			//session.save(dept); //no need to save dept separately
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
	public void selectEmpToDept(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_005Emp> employees = (List<_005Emp>)session.createQuery(" FROM _001._005Emp").list();
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
	
	[_005Emp [eid=1, dept=_005Dept [did=1, name=Aladdin Product Group], name=Bimal, address=Pune, salary=23456.0]]
	 */

	@SuppressWarnings("unchecked")
	public void selectDeptToEmp(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_005Dept> deptList = (List<_005Dept>)session.createQuery(" FROM _001._005Dept").list();
			for(_005Dept dept : deptList){
				System.out.println(dept.toString() + " " + dept.empList.toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	
	/*
	OUTPUT:
	Hibernate: select dept0_.DNO as DNO1_0_, dept0_.DNAME as DNAME2_0_ from DEPT dept0_
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	
	_005Dept [did=1, name=Aladdin Product Group, empList=[_005Emp [eid=1, name=Bimal, address=Pune, salary=23456.0]]] [_005Emp [eid=1, name=Bimal, address
	=Pune, salary=23456.0]]

	 */
	
	public void insertDeptToEmp(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_005Dept dept = new _005Dept();
			dept.setName("Aladdin Product Group");
			_005Emp emp1 = new _005Emp("Bimal","Pune",23456);
			emp1.setDept(dept);
			_005Emp emp2 = new _005Emp("meghna","Pune",98765);
			emp2.setDept(dept);
			List<_005Emp> empList = new ArrayList<_005Emp>();
			empList.add(emp1);
			empList.add(emp2);
			dept.setEmpList(empList);
			
			session.save(dept);
			//session.save(emp1);
			//session.save(emp2);
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
	Hibernate: insert into EMP (EADDRESS, DNO, ENAME, ESALARY) values (?, ?, ?, ?)
	 */	
}
