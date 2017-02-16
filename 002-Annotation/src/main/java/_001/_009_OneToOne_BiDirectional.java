/*
PURPOSE: Hibernate One-To-One Bidirectional Shared primary key mapping.
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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name = "STUDENT")
class _009Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private _009Address address;
    
    public _009Student() {
    }
 
    public _009Student(String firstName, String lastName) {
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
    
    public _009Address getAddress() {
		return address;
	}

	public void setAddress(_009Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "_009Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

//	@Override
//	public String toString() {
//		return "_009Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + "]";
//	}
	
}

@Entity
@Table(name = "ADDRESS")
class _009Address {
	
	@Id
	@GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign",parameters=@Parameter(name="property", value="student"))
    @Column(name = "ADDRESS_ID")
    private long id;
 
    @Column(name = "STREET")
    private String street;
 
    @Column(name = "CITY")
    private String city;
 
    @Column(name = "COUNTRY")
    private String country;
    
    @OneToOne(mappedBy="address", cascade = CascadeType.ALL)
    private _009Student student;
    
    public _009Address() {
 
    }
 
    public _009Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }
 
    public _009Student getStudent() {
		return student;
	}

	public void setStudent(_009Student student) {
		this.student = student;
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
		return "_009Address [id=" + id + ", street=" + street + ", city=" + city + ", country=" + country + ", student=" + student + "]";
	}
 
//    @Override
//    public String toString() {
//        return "Address [id=" + id + ", street=" + street + ", city=" + city + ", country=" + country + "]";
//    }   
    
    
}

public class _009_OneToOne_BiDirectional {    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/009.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_009_OneToOne_BiDirectional demo = new _009_OneToOne_BiDirectional();
		demo.insert(sf);
//		demo.select(sf);
		demo.selectInverse(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_009Student student = new _009Student("bimal","jain");
			_009Address address = new _009Address("Fremont Blvd","Fremont","USA");
			student.setAddress(address);
			address.setStudent(student);
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
	Hibernate: insert into STUDENT (FIRST_NAME, LAST_NAME) values (?, ?)
	Hibernate: insert into ADDRESS (CITY, COUNTRY, STREET, ADDRESS_ID) values (?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_009Student> students = (List<_009Student>)session.createQuery(" FROM _001._009Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_1_, studen0_.FIRST_NAME as FIRST_NA2_1_, studen0_.LAST_NAME as LAST_NAM3_1_ from STUDENT studen0_
	Hibernate: select addres0_.ADDRESS_ID as ADDRESS_1_0_0_, addres0_.CITY as CITY2_0_0_, addres0_.COUNTRY as COUNTRY3_0_0_, addres0_.STREET as STREET4_0_
	0_, studen1_.STUDENT_ID as STUDENT_1_1_1_, studen1_.FIRST_NAME as FIRST_NA2_1_1_, studen1_.LAST_NAME as LAST_NAM3_1_1_ from ADDRESS addres0_ left oute
	r join STUDENT studen1_ on addres0_.ADDRESS_ID=studen1_.STUDENT_ID where addres0_.ADDRESS_ID=?
	
	[_009Student [id=1, firstName=bimal, lastName=jain, address=Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]]]
	 */
	
	@SuppressWarnings("unchecked")
	public void selectInverse(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_009Address> addresses = (List<_009Address>)session.createQuery(" FROM _001._009Address").list();
			System.out.println(addresses);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select addres0_.ADDRESS_ID as ADDRESS_1_0_, addres0_.CITY as CITY2_0_, addres0_.COUNTRY as COUNTRY3_0_, addres0_.STREET as STREET4_0_ from 
	ADDRESS addres0_
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_1_0_, studen0_.FIRST_NAME as FIRST_NA2_1_0_, studen0_.LAST_NAME as LAST_NAM3_1_0_, addres1_.ADDRESS
	_ID as ADDRESS_1_0_1_, addres1_.CITY as CITY2_0_1_, addres1_.COUNTRY as COUNTRY3_0_1_, addres1_.STREET as STREET4_0_1_ from STUDENT studen0_ left oute
	r join ADDRESS addres1_ on studen0_.STUDENT_ID=addres1_.ADDRESS_ID where studen0_.STUDENT_ID=?
	
	[_009Address [id=1, street=Fremont Blvd, city=Fremont, country=USA, student=_009Student [id=1, firstName=bimal, lastName=jain]]]
	 */
}
