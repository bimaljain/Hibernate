/*
PURPOSE: Hibernate One-To-One Unidirectional Shared primary key mapping. In One-To-One Unidirectional Shared primary key mapping, two tables share the 
same primary key. The Unidirectional relationship means only one side navigation is possible.(STUDENT to ADDRESS in this example)

1. @OneToOne on address property of Student class indicates that there is a one-to-one association from Student to Address. PrimaryKeyJoinColumn indicates 
that the primary key of the Student entity is used as a foreign key to the Address entity. Together these two annotation indicates that both the source 
and target share the same primary key values.

2. We have first persisted Student class, so that it’s id can be generated. Then we have set the address id with student id(so that foreign key constraint 
can be respected).Finally we have set the address property of Student and saved student.Thanks to Cascade attribute on address property of Student class, 
address will be saved automatically on student save. No need to save the address explicitly.

3. If you don’t want to do that, another option is to use hibernate specific annotation @GenericGenerator. In Address class, @GenericGenerator ensures 
that id value of Address property value will be taken from the id of Student table. (see next example)

-----------
DB DETAILS:
-----------
DROP TABLE ADDRESS;
DROP TABLE STUDENT_SUBJECT;
DROP TABLE STUDENT;

create table STUDENT (
   student_id INT NOT NULL AUTO_INCREMENT,
   first_name VARCHAR(30) NOT NULL,
   last_name  VARCHAR(30) NOT NULL,
   PRIMARY KEY (student_id)
);

create table ADDRESS (
   ADDRESS_ID INT NOT NULL,
   street VARCHAR(30),
   city  VARCHAR(30),
   country  VARCHAR(30),
   PRIMARY KEY (ADDRESS_ID),
   CONSTRAINT student_address FOREIGN KEY (ADDRESS_ID) REFERENCES STUDENT (student_id) ON UPDATE CASCADE ON DELETE CASCADE
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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name = "STUDENT")
class _008Student {
	
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
    private _008Address address;
 
    public _008Student() {
    }
 
    public _008Student(String firstName, String lastName) {
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
    
    public _008Address getAddress() {
		return address;
	}

	public void setAddress(_008Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "_008Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + "]";
	}
}

@Entity
@Table(name = "ADDRESS")
class _008Address {
	
	@Id
    @Column(name = "ADDRESS_ID")
    private long id;
 
    @Column(name = "STREET")
    private String street;
 
    @Column(name = "CITY")
    private String city;
 
    @Column(name = "COUNTRY")
    private String country;
 
    public _008Address() {
 
    }
 
    public _008Address(String street, String city, String country) {
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

public class _008_OneToOne_UniDirectional {    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/008.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_008_OneToOne_UniDirectional demo = new _008_OneToOne_UniDirectional();
		demo.insert(sf);
		demo.select(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_008Student student = new _008Student("bimal","jain");
			_008Address address = new _008Address("Fremont Blvd","Fremont","USA");
			session.persist(student);				
			address.setId(student.getId());			
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
	Hibernate: insert into STUDENT (FIRST_NAME, LAST_NAME) values (?, ?)
	Hibernate: select addre_.ADDRESS_ID, addre_.CITY as CITY2_0_, addre_.COUNTRY as COUNTRY3_0_, addre_.STREET as STREET4_0_ from ADDRESS addre_ where add
	re_.ADDRESS_ID=?
	Hibernate: insert into ADDRESS (CITY, COUNTRY, STREET, ADDRESS_ID) values (?, ?, ?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_008Student> students = (List<_008Student>)session.createQuery(" FROM _001._008Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_1_, stude0_.FIRST_NAME as FIRST_NA2_1_, stude0_.LAST_NAME as LAST_NAM3_1_ from STUDENT stude0_
	Hibernate: select addre0_.ADDRESS_ID as ADDRESS_1_0_0_, addre0_.CITY as CITY2_0_0_, addre0_.COUNTRY as COUNTRY3_0_0_, addre0_.STREET as STREET4_0_0_ f
	rom ADDRESS addre0_ where addre0_.ADDRESS_ID=?
	[_008Student [id=1, firstName=bimal, lastName=jain, address=Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]]]
	 */
}
