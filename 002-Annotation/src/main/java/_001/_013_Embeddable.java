/*
Embedded columns can be customized in the entity object.

DROP TABLE STUDENT;

create table STUDENT (
   STUDENT_ID INT NOT NULL AUTO_INCREMENT,
   FIRST_NAME VARCHAR(30) NOT NULL,
   LAST_NAME  VARCHAR(30) NOT NULL,
   HOME_STREET VARCHAR(30),
   HOME_CITY  VARCHAR(30),
   HOME_COUNTRY  VARCHAR(30),
   WORK_STREET VARCHAR(30),
   WORK_CITY  VARCHAR(30),
   WORK_COUNTRY  VARCHAR(30),
   PRIMARY KEY (STUDENT_ID)
);

select * from STUDENT;

 */

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name = "STUDENT")
class _013Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    @Embedded
	@AttributeOverrides({
		@AttributeOverride(name="city", column=@Column(name="HOME_CITY")), // name="city" is the java attribute and not the DB column. So no caps letter.
		@AttributeOverride(name="street", column=@Column(name="HOME_STREET")),
		@AttributeOverride(name="country", column=@Column(name="HOME_COUNTRY"))})
    private _013Address homeAddress;
    
    @Embedded
	@AttributeOverrides({
		@AttributeOverride(name="city", column=@Column(name="WORK_CITY")),
		@AttributeOverride(name="street", column=@Column(name="WORK_STREET")),
		@AttributeOverride(name="country", column=@Column(name="WORK_COUNTRY"))})
    private _013Address workAddress;
 
    public _013Student() {
    }

	public _013Student(String firstName, String lastName, _013Address homeAddress, 	_013Address workAddress) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.homeAddress = homeAddress;
		this.workAddress = workAddress;
	}

	@Override
	public String toString() {
		return "_024_Student_Embeddable [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", homeAddress=" + homeAddress + ", workAddress=" + workAddress + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public _013Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(_013Address homeAddress) {
		this.homeAddress = homeAddress;
	}

	public _013Address getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(_013Address workAddress) {
		this.workAddress = workAddress;
	}
}


@Embeddable
class _013Address {
	
    @Column(name = "STREET")
    private String street;
 
    @Column(name = "CITY")
    private String city;
 
    @Column(name = "COUNTRY")
    private String country;
    
    public _013Address() {
     }
 
    public _013Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }
 
    public String getStreet() {
        return street;
    }
 
    public void setStreet(String street) {
        this.street = street;
    }
 
    public String getCity() {
        return city;
    }
 
    public void setCity(String city) {
        this.city = city;
    }
 
    public String getCountry() {
        return country;
    }
 
    public void setCountry(String country) {
        this.country = country;
    }
 
    @Override
    public String toString() {
        return "Address [street=" + street + ", city=" + city + ", country=" + country + "]";
    }
     
}

public class _013_Embeddable {
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/013.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_013_Embeddable demo = new _013_Embeddable();
		demo.insert(sf);
		demo.select(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_013Address homeAddress = new _013Address("Fremont Blvd","Fremont","USA");
			_013Address workAddress = new _013Address("Fremont Street","SFO","USA");
			_013Student student = new _013Student("bimal","jain", homeAddress, workAddress);
	        session.save(student);       
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
	Hibernate: insert into STUDENT (FIRST_NAME, HOME_CITY, HOME_COUNTRY, HOME_STREET, LAST_NAME, WORK_CITY, WORK_COUNTRY, WORK_STREET) values (?, ?, ?, ?,
 	?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_013Student> students = (List<_013Student>)session.createQuery(" FROM _001._013Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_0_, stude0_.FIRST_NAME as FIRST_NA2_0_, stude0_.HOME_CITY as HOME_CIT3_0_, stude0_.HOME_COUNTRY as H
	OME_COU4_0_, stude0_.HOME_STREET as HOME_STR5_0_, stude0_.LAST_NAME as LAST_NAM6_0_, stude0_.WORK_CITY as WORK_CIT7_0_, stude0_.WORK_COUNTRY as WORK_C
	OU8_0_, stude0_.WORK_STREET as WORK_STR9_0_ from STUDENT stude0_
	[_024_Student_Embeddable [id=1, firstName=bimal, lastName=jain, homeAddress=Address [street=Fremont Blvd, city=Fremont, country=USA], workAddress=Addr
	ess [street=Fremont Street, city=SFO, country=USA]]]
	 */
	
}
