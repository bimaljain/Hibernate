/* 
Bydefault, all the member variables of the entity will be persisted by hibernate. You can choose to skip any particular member variable in 2 ways:
- Mark the member variable as transient or static
- Use @Transient on top of the member variable.
 
The datatype conversion from Java datatype to DB datatype is also done automatically by hibernate. You can control this to override the default 
conversion behavior. For example the java Calender object is converted to timestamp (without timezone) in the DB. What if you want to save the date 
alone or time alone. Use:
@Temporal(TemporalType.DATE)
@Temporal(TemporalType.TIME)
 
Hibernate converts String to varchar(255). What if your String object has more than 255 characters. Use:
@Lob on top of the member variable. In that case, hibernate choses either Clob or Blob for datatype conversion. If the member variable is a String, 
Hibernate converts it to Clob, and if the member variable is a byte array, hibernate will choose Blob.

DROP TABLE EMP;

CREATE TABLE EMP (
  ENO INT NOT NULL AUTO_INCREMENT,
  ENAME VARCHAR(15) NOT NULL,
  EADDRESS VARCHAR(100) NOT NULL,
  ESALARY INT,
  LAST_UPDATED_TIMESTAMP TIMESTAMP NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  NOTES TEXT,
  PRIMARY KEY (ENO)
);

select * from EMP;

*/

package _020_Misc;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
public class _001_Emp {
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Transient
	@Column(name = "ESALARY")
	double salary;
	
	@Column(name="LAST_UPDATED_TIMESTAMP")
	Date lastUpdatedTimeStamp;
	
	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	Date dateAdded;
	
	@Lob
	@Column(name="NOTES")
	String notes;
	
	public _001_Emp() {	}

	public _001_Emp(String name, String address, double salary, Date lastUpdatedTimeStamp, Date dateAdded, String notes) {
		super();
		this.name = name;
		this.address = address;
		this.salary = salary;
		this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
		this.dateAdded = dateAdded;
		this.notes = notes;
	}

	public int getEid() {
		return eid;
	}

	public void setEid(int eid) {
		this.eid = eid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Date getLastUpdatedTimeStamp() {
		return lastUpdatedTimeStamp;
	}

	public void setLastUpdatedTimeStamp(Date lastUpdatedTimeStamp) {
		this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "_001_Emp [eid=" + eid + ", name=" + name + ", address=" + address + ", salary=" + salary
				+ ", lastUpdatedTimeStamp=" + lastUpdatedTimeStamp + ", dateAdded=" + dateAdded + ", notes=" + notes
				+ "]";
	}

	public static void main(String[] args) throws IOException {
		_003Demo demo = new _003Demo();
		demo.insert();
		demo.selectAll();
	}
}

class _003Demo{
	public void insert(){
		Configuration cfg = new Configuration().configure("020_Misc/001.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001_Emp emp = new _001_Emp("Bimal","Pune",23456, new Date(), new Date(), "Hi, I am a java geek");
			session.save(emp);
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
	Hibernate: insert into EMP (EADDRESS, CREATED_DATE, LAST_UPDATED_TIMESTAMP, ENAME, NOTES) values (?, ?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void selectAll() throws IOException{
		Configuration cfg = new Configuration().configure("020_Misc/001.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_001_Emp> employees = (List<_001_Emp>)session.createQuery(" FROM _001_Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.CREATED_DATE as CREATED_3_0_, emp0_.LAST_UPDATED_TIMESTAMP as LAST_UPD4_
	0_, emp0_.ENAME as ENAME5_0_, emp0_.NOTES as NOTES6_0_ from EMP emp0_
	[_001_Emp [eid=1, name=Bimal, address=Pune, salary=0.0, lastUpdatedTimeStamp=2017-01-30 18:04:31.0, dateAdded=2017-01-30, notes=Hi, I am a java geek]
	 */
}
