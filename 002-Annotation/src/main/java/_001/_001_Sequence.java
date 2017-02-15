/* 
-----------
ANNOTATION:
-----------
Java Persistence API: JPA is a Standard Specification and Hibernate implements JPA specification. Hibernate also provides it’s own non-standard 
annotations to work with, but often it’s good practice to follow along with Standards and use standard annotations to make your code portable on other 
implementations.

@Entity marks this class as Entity Bean. 

@Table declares on which database Table this class will be mapped to.You can also specify other attributes like catalog and schema along with name. Note 
that @Table annotation is optional and in absence of this annotation, unqualified class name is used as table name in database.

@Id marks the field(or group of fields) as primary key of entity. Optionally, you can also specify how the primary key should be generated.This depends on 
the underlying database as well. For example, on MySQL ,IDENTITY is supported (strategy = GenerationType.IDENTITY) but not on Oracle. On the other hand 
Oracle supports sequence (strategy = GenerationType.SEQUENCE) but MySQL doesn’t.

@Column maps the class field to column name in database table.This too is optional and if not specified , field name will be used as column name. You 
can specify other attributes including unique, nullable, name & length.

Annotations can be used either on the member varaible or on top of the getter methods.Hibernate detects that the @Id annotation is on a field and 
assumes that it should access properties on an object directly through fields at runtime. If you placed the @Id annotation on the getId() method, you 
would enable access to properties through getter and setter methods by default. Hence, all other annotations are also placed on either fields or 
getter methods, following the selected strategy. 

-----------------------
PRIMARY KEY GENERATION:
-----------------------
Natural Keys: Values of primary key column which has business values are called natural keys. (eg, emailId). We control this value and we must provide 
it.
Surrogate Key: Values of primary key columen which does not has any business values and are only there to identify the rows uniquely are called 
surrogate keys. We can ask hibernate to generate such keys.
 
@GeneratedValue tells hibernate to generate the primary key. There are various strategies that can be used here: AUTO, IDENTITY, TABLE, SEQUENCE
@GeneratedValue or @GeneratedValue (strategy=GenerationType.AUTO) are same. This lets hibernate decide  appropriate strategy to create primary keys. 
Hibernate internally maintains DB sequence and gets the next primary key value. 
selecthibernate_sequence.nextval from dual
@GeneratedValue (strategy=GenerationType.IDENTITY): This lets hibernate use identiy column from the table to come up with a unique primary key.
@GeneratedValue (strategy=GenerationType.TABLE): Hibernate uses a table to get the last primary key used and create the next one.
@GeneratedValue (strategy=GenerationType.SEQUENCE): Hibernate uses DB managed sequence.
selectemp_seq.nextval from dual

---------
SEQUENCE:
---------
Use Oracle DB

DROP TABLE EMP;
DROP SEQUENCE EMP_SEQUENCE;

--TABLE
CREATE TABLE EMP (
  ENO INT,
  ENAME VARCHAR(255),
  EADDRESS VARCHAR(100),
  ESALARY INT,
  CONSTRAINT ENO_PK PRIMARY KEY (ENO)
);

--SEQUENCE
CREATE SEQUENCE EMP_SEQUENCE
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER
create or replace TRIGGER EMP_TRIGGER 
BEFORE INSERT ON EMP
FOR EACH ROW
BEGIN
  SELECT EMP_SEQUENCE.NEXTVAL
  INTO   :NEW.ENO
  FROM   dual;
END;

select * from EMP;
*/

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _001Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="EMPLOYEE_SEQUENCE")
	@SequenceGenerator(name = "EMPLOYEE_SEQUENCE", sequenceName="EMP_SEQUENCE")
	int eid;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _001Emp() {	}

	public _001Emp(String name, String address, double salary) {
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

public class _001_Sequence {
	
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/001.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_001_Sequence demo = new _001_Sequence();
		demo.insert(sf);
		demo.selectAll(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001Emp emp1 = new _001Emp("Bimal","Pune",23456);
			_001Emp emp2 = new _001Emp("meghna","Pune",98765);
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
	Hibernate: select EMP_SEQUENCE.nextval from dual
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY, ENO) values (?, ?, ?, ?)
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY, ENO) values (?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void selectAll(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_001Emp> employees = (List<_001Emp>)session.createQuery(" FROM _001Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	
	[4 Bimal Pune 23456.0, 5 meghna Pune 98765.0, 7 Bimal Pune 23456.0, 8 meghna Pune 98765.0, 10 Bimal Pune 23456.0, 11 meghna Pune 98765.0, 13 Bimal Pun
	e 23456.0, 14 meghna Pune 98765.0]
	 */
}
