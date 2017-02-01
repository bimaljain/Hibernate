/*
PURPOSE: Hibernate One-To-One Unidirectional Foreign Key association mapping using annotation based configuration.

In One-To-One Unidirectional with Foreign Key association mapping, one table has a foreign key column that references the primary key of associated table.
By Unidirectional relationship means only one side navigation is possible (STUDENT to ADDRESS in this example).

Difference between Student class define here and in the previous tutorial(shared primary key) is that we have replaced @PrimaryKeyJoinColumn with 
@joinColumn which maps on a seperate column in database but will still point to primary key of address table, thanks to the foreign key constrained we 
have declared during table creation time. Address class is completly independent of Student table.

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
class _010Student {
	
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
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
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
        return "Address [id=" + id + ", street=" + street + ", city=" + city
                + ", country=" + country + "]";
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
			List<_010Student> students = (List<_010Student>)session.createQuery(" FROM _001._010Student").list();
			for(_010Student student : students){
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
	Hibernate: select addre0_.ADDRESS_ID as ADDRESS_1_0_0_, addre0_.CITY as CITY2_0_0_, addre0_.COUNTRY as COUNTRY3_0_0_, addre0_.STREET as STREET4_0_0_ f
	rom ADDRESS addre0_ where addre0_.ADDRESS_ID=?
	bimal Address [id=2, street=Fremont Blvd, city=Fremont, country=USA]
	 */
}
