/* 

Hibernate Criteria Query
1. So far 2 ways to pull up objects from DB
· Configuring entity objects and then calling session.get() [very little control]
· Using HQL [provides more control, but can be messier]
· Criteria Query: Instead of specifying all the criteria into your HQL which makes it messier, we can use criteria objects to specify the criteria. Criteria API allows you to build up a criteria query object where you can apply filtration rules and logical conditions.
2. The Hibernate Session interface provides createCriteria() method which can be used to create a Criteria object that returns instances of the persistence class when your application executes a criteria query. You can use add() method available for Criteria object to add restriction for a criteria query.
3. Projections: The Criteria API provides the org.hibernate.criterion.Projections class which can be used to get average, maximum or minimum of the property values. The Projections class is similar to the Restrictions class in that it provides several static factory methods for obtaining Projection instances.
· Can return you scalars instead of all the entity properties
· Allows you to use aggregate functions
· Allows you to order resultset
 
Criteria Interface provides the following methods to restrict the records fetched from DB:
Method	Description
add	The Add method adds a Criterion to constrain the results to be retrieved.
addOrder	Add an Order to the result set.
createAlias	Join an association, assigning an alias to the joined entity
createCriteria	This method is used to create a new Criteria, "rooted" at the associated entity.
setFetchSize	This method is used to set a fetch size for the underlying JDBC query.
setFirstResult	This method is used to set the first result to be retrieved.
setMaxResults	This method is used to set a limit upon the number of objects to be retrieved.
uniqueResult	This method is used to instruct the Hibernate to fetch and return the unique records from database.
 
Class Restriction provides built-in criterion via static factory methods. Important methods of the Restriction class are:
Method	Description
Restriction.allEq	This is used to apply an "equals" constraint to each property in the key set of a Map
Restriction.between	This is used to apply a "between" constraint to the named property
Restriction.eq	This is used to apply an "equal" constraint to the named property
Restriction.ge	This is used to apply a "greater than or equal" constraint to the named property
Restriction.gt	This is used to apply a "greater than" constraint to the named property
Restriction.idEq	This is used to apply an "equal" constraint to the identifier property
Restriction.ilike	This is case-insensitive "like", similar to Postgres ilike operator
Restriction.in	This is used to apply an "in" constraint to the named property
Restriction.isNotNull	This is used to apply an "is not null" constraint to the named property
Restriction.isNull  	This is used to apply an "is null" constraint to the named property
Restriction.le 	This is used to apply a "less than or equal" constraint to the named property
Restriction.like	This is used to apply a "like" constraint to the named property
Restriction.lt	This is used to apply a "less than" constraint to the named property
Restriction.ltProperty	This is used to apply a "less than" constraint to two properties
Restriction.ne 	This is used to apply a "not equal" constraint to the named property
Restriction.neProperty	This is used to apply a "not equal" constraint to two properties
Restriction.not  	This returns the negation of an expression
Restriction.or	This method returns the disjuction of two expressions. Any given condition is 'true' then it executes the query.
Restriction.and	This method returns the conjunctions of two expressions. Both conditions are 'true' then it executes the query otherwise not.


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
import javax.persistence.criteria.Order;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

@Entity
@Table(name="EMP")
class _020Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="DNO")
	_020Dept dept;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _020Emp() {	}

	public _020Emp(_020Dept dept, String name, String address, double salary) {
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
	
	public _020Dept getDept() {
		return dept;
	}

	public void setDept(_020Dept dept) {
		this.dept = dept;
	}

	public String toString(){
		return this.eid + " " + this.dept + " " + this.name + " " + this.address + " " + this.salary;
	}
}

@Entity
@Table(name="DEPT")
class _020Dept {
	
	@Id
	@Column(name = "DNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int did;
	
	@Column(name = "DNAME")
	String name;
	
	public _020Dept() {	}

	public _020Dept(String name) {
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

public class _020_CriteriaQuery{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/020.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_020Dept dept = new _020Dept("Aladdin Product Group");
		_020Emp emp1 = new _020Emp(dept, "Bimal","Pune",23456);
		session.save(dept);
		session.save(emp1);
		
		//new criteria instance
		Criteria crit = session.createCriteria(_020Emp.class);
		//crit.add(Restrictions.like("name", "%v%"));     //Like condition
		//crit.add(Restrictions.between("salary",new Double(25000), new Double(50000)));     //Between condition //crit.add(Restrictions.eq("name", "vikas"));     //equals constraint
		//crit.add(Restrictions.lt("name", "vikas"));     //less than constraint
		//crit.add(Restrictions.le("name", "vikas"));     //less than or equals constraint
		//crit.add(Restrictions.gt("name", "vikas"));     //less than or equals constraint
		//crit.add(Restrictions.ge("name", "vikas"));     //less than or equals constraint
		//crit.add(Restrictions.isNull("salary"));
		//crit.add(Restrictions.isNotNull("salary"));
		//crit.add(Restrictions.isEmpty("salary"));
		//crit.add(Restrictions.isNotEmpty("salary"));
		//crit.add(Restrictions.and(Restrictions.gt("name", "swati"), Restrictions.lt("name", "vishal"))); //and expression
		//crit.add(Restrictions.or(Restrictions.gt("name", "swati"), Restrictions.lt("name", "vishal"))); //or expression
		crit.add(Restrictions.gt("salary", 99998.0));
		//Below code creates the sql query and execute against database to retrieve the data.
		List emp = crit.list();
		System.out.println(emp);
		
		tx.commit();
		session.close();
	}
}
