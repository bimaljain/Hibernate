package _001;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name = "STUDENT")
class _007Student {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDENT_ID")
    private long id;
 
    @Column(name = "FIRST_NAME")
    private String firstName;
 
    @Column(name = "LAST_NAME")
    private String lastName;
 
    //owner side of the relationship
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "STUDENT_SUBJECT", 
             joinColumns = { @JoinColumn(name = "STUDENT_ID") }, 
             inverseJoinColumns = { @JoinColumn(name = "SUBJECT_ID") })
    private List<_007Subject> subjects = new ArrayList<_007Subject>();
 
    public _007Student() {
    }
 
    public _007Student(String firstName, String lastName) {
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
 
    public List<_007Subject> getSubjects() {
        return subjects;
    }
 
    public void setSubjects(List<_007Subject> subjects) {
        this.subjects = subjects;
    } 
     
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof _007Student))
            return false;
        _007Student other = (_007Student) obj;
        if (id != other.id)
            return false;
        return true;
    }
 
    // this is needed to do select from Emp side
	@Override
	public String toString() {
		return "_007Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", subjects="
				+ subjects + "]";
	}
    
    //this is needed to do select from Subject side
//	@Override
//	public String toString() {
//		return "_007Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
//	}
}

@Entity
@Table(name = "SUBJECT")
class _007Subject {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBJECT_ID")
    private long id;
 
    @Column(name = "SUBJECT_NAME")
    private String name;
    
    //inverse side of relationship
    @ManyToMany(mappedBy="subjects")
    private List<_007Student> students = new ArrayList<_007Student>();
     
    public _007Subject(){         
    }
     
    public _007Subject(String name){
        this.name = name;
    }
 
    public List<_007Student> getStudents() {
		return students;
	}

	public void setStudents(List<_007Student> students) {
		this.students = students;
	}

	public long getId() {
        return id;
    }
 
    public void setId(long id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof _007Subject))
            return false;
        _007Subject other = (_007Subject) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
 
     // this is needed to do select from Emp side
    @Override
    public String toString() {
        return "Subject [id=" + id + ", name=" + name + "]";
    }
    
    // this is needed to do select from Subject side
//    @Override
//	public String toString() {
//		return "_007Subject [id=" + id + ", name=" + name + ", students=" + students + "]";
//	}   
}

public class _007_ManyToMany_BiDirectional {
    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/007.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_007_ManyToMany_BiDirectional demo = new _007_ManyToMany_BiDirectional();
		demo.insert(sf);
		demo.select(sf); //uncomment toString() as well.
//		demo.inverseSelect(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_007Student student1 = new _007Student("Sam","Disilva");
			_007Student student2 = new _007Student("Joshua", "Brill");
	         
			_007Subject subject1 = new _007Subject("Economics");
			_007Subject subject2 = new _007Subject("Politics");
			_007Subject subject3 = new _007Subject("Foreign Affairs");
	 
	        //Student1 have 3 subjects
			student1.getSubjects().add(subject1);
	        student1.getSubjects().add(subject2);
	        student1.getSubjects().add(subject3);
	         
	        //Student2 have 2 subjects
	        student2.getSubjects().add(subject1);
	        student2.getSubjects().add(subject2);
	        
	        session.save(student1);
	        session.save(student2);
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
	Hibernate: insert into SUBJECT (SUBJECT_NAME) values (?)
	Hibernate: insert into SUBJECT (SUBJECT_NAME) values (?)
	Hibernate: insert into SUBJECT (SUBJECT_NAME) values (?)
	Hibernate: insert into STUDENT (FIRST_NAME, LAST_NAME) values (?, ?)
	Hibernate: insert into STUDENT_SUBJECT (STUDENT_ID, SUBJECT_ID) values (?, ?)
	Hibernate: insert into STUDENT_SUBJECT (STUDENT_ID, SUBJECT_ID) values (?, ?)
	Hibernate: insert into STUDENT_SUBJECT (STUDENT_ID, SUBJECT_ID) values (?, ?)
	Hibernate: insert into STUDENT_SUBJECT (STUDENT_ID, SUBJECT_ID) values (?, ?)
	Hibernate: insert into STUDENT_SUBJECT (STUDENT_ID, SUBJECT_ID) values (?, ?)
	 */
		
	@SuppressWarnings("unchecked")
	public void select(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_007Student> students = (List<_007Student>)session.createQuery(" FROM _001._007Student").list();
			System.out.println(students);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select studen0_.STUDENT_ID as STUDENT_1_0_, studen0_.FIRST_NAME as FIRST_NA2_0_, studen0_.LAST_NAME as LAST_NAM3_0_ from STUDENT studen0_
	Hibernate: select subjects0_.STUDENT_ID as STUDENT_1_1_0_, subjects0_.SUBJECT_ID as SUBJECT_2_1_0_, subjec1_.SUBJECT_ID as SUBJECT_1_2_1_, subjec1_.SU
	BJECT_NAME as SUBJECT_2_2_1_ from STUDENT_SUBJECT subjects0_ inner join SUBJECT subjec1_ on subjects0_.SUBJECT_ID=subjec1_.SUBJECT_ID where subjects0_
	.STUDENT_ID=?
	Hibernate: select subjects0_.STUDENT_ID as STUDENT_1_1_0_, subjects0_.SUBJECT_ID as SUBJECT_2_1_0_, subjec1_.SUBJECT_ID as SUBJECT_1_2_1_, subjec1_.SU
	BJECT_NAME as SUBJECT_2_2_1_ from STUDENT_SUBJECT subjects0_ inner join SUBJECT subjec1_ on subjects0_.SUBJECT_ID=subjec1_.SUBJECT_ID where subjects0_
	.STUDENT_ID=?
	
	[_007Student [id=1, firstName=Sam, lastName=Disilva, subjects=[Subject [id=1, name=Economics], Subject [id=2, name=Politics], Subject [id=3, name=Foreign Affairs]]], 
	_007Student [id=2, firstName=Joshua, lastName=Brill, subjects=[Subject [id=1, name=Economics], Subject [id=2, name=Politics]]]]
	 */
	
	@SuppressWarnings("unchecked")
	public void inverseSelect(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_007Subject> subjects = (List<_007Subject>)session.createQuery(" FROM _001._007Subject").list();
			System.out.println(subjects);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*OUTPUT:
	Hibernate: select subjec0_.SUBJECT_ID as SUBJECT_1_2_, subjec0_.SUBJECT_NAME as SUBJECT_2_2_ from SUBJECT subjec0_
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, studen1_.STUDENT_ID as STUDENT_1_0_1_, studen1_.FI
	RST_NAME as FIRST_NA2_0_1_, studen1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT studen1_ on students0_.STUDENT_ID=
	studen1_.STUDENT_ID where students0_.SUBJECT_ID=?
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, studen1_.STUDENT_ID as STUDENT_1_0_1_, studen1_.FI
	RST_NAME as FIRST_NA2_0_1_, studen1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT studen1_ on students0_.STUDENT_ID=
	studen1_.STUDENT_ID where students0_.SUBJECT_ID=?
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, studen1_.STUDENT_ID as STUDENT_1_0_1_, studen1_.FI
	RST_NAME as FIRST_NA2_0_1_, studen1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT studen1_ on students0_.STUDENT_ID=
	studen1_.STUDENT_ID where students0_.SUBJECT_ID=?
	
	[_007Subject [id=1, name=Economics, students=[_007Student [id=1, firstName=Sam, lastName=Disilva], _007Student [id=2, firstName=Joshua, lastName=Brill]]], 
	_007Subject [id=2, name=Politics, students=[_007Student [id=1, firstName=Sam, lastName=Disilva], _007Student [id=2, firstName=Joshua, lastName=Brill]]], 
	_007Subject [id=3, name=Foreign Affairs, students=[_007Student [id=1, firstName=Sam, lastName=Disilva]]]]
	 */
}
