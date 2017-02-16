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
class _011Student {
	
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
		return "_011Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

//	@Override
//	public String toString() {
//		return "_011Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address + "]";
//	}	
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
		return "_011Address [id=" + id + ", street=" + street + ", city=" + city + ", country=" + country + ", student=" + student + "]";
	}
 
//    @Override
//    public String toString() {
//        return "Address [id=" + id + ", street=" + street + ", city=" + city + ", country=" + country + "]";
//    }     
}

class _011_OneToOne_BiDirectional{    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/011.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_011_OneToOne_BiDirectional demo = new _011_OneToOne_BiDirectional();
		demo.insert(sf);
//		demo.select(sf);
		demo.inverseSelect(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_011Student student = new _011Student("bimal","jain");
			_011Address address = new _011Address("Fremont Blvd","Fremont","USA");
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
	0_, studen1_.STUDENT_ID as STUDENT_1_1_1_, studen1_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, studen1_.FIRST_NAME as FIRST_NA2_1_1_, studen1_.LAST_NAME as
	 LAST_NAM3_1_1_ from ADDRESS addres0_ left outer join STUDENT studen1_ on addres0_.ADDRESS_ID=studen1_.STUDENT_ADDRESS_ID where addres0_.ADDRESS_ID=?
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_1_1_, studen0_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, studen0_.FIRST_NAME as FIRST_NA2_1_1_, studen0
	_.LAST_NAME as LAST_NAM3_1_1_, addres1_.ADDRESS_ID as ADDRESS_1_0_0_, addres1_.CITY as CITY2_0_0_, addres1_.COUNTRY as COUNTRY3_0_0_, addres1_.STREET 
	as STREET4_0_0_ from STUDENT studen0_ left outer join ADDRESS addres1_ on studen0_.STUDENT_ADDRESS_ID=addres1_.ADDRESS_ID where studen0_.STUDENT_ADDRE
	SS_ID=?
	
	[_011Student [id=1, firstName=bimal, lastName=jain, address=Address [id=1, street=Fremont Blvd, city=Fremont, country=USA]]]
	 */
	
	@SuppressWarnings("unchecked")
	public void inverseSelect(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_011Address> addresses = (List<_011Address>)session.createQuery(" FROM _001._011Address").list();
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
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_1_1_, studen0_.STUDENT_ADDRESS_ID as STUDENT_4_1_1_, studen0_.FIRST_NAME as FIRST_NA2_1_1_, studen0
	_.LAST_NAME as LAST_NAM3_1_1_, addres1_.ADDRESS_ID as ADDRESS_1_0_0_, addres1_.CITY as CITY2_0_0_, addres1_.COUNTRY as COUNTRY3_0_0_, addres1_.STREET 
	as STREET4_0_0_ from STUDENT studen0_ left outer join ADDRESS addres1_ on studen0_.STUDENT_ADDRESS_ID=addres1_.ADDRESS_ID where studen0_.STUDENT_ADDRE
	SS_ID=?
	
	[_011Address [id=1, street=Fremont Blvd, city=Fremont, country=USA, student=_011Student [id=1, firstName=bimal, lastName=jain]]]
	 */
	
}
