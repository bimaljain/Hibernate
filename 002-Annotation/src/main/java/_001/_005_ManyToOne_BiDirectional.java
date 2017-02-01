/* 
PURPOSE: Hibernate Many-To-One Bidirectional mapping using annotation based configuration.

Schema layout for Many-To-One Bidirectional mapping is exactly same as Many-To-One Unidirectional Mapping. One table has a foreign key column that 
references the primary key of associated table. In Bidirectional relationship, both side navigation is possible.

@OneToMany on list property here denotes that one DEPT can have multiple EMP. With EMP property defined in DEPT class, we can now navigate from DEPT to 
EMP. mappedBy says that it’s the inverse side of relationship.Also note the cascade attribute, which means the dependent object(EMP) will be 
persisted/updated/deleted automatically on subsequent persist/update/delete on DEPT object. No need to perform operation separately on EMP.

You can see that we have updated both the EMP and DEPT object with there associations but we have only saved DEPT explicitly. Since we have cascade 
attribute set to all on EMP list of DEPT, all child objects(EMP) will be saved on persisting parent object(DEPT).
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
	
	@ManyToOne(optional = false)
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

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
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
	
	@OneToMany(mappedBy="dept", cascade=CascadeType.ALL)
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
	
	public String toString(){
		return this.did + " " + this.name;
	}
}

public class _005_ManyToOne_BiDirectional {
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("005.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_005_ManyToOne_BiDirectional demo = new _005_ManyToOne_BiDirectional();
		demo.insertEmpToDept(sf);
		demo.selectEmpToDept(sf);
		demo.insertDeptToEmp(sf);
		demo.selectDeptToEmp(sf);
	}
	
	public void insertEmpToDept(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_005Dept dept = new _005Dept();
			dept.setName("Aladdin Product Group");
			_005Emp emp1 = new _005Emp("Bimal","Pune",23456);
			emp1.setDept(dept);
			_005Emp emp2 = new _005Emp("meghna","Pune",98765);
			emp2.setDept(dept);
			session.save(dept);
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
	Hibernate: insert into DEPT (DNAME) values (?)
	Hibernate: insert into EMP (EADDRESS, DNO, ENAME, ESALARY) values (?, ?, ?, ?)
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
	Hibernate: select emp_m0_.ENO as ENO1_1_, emp_m0_.EADDRESS as EADDRESS2_1_, emp_m0_.DNO as DNO5_1_, emp_m0_.ENAME as ENAME3_1_, emp_m0_.ESALARY as ESA
	LARY4_1_ from EMP emp_m0_
	Hibernate: select dept_0_.DNO as DNO1_0_0_, dept_0_.DNAME as DNAME2_0_0_ from DEPT dept_0_ where dept_0_.DNO=?
	Hibernate: select dept_0_.DNO as DNO1_0_0_, dept_0_.DNAME as DNAME2_0_0_ from DEPT dept_0_ where dept_0_.DNO=?
	Hibernate: select dept_0_.DNO as DNO1_0_0_, dept_0_.DNAME as DNAME2_0_0_ from DEPT dept_0_ where dept_0_.DNO=?
   [1 1 Aladdin Product Group Bimal Pune 23456.0, 
	2 1 Aladdin Product Group meghna Pune 98765.0, 
	3 2 Aladdin Product Group Bimal Pune 23456.0, 
	4 2 Aladdin Product Group meghna Pune 98765.0, 
	5 3 Aladdin Product Group Bimal Pune 23456.0, 
	6 3 Aladdin Product Group meghna Pune 98765.0]
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
	
	@SuppressWarnings("unchecked")
	public void selectDeptToEmp(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_005Dept> deptList = (List<_005Dept>)session.createQuery(" FROM _001._005Dept").list();
			for(_005Dept dept : deptList){
				System.out.println(dept.empList.toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select dept_0_.DNO as DNO1_0_, dept_0_.DNAME as DNAME2_0_ from DEPT dept_0_
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[]
	
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[1 26 Aladdin Product Group Bimal Pune 23456.0, 2 26 Aladdin Product Group meghna Pune 98765.0]
	
	Hibernate: select emplist0_.DNO as DNO5_1_0_, emplist0_.ENO as ENO1_1_0_, emplist0_.ENO as ENO1_1_1_, emplist0_.EADDRESS as EADDRESS2_1_1_, emplist0_.
	DNO as DNO5_1_1_, emplist0_.ENAME as ENAME3_1_1_, emplist0_.ESALARY as ESALARY4_1_1_ from EMP emplist0_ where emplist0_.DNO=?
	[3 27 Aladdin Product Group Bimal Pune 23456.0, 4 27 Aladdin Product Group meghna Pune 98765.0]
	 */
}
