/*
Parameter Binding: There are 3 ways to do: 
1. Appending input parameters to the SQL directly
	String userId = 5; 
	Query query  = session.createQuery(" from Users where id= " + userId ); 
The problem with this approach is that it can lead to SQL injection. eg, the user may provide, 
	minUserId= 5 or 1=1; 
and in that case, he can pull out all the information and not just for userId =5 

2. Using placeholders & pass the input parameters 
	Query query  = session.createQuery(" from Users where id= ? and userName= ?"); 
	query.setInteger(0, userId);  
	query.setString(1, userName); 
here 0 & 1 are the position indicator. First ? is indicated by 0, 2nd by 1 and so on. 
Here hibernate will detect any SQL injection.

3. Instead of random place holders, you can assign names to the place holders
	Query query  = session.createQuery("from Users where id= :userId"); 
	query.setInteger("userId", userId); // overloaded  method
Here hibernate will detect any SQL injection.

-------------------------------
Hibernate Query Language (HQL):
-------------------------------
1. HQL is an object-oriented query language, similar to SQL, but instead of operating on tables and columns, HQL works with persistent objects and their 
properties. HQL queries are translated by Hibernate into conventional SQL queries which in turns perform action on database.

2. Although you can use SQL statements directly with Hibernate using Native SQL but it is recommended to use HQL whenever possible to avoid database 
portability hassles, and to take advantage of Hibernate's SQL generation and caching strategies.

3. The HQL queries return the query result(s) in the form of object(s). This eliminates the need of creating the object and 
populate the data from result set.

4. HQL fully supports polymorphic queries. Polymorphic queries results the query results along with all the child objects if any.

5. It supports many advance features like, Projection, Aggregation and grouping, Ordering, subqueries, pagination, fetch join with dynamic profiling, 
Inner/outer/full joins, Cartesian products and SQL function calls.

6. Queries written in HQL are database independent, provided supported by the underlying DB.
	
7. HQL may consist of following elements:
	Clauses: from, select, where, order by, group by
	Aggregate functions: avg(), sum(), min(), max(), count()
	Subqueries: Hibernate supports Subqueries if the underlying database supports it.

Clauses:
FROM: You will use FROM clause if you want to load a complete persistent objects into memory
AS: The AS clause can be used to assign aliases to the classes in your HQL queries. The AS keyword is optional and you can also specify the alias directly 
	after the class name
SELECT: The SELECT clause provides more control over the result set than from clause. If you want to obtain few properties of objects instead of the complete 
	object, use the SELECT clause.
WHERE: If you want to narrow the specific objects that are returned from storage, you use the WHERE clause.
ORDER BY: To sort your HQL query's results, you will need to use the ORDER BY clause. 
	You can order the results by any property on the objects in the result set either ascending (ASC) or descending (DESC).
	If you wanted to sort by more than one property, you would just add the additional properties to the end of the order by clause, separated by commas 
	as follows:
	String hql = "FROM Employee E WHERE E.id > 10 ORDER BY E.firstName DESC, E.salary DESC ";
GROUP BY: This clause lets Hibernate pull information from the database and group it based on a value of an attribute and, typically, use the result to 
	include an aggregate value. 
UPDATE: The UPDATE clause can be used to update one or more properties of one or more objects.
DELETE: The DELETE clause can be used to delete one or more objects.
	The Query interface contains a method called executeUpdate() for executing HQL UPDATE or DELETE statements. 
INSERT: HQL supports INSERT INTO clause only where records can be inserted from one object to another object.

Aggregate Functions:
avg(property name):	The average of a property's value
count(property name or *): The number of times a property occurs in the results
max(property name):	The maximum value of the property values
min(property name):	The minimum value of the property values
sum(property name):	The sum total of the property values
Pagination:
	Query setFirstResult(int startPosition): This method takes an integer that represents the first row in your result set, starting with row 0.
	Query setMaxResults(int maxResult): This method tells Hibernate to retrieve a fixed number maxResults of objects.

Named Parameters:
Hibernate supports named parameters in its HQL queries. This makes writing HQL queries that accept input from the user easy and you do not have to defend 
against SQL injection attacks.

-----------
DB DETAILS:
-----------
drop table EMP;
drop table DEPT;

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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class _019_HQL{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/019.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_019Dept dept = new _019Dept("Aladdin Product Group");
		_019Emp emp1 = new _019Emp(dept, "Bimal","Pune",23456);
		session.save(dept);
		session.save(emp1);
		
		//FROM
		Query query = session.createQuery("from _019Emp");
		List<_019Emp> employees1 = (List<_019Emp>)query.list();
		System.out.println(employees1);
		
		//SELECT
		query = session.createQuery("select e.name, e.dept from _019Emp e");
		List<Object[]> employees2 = query.list();
		for (Object[] emp : employees2)
			System.out.println(emp[0] + " " + emp[1]);
		
		//WHERE
		query = session.createQuery("from _019Emp e where e.name like :pattern");
		query.setString("pattern", "bimal%");
		List<_019Emp> employees3 = query.list();
		System.out.println(employees3);
		
		//DELETE
		int rowDeleted = session.createQuery("delete from _019Emp e where e.name like 'meghna%'").executeUpdate();
		System.out.println("# of rows deleted" + rowDeleted);
		
		//UPDATE
		query = session.createQuery("update _019Emp e set e.salary = :salary where e.eid = :employee_id");
		query.setParameter("salary",99999.0);
		query.setParameter("employee_id",1);
		int row = query.executeUpdate();
		System.out.println("Rows affected: " + row);

		//GROUP BY
		//HQL interprets SELECT e.* as select the member field * of the object R. But * is not a member field of R. To select all the member fields of R use:
		query = session.createQuery("select e.name, count(*) from _019Emp e group by e.name");
		List<Object[]> employees4 = query.list();
		for (Object[] emp : employees4)
			System.out.println(emp[0] + " " + emp[1]);
		
		//ORDER BY
		query = session.createQuery("select e from _019Emp e order by e.name");
		List<Object[]> employees5 = query.list();
		System.out.println(employees5);
		
		//AGGREGATE FUNCTIONS
		//String hql = "SELECT COUNT(*) FROM _019Emp e";
		//String hql = "SELECT COUNT(DISTINCT e.salary) FROM _019Emp e";
		//String hql = "SELECT AVG(e.salary) FROM _019Emp e";
		//String hql = "SELECT SUM(e.salary) FROM _019Empv e";
		String hql = "SELECT MAX(e.salary) FROM _019Emp e";
		//String hql = "SELECT MIN(e.salary) FROM _019Emp e";
		query = session.createQuery(hql);
		List list = query.list();
		System.out.println("Output: " + list.get(0));

		//PAGINATION
		query = session.createQuery("from _019Emp");
		query.setFirstResult(1);
		query.setMaxResults(5);
		List employees6 = query.list();
		System.out.println(employees6);
		
		tx.commit();
		session.close();
	}
}

@Entity
@Table(name="EMP")
class _019Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="DNO")
	_019Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _019Emp() {	}

	public _019Emp(_019Dept dept, String name, String address, double salary) {
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
	
	public _019Dept getDept() {
		return dept;
	}

	public void setDept(_019Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _019Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _019Dept() {	}

	public _019Dept(String name) {
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
