/*
Entity Object: These objects have business value on their own.
Value Object: These objects does not have value on their own, and are only there to provide value to other entity.
@Embeddable & @Embedded: These annotations tells hibernate that the object needs to be embedded in some other entity and not to create a separate 
table for this. In other words, this annotation tells hibernate that this class is not an entity.

DROP TABLE STUDENT;

create table STUDENT (
   STUDENT_ID INT NOT NULL AUTO_INCREMENT,
   FIRST_NAME VARCHAR(30) NOT NULL,
   LAST_NAME  VARCHAR(30) NOT NULL,
   STREET VARCHAR(30),
   CITY  VARCHAR(30),
   COUNTRY  VARCHAR(30),
   PRIMARY KEY (STUDENT_ID)
);

select * from STUDENT;

 */

package _001;

import java.io.IOException;
import java.util.List;

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
class _012Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    @Embedded
    private _012Address address;
 
    public _012Student() {
    }
 
    public _012Student(String firstName, String lastName, _012Address address) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
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
    
    public _012Address getAddress() {
		return address;
	}

	public void setAddress(_012Address address) {
		this.address = address;
	}
     
    @Override
	public String toString() {
		return "_022_Student_Embeddable [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + "]";
	}

}

@Embeddable
class _012Address {
	
    @Column(name = "STREET")
    private String street;
 
    @Column(name = "CITY")
    private String city;
 
    @Column(name = "COUNTRY")
    private String country;
    
    public _012Address() {
     }
 
    public _012Address(String street, String city, String country) {
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

public class _012_Embeddable {
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/012.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_012_Embeddable demo = new _012_Embeddable();
		demo.insert(sf);
		demo.select(sf);
	}
	
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_012Address address = new _012Address("Fremont Blvd","Fremont","USA");
			_012Student student = new _012Student("bimal","jain", address);
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
	Hibernate: insert into STUDENT (CITY, COUNTRY, STREET, FIRST_NAME, LAST_NAME) values (?, ?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_012Student> students = (List<_012Student>)session.createQuery(" FROM _001._012Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_0_, stude0_.CITY as CITY2_0_, stude0_.COUNTRY as COUNTRY3_0_, stude0_.STREET as STREET4_0_, stude0_.
	FIRST_NAME as FIRST_NA5_0_, stude0_.LAST_NAME as LAST_NAM6_0_ from STUDENT stude0_
	[_022_Student_Embeddable [id=1, firstName=bimal, lastName=jain, address=Address [street=Fremont Blvd, city=Fremont, country=USA]]]
	 */
	
}
