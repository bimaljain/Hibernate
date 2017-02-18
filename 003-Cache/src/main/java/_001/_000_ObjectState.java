/*
--------------
Object States:
--------------
Create:
new() creates a object in transient state & remains there until it is handed over to hibernate.
Session.save() moves the transient object to persistent state.
Session.close() moves the persistent object to detached state.

Read:
There is no transient state.
Session.get() gives us a persistent object directly.
Session.close() moves the persistent object to detached state.

Update:
We can do an update in create flow or in read flow, as long as the object is in presistent state.

Delete:
Object is in persistent state either by session.save() or session.get().
Session.delete() moves the persistent object to transient state.

DROP TABLE EMP;

CREATE TABLE EMP (
  ENO INT NOT NULL AUTO_INCREMENT,
  ENAME VARCHAR(255) ,
  EADDRESS VARCHAR(100),
  ESALARY INT,
  CONSTRAINT ENO_PK PRIMARY KEY (ENO)
);

*/

package _001;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _000Emp{
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _000Emp() {	}

	public _000Emp(String name, String address, double salary) {
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

public class _000_ObjectState {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("000.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		//transient (new operator)
		_000Emp emp = new _000Emp("Bimal","Pune",23456);
		//transient --> persistent
		session.save(emp);
		session.get(_000Emp.class, 1);
		tx.commit();
		//persistent --> detached
		session.close();
		
		System.out.println();
		//detached --> persistent
		//Multiple changes made once the object is attached to a session. But there is only one update query, since hibernate is tracking all the changes 
		//in the cache
		session = sf.openSession();
		tx = session.beginTransaction();
		session.update(emp);
		emp.setName("Bimal change");
		session.update(emp);
		emp.setName("bimlo");
		session.update(emp);
		tx.commit();
		session.close();		
	}
	
	/*
	OUTPUT:
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	
	//Multiple changes done, but only one update SQL
	Hibernate: update EMP set EADDRESS=?, ENAME=?, ESALARY=? where ENO=?
	 */
}
