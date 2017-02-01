/*
PURPOSE: Hibernate One-To-One Bidirectional Shared primary key mapping using annotation based configuration.

In One-To-One Bidirectional Shared primary key mapping, two tables share the same primary key.
The Bidirectional relationship means navigation is possible in both direction.

Note that now we have a student property in Address class and address property in Student class, which means we can now navigate in either direction.In 
hibernate, for bidirectional relationships like this, we have a concept of ownership, means who is the owner of this relationship.Put simply, who is 
responsible for updating the column in DB on which this relationship depends on. In our case it’s the student_id of Student table which is driving the 
complete relationship truck. So we should tell hibernate that it’s the Student class which will manage the relationship.

To do that, we can use mappedBy attribute. mappedBy attribute are always put(annotated) on the inverse side of relation ship and specifies with it’s 
attribute value, the owner of the relationship.

	@OneToOne(mappedBy="student", cascade = CascadeType.ALL)
	private Address address;
With this declaration, we ask hibernate to go and find the student property of Address class to know how to manage the relationship/perform some 
operation. Now in Address class , we have following declaration

	@OneToOne
	@PrimaryKeyJoinColumn
	private Student student;
Which simply says that both the Address table and Student table share the same primary key.

But how would you make sure to that Address class use the same id value as used by Student? You have to make sure in your code that you set the id of 
Address with the id of saved Student before you save address(as we did in one-to-one unidirectional tutorial).If you don’t want to do that, another option 
is to use hibernate specific annotation @GenericGenerator (that’s what we are doing now in Address class). In Address class, @GenericGenerator ensures 
that id value of Address property value will be taken from the id of Student table.

Look at how we have set address property of student and student property of address.Then we have called save only on student, thanks to Cascade attribute 
on address property of Student class, address will be saved automatically.

DROP TABLE ADDRESS;
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
)
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
 
    @OneToOne(mappedBy="student", cascade = CascadeType.ALL)
    private _009Address address; 
//	  Alternatively:
//    @OneToOne(cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    private _017_Address_OneToOne_BiDirectional address;
    
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
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
    }
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
    
    @OneToOne
    @PrimaryKeyJoinColumn
    private _009Student student;

//	  Alternatively: 
//    @OneToOne(mappedBy="address")
//    private _016_Student_OneToOne_BiDirectional student;
    
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
        return "Address [id=" + id + ", street=" + street + ", city=" + city
                + ", country=" + country + "]";
    }     
}

public class _009_OneToOne_BiDirectional {    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/009.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_009_OneToOne_BiDirectional demo = new _009_OneToOne_BiDirectional();
		demo.insert(sf);
		demo.select(sf);
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
			for(_009Student student : students){
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
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_1_, stude0_.FIRST_NAME as FIRST_NA2_1_, stude0_.LAST_NAME as LAST_NAM3_1_ from STUDENT stude0_
	Hibernate: select addre0_.ADDRESS_ID as ADDRESS_1_0_0_, addre0_.CITY as CITY2_0_0_, addre0_.COUNTRY as COUNTRY3_0_0_, addre0_.STREET as STREET4_0_0_, 
	stude1_.STUDENT_ID as STUDENT_1_1_1_, stude1_.FIRST_NAME as FIRST_NA2_1_1_, stude1_.LAST_NAME as LAST_NAM3_1_1_ from ADDRESS addre0_ left outer join S
	TUDENT stude1_ on addre0_.ADDRESS_ID=stude1_.STUDENT_ID where addre0_.ADDRESS_ID=?
	bimal Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]
	 */
}
