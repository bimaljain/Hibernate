/*
PURPOSE: Hibernate One-To-One Unidirectional Foreign Key association mapping using annotation based configuration.
Here one table has a foreign key column that references the primary key of associated table.

DROP TABLE ADDRESS;
DROP TABLE STUDENT;

create table ADDRESS (
   ADDRESS_ID INT NOT NULL AUTO_INCREMENT,
   street VARCHAR(30),
   city  VARCHAR(30),
   country  VARCHAR(30),
   PRIMARY KEY (ADDRESS_ID)
);

create table STUDENT (
   STUDENT_ID INT NOT NULL AUTO_INCREMENT,
   first_name VARCHAR(30) NOT NULL,
   last_name  VARCHAR(30) NOT NULL,
   STUDENT_ADDRESS_ID INT NOT NULL,
   PRIMARY KEY (student_id),
   CONSTRAINT address_student FOREIGN KEY (STUDENT_ADDRESS_ID) REFERENCES ADDRESS (ADDRESS_ID)
);

select * from STUDENT;
select * from ADDRESS;
 */

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name = "STUDENT")
class _010Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="STUDENT_ADDRESS_ID")
    private _010Address address;
 
    public _010Student() {
    }
 
    public _010Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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
    
    public _010Address getAddress() {
		return address;
	}

	public void setAddress(_010Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "_010Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + "]";
	}
}

@Entity
@Table(name = "ADDRESS")
class _010Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private long id;
 
    @Column(name = "STREET")
    private String street;
 
    @Column(name = "CITY")
    private String city;
 
    @Column(name = "COUNTRY")
    private String country;
 
    public _010Address() {
 
    }
 
    public _010Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }
 
    public long getId() {
        return id;
    }
 
    public void setId(long id) {
        this.id = id;
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
        return "Address [id=" + id + ", street=" + street + ", city=" + city + ", country=" + country + "]";
    }
     
}

class _010_OneToOne_UniDirectional{    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/010.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_010_OneToOne_UniDirectional demo = new _010_OneToOne_UniDirectional();
		demo.insert(sf);
		demo.select(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_010Student student = new _010Student("bimal","jain");
			_010Address address = new _010Address("Fremont Blvd","Fremont","USA");
//			session.save(address); //without cascade
			student.setAddress(address);
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
	Hibernate: insert into ADDRESS (CITY, COUNTRY, STREET) values (?, ?, ?)
	Hibernate: insert into STUDENT (STUDENT_ADDRESS_ID, FIRST_NAME, LAST_NAME) values (?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_010Student> students = (List<_010Student>)session.createQuery(" FROM _001._010Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_1_, studen0_.STUDENT_ADDRESS_ID as STUDENT_4_1_, studen0_.FIRST_NAME as FIRST_NA2_1_, studen0_.LAST
	_NAME as LAST_NAM3_1_ from STUDENT studen0_
	Hibernate: select addres0_.ADDRESS_ID as ADDRESS_1_0_0_, addres0_.CITY as CITY2_0_0_, addres0_.COUNTRY as COUNTRY3_0_0_, addres0_.STREET as STREET4_0_
	0_ from ADDRESS addres0_ where addres0_.ADDRESS_ID=?
	
	[_010Student [id=1, firstName=bimal, lastName=jain, address=Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]]]
	 */
}
