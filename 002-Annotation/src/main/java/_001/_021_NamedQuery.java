/*
--------------------------------
Named Query& Named Native Query:
--------------------------------
1. Queries may be expressed in native SQL. The result of a native SQL query may consist of entities, scalar values, or a combination of the two.
2. When multiple entities are returned by a SQL query, the entities must be specified in a @SqlResultSetMapping metadata definition. This result set 
mapping metadata can then be used by the persistence provider runtime to map the JDBC results into the expected objects.If the results of the query are 
limited to entities of a SINGLE entity class, a simpler form may be used and @SqlResultSetMapping metadata is not required.
3. When an entity is being returned, the SQL statement should select ALL of the columns that are mapped to the entity object. This should include foreign 
key columns to related entities. The results obtained when insufficient data is available are undefined.
4. Its best practice to write all the queries at a common place for easy maintenance. Named Query allows to write queries at the entity level.
5. It's a good practice to use entityName in front of the named query.
6. Query can be HQL (using @NamedQuery) or SQL (using @NamedNativeQuery). These annotations are placed below @Entity, and both takes 2 parameters, name 
and query. Incase of @NamedNativeQuery, supply the entity class as well using resultClass. This is optional. But it helps you to easily cast it once you 
get the resultset. This is not required in HQL since it already knows about the class.

-----------
DB DETAILS:
-----------
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
*/

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class _021_NamedQuery{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/021.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_021Dept dept = new _021Dept("Aladdin Product Group");
		_021Emp emp1 = new _021Emp(dept, "Bimal","Pune",23456);
		session.save(dept);
		session.save(emp1);
		
		System.out.println("\n >>> HQL: Returns one entity");
		Query query = session.getNamedQuery("emp.named.query1");
		List<_021Emp> result1 = query.list();
		System.out.println(result1);	
		
		System.out.println("\n >>> HQL: returns few entity properties");
		query = session.getNamedQuery("emp.named.query2");
		List<Object[]> result2 = query.list();
		for (Object[] emp : result2) System.out.println(emp[0] + " " + emp[1]);
		
		System.out.println("\n >>> HQL: returns unique result");
		query = session.getNamedQuery("emp.named.query3");
		Long result3 = (Long) query.uniqueResult();
		System.out.println(result3);
		
		System.out.println("\n >>> SQL: Returns one entity using resultClass");
		query = session.getNamedQuery("emp.native.named.query2");
		List<_021Emp> result4 = query.list();
		System.out.println(result4);
		
		System.out.println("\n >>> SQL: Returns one entity using resultSetMapping");
		query = session.getNamedQuery("emp.native.named.query3");
		List<_021Emp> result5 = query.list();
		System.out.println(result5);
		
		System.out.println("\n >>> SQL: Returns multiple entity");
		query = session.getNamedQuery("emp.native.named.query6");
		List<Object[]> result6 = query.list();
		for (Object[] obj : result6) System.out.println(obj[0] + " " + obj[1]);
		
		System.out.println("\n >>> SQL: Returns scalars");
		query = session.getNamedQuery("emp.native.named.query4");
		List<Object[]> result7 = query.list();
		for (Object[] obj : result7) System.out.println(obj[0] + " " + obj[1]);
				
		System.out.println("\n >>>SQL: Returns scalars from multiple entity");
		query = session.getNamedQuery("emp.native.named.query9");
		List<Object[]> result10 = query.list();
		for (Object[] obj : result10) System.out.println(obj[0] + " " + obj[1]);
		
		System.out.println("\n >>>SQL: Returns scalars using alias");
		query = session.getNamedQuery("emp.native.named.query5");
		List<Object[]> result8 = query.list();
		for (Object[] obj : result8) System.out.println(obj[0] + " " + obj[1]);
		
		System.out.println("\n >>>SQL: Returns entity + scalar");
		query = session.getNamedQuery("emp.native.named.query7");
		List<Object[]> result9 = query.list();
		for (Object[] obj : result9) System.out.println(obj[0] + " " + obj[1]);
		
		tx.commit();
		session.close();
	}
}

@Entity
@NamedQueries({	
@NamedQuery(name="emp.named.query1", query = "from _021Emp where id=1"),
@NamedQuery(name="emp.named.query2", query = "select name, salary from _021Emp where id=2"),
@NamedQuery(name="emp.named.query3", query = "select count(*) from _021Emp")
})

@NamedNativeQueries({
@NamedNativeQuery(name="emp.native.named.query2", 
	query="SELECT * FROM EMP WHERE ENO=1", resultClass=_021Emp.class),

@NamedNativeQuery(name="emp.native.named.query3", 
	query="SELECT * FROM EMP WHERE ENO=2", resultSetMapping="rsMetadata3"),

@NamedNativeQuery(name="emp.native.named.query6", 
	query="SELECT E.*, D.* FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=1", resultSetMapping="rsMetadata6"),

@NamedNativeQuery(name="emp.native.named.query4", 
	query="SELECT ENAME, ESALARY FROM EMP WHERE ENO=3", resultSetMapping="rsMetadata4"),

@NamedNativeQuery(name="emp.native.named.query5", 
	query="SELECT ENAME as NAME, ESALARY as SALARY FROM EMP WHERE ENO=4", resultSetMapping="rsMetadata5"),

@NamedNativeQuery(name="emp.native.named.query9", 
	query="SELECT E.ENAME, D.DNAME FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=2", resultSetMapping="rsMetadata9"),

@NamedNativeQuery(name="emp.native.named.query7", 
	query="SELECT E.*, D.DNAME FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=2", resultSetMapping="rsMetadata7"),

@NamedNativeQuery(name="emp.native.named.query8", 
	query="SELECT SUM(ESALARY) as ctc FROM EMP", resultSetMapping="rsMetadata8"),

//DON'T USE THIS APPROACH
@NamedNativeQuery(name="emp.native.named.query1", 
	query="SELECT EMP.*, ADR.* FROM EMPLOYEE EMP, ADDRESS ADR WHERE EMP.EMP_ID=ADR.EMP_ID AND EMP.EMP_ID=1 ", resultClass=_021Emp.class)
})

@SqlResultSetMappings({
@SqlResultSetMapping(name="rsMetadata3", entities={@EntityResult(entityClass=_001._021Emp.class)}),

/* these are just scalars, so we don't need to know about an entity here */
@SqlResultSetMapping(name="rsMetadata4", columns={@ColumnResult(name="ENAME"), @ColumnResult(name="ESALARY")}),

@SqlResultSetMapping(name="rsMetadata5", columns={@ColumnResult(name="NAME"), @ColumnResult(name="SALARY")}),

@SqlResultSetMapping(name="rsMetadata6", entities={@EntityResult(entityClass=_001._021Emp.class), @EntityResult(entityClass=_001._021Dept.class)}),

@SqlResultSetMapping(name="rsMetadata7", entities={@EntityResult(entityClass=_001._021Emp.class)}, columns={@ColumnResult(name="DNAME")}),

@SqlResultSetMapping(name="rsMetadata8", columns={@ColumnResult(name="ctc")}),

@SqlResultSetMapping(name="rsMetadata9", columns={@ColumnResult(name="ENAME"), @ColumnResult(name="DNAME")})

})
@Table(name="EMP")
class _021Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false, fetch=FetchType.EAGER)
    @JoinColumn(name="DNO")
	_021Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _021Emp() {	}

	public _021Emp(_021Dept dept, String name, String address, double salary) {
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
	
	public _021Dept getDept() {
		return dept;
	}

	public void setDept(_021Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _021Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _021Dept() {	}

	public _021Dept(String name) {
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

/*OUTPUT
------------
LAZY OUTPUT:
------------
Hibernate: insert into DEPT (DNAME) values (?)
Hibernate: insert into EMP (EADDRESS, DNO, ENAME, ESALARY) values (?, ?, ?, ?)

 >>> HQL: Returns one entity
Hibernate: select emp0_.ENO as ENO1_1_, emp0_.EADDRESS as EADDRESS2_1_, emp0_.DNO as DNO5_1_, emp0_.ENAME as ENAME3_1_, emp0_.ESALARY as ESALARY4_1_ from EMP emp0_ where emp0_.ENO=1
Hibernate: select dept0_.DNO as DNO1_0_0_, dept0_.DNAME as DNAME2_0_0_ from DEPT dept0_ where dept0_.DNO=?
[1 1 Aladdin Product Group Bimal Pune 99999.0]

 >>> HQL: returns few entity properties
Hibernate: select emp0_.ENAME as col_0_0_, emp0_.ESALARY as col_1_0_ from EMP emp0_ where emp0_.ENO=2
Bimal 23456.0

 >>> HQL: returns unique result
Hibernate: select count(*) as col_0_0_ from EMP emp0_
23

 >>> SQL: Returns one entity using resultClass
Hibernate: SELECT * FROM EMP WHERE ENO=1
[1 1 Aladdin Product Group Bimal Pune 99999.0]

 >>> SQL: Returns one entity using resultSetMapping
Hibernate: SELECT * FROM EMP WHERE ENO=2
Hibernate: select dept0_.DNO as DNO1_0_0_, dept0_.DNAME as DNAME2_0_0_ from DEPT dept0_ where dept0_.DNO=?
[2 2 Aladdin Product Group Bimal Pune 23456.0]

 >>> SQL: Returns multiple entity
Hibernate: SELECT E.*, D.* FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=1
1 1 Aladdin Product Group Bimal Pune 99999.0 1 Aladdin Product Group

 >>> SQL: Returns scalars
Hibernate: SELECT ENAME, ESALARY FROM EMP WHERE ENO=3
Bimal 23456

 >>>SQL: Returns scalars from multiple entity
Hibernate: SELECT E.ENAME, D.DNAME FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=2
Bimal Aladdin Product Group

 >>>SQL: Returns scalars using alias
Hibernate: SELECT ENAME as NAME, ESALARY as SALARY FROM EMP WHERE ENO=4
Bimal 23456

 >>>SQL: Returns entity + scalar
Hibernate: SELECT E.*, D.DNAME FROM EMP E, DEPT D WHERE E.DNO=D.DNO AND E.ENO=2
2 2 Aladdin Product Group Bimal Pune 23456.0 Aladdin Product Group

*/
