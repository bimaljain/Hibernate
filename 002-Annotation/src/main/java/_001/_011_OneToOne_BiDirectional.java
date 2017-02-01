/*
PURPOSE: One-To-One BiDirectional

DROP TABLE STUDENT;
DROP TABLE ADDRESS;

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
class _011Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    @OneToOne
    @JoinColumn(name="STUDENT_ADDRESS_ID")
    private _011Address address;
 
    public _011Student() {
    }
 
    public _011Student(String firstName, String lastName) {
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
    
    public _011Address getAddress() {
		return address;
	}

	public void setAddress(_011Address address) {
		this.address = address;
	}
 
    @Override
    public String toString() {
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
    }

}

@Entity
@Table(name = "ADDRESS")
class _011Address {
	
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
    
    @OneToOne(mappedBy="address")
    private _011Student student;
 
    public _011Address() {
     }
 
    public _011Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }
 
    public _011Student getStudent() {
		return student;
	}

	public void setStudent(_011Student student) {
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
        return "Address [id=" + id + ", street=" + street + ", city=" + city
                + ", country=" + country + "]";
    }
     
}

class _011_OneToOne_BiDirectional{    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/011.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_011_OneToOne_BiDirectional demo = new _011_OneToOne_BiDirectional();
		demo.insert(sf);
		demo.select(sf);
		demo.inverseSelect(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_011Student student = new _011Student("bimal","jain");
			_011Address address = new _011Address("Fremont Blvd","Fremont","USA");
			//Here we have persisted Address class first in order to meet foreign key constraint(not null), then we have set student’s address property 
			//followed by persisting student.
			session.save(address);
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
			List<_011Student> students = (List<_011Student>)session.createQuery(" FROM _001._011Student").list();
			for(_011Student student : students){
				System.out.println(student.getFirstName() + " " + student.getAddress().toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_1_, stude0_.STUDENT_ADDRESS_ID as STUDENT_4_1_, stude0_.FIRST_NAME as FIRST_NA2_1_, stude0_.LAST_NAM
	E as LAST_NAM3_1_ from STUDENT stude0_
	Hibernate: select addre0_.ADDRESS_ID as ADDRESS_1_0_0_, addre0_.CITY as CITY2_0_0_, addre0_.COUNTRY as COUNTRY3_0_0_, addre0_.STREET as STREET4_0_0_, 
	stude1_.STUDENT_ID as STUDENT_1_1_1_, stude1_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, stude1_.FIRST_NAME as FIRST_NA2_1_1_, stude1_.LAST_NAME as LAST_NA
	M3_1_1_ from ADDRESS addre0_ left outer join STUDENT stude1_ on addre0_.ADDRESS_ID=stude1_.STUDENT_ADDRESS_ID where addre0_.ADDRESS_ID=?
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_1_1_, stude0_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, stude0_.FIRST_NAME as FIRST_NA2_1_1_, stude0_.LA
	ST_NAME as LAST_NAM3_1_1_, addre1_.ADDRESS_ID as ADDRESS_1_0_0_, addre1_.CITY as CITY2_0_0_, addre1_.COUNTRY as COUNTRY3_0_0_, addre1_.STREET as STREE
	T4_0_0_ from STUDENT stude0_ left outer join ADDRESS addre1_ on stude0_.STUDENT_ADDRESS_ID=addre1_.ADDRESS_ID where stude0_.STUDENT_ADDRESS_ID=?
	bimal Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]
	 */
	
	@SuppressWarnings("unchecked")
	public void inverseSelect(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_011Address> addresses = (List<_011Address>)session.createQuery(" FROM _001._011Address").list();
			for(_011Address address : addresses){
				System.out.println(address.getStudent().getFirstName() + " " + address.toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select addre0_.ADDRESS_ID as ADDRESS_1_0_, addre0_.CITY as CITY2_0_, addre0_.COUNTRY as COUNTRY3_0_, addre0_.STREET as STREET4_0_ from ADDR
	ESS addre0_
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_1_1_, stude0_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, stude0_.FIRST_NAME as FIRST_NA2_1_1_, stude0_.LA
	ST_NAME as LAST_NAM3_1_1_, addre1_.ADDRESS_ID as ADDRESS_1_0_0_, addre1_.CITY as CITY2_0_0_, addre1_.COUNTRY as COUNTRY3_0_0_, addre1_.STREET as STREE
	T4_0_0_ from STUDENT stude0_ left outer join ADDRESS addre1_ on stude0_.STUDENT_ADDRESS_ID=addre1_.ADDRESS_ID where stude0_.STUDENT_ADDRESS_ID=?
	bimal Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]
	 */
	
}
