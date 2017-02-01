/*
PURPOSE: Hibernate Many-To-Many Bidirectional mapping using annotation based configuration.

	@ManyToMany(mappedBy="subjects")
	private List<Student> students = new ArrayList<Student>();
Nothing else changes.We added this property in Subject class to make the relationship bidirectional.You can now navigate from Subject to Student.mappedBy 
attribute tells that this is the inverse side of relationship which is managed by “subjects” property of Student annotated with @JoinColumn.

If you prefer to recall (as in Unidirectional) , owner side of relationship is defined in Student class as follows
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "STUDENT_SUBJECT", 
	    joinColumns = { @JoinColumn(name = "STUDENT_ID") }, 
	    inverseJoinColumns = { @JoinColumn(name = "SUBJECT_ID") })
	private List<Subject> subjects = new ArrayList<Subject>();

 */
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
 
    @Override
    public String toString() {
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
    }
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
 
    @Override
    public String toString() {
        return "Subject [id=" + id + ", name=" + name + "]";
    }
}

public class _007_ManyToMany_BiDirectional {
    
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/007.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_007_ManyToMany_BiDirectional demo = new _007_ManyToMany_BiDirectional();
		demo.insert(sf);
		demo.select(sf);
		demo.inverseSelect(sf);
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
			List<_007Student> students = 
					(List<_007Student>)session.createQuery(" FROM _001._007Student").list();
			for(_007Student student : students){
				System.out.println(student.getFirstName() + " " + student.getSubjects().toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select stude0_.STUDENT_ID as STUDENT_1_0_, stude0_.FIRST_NAME as FIRST_NA2_0_, stude0_.LAST_NAME as LAST_NAM3_0_ from STUDENT stude0_
	Hibernate: select subjects0_.STUDENT_ID as STUDENT_1_1_0_, subjects0_.SUBJECT_ID as SUBJECT_2_1_0_, subje1_.SUBJECT_ID as SUBJECT_1_2_1_, subje1_.SUBJ
	ECT_NAME as SUBJECT_2_2_1_ from STUDENT_SUBJECT subjects0_ inner join SUBJECT subje1_ on subjects0_.SUBJECT_ID=subje1_.SUBJECT_ID where subjects0_.STU
	DENT_ID=?
	Sam [Subject [id=1, name=Economics], Subject [id=2, name=Politics], Subject [id=3, name=Foreign Affairs]]
	
	Hibernate: select subjects0_.STUDENT_ID as STUDENT_1_1_0_, subjects0_.SUBJECT_ID as SUBJECT_2_1_0_, subje1_.SUBJECT_ID as SUBJECT_1_2_1_, subje1_.SUBJ
	ECT_NAME as SUBJECT_2_2_1_ from STUDENT_SUBJECT subjects0_ inner join SUBJECT subje1_ on subjects0_.SUBJECT_ID=subje1_.SUBJECT_ID where subjects0_.STU
	DENT_ID=?
	Joshua [Subject [id=1, name=Economics], Subject [id=2, name=Politics]]
	 */
	
	@SuppressWarnings("unchecked")
	public void inverseSelect(SessionFactory sf) throws IOException{
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_007Subject> subjects = (List<_007Subject>)session.createQuery(" FROM _001._007Subject").list();
			for(_007Subject subject : subjects){
				System.out.println(subject.getName() + " " + subject.getStudents().toString());
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*OUTPUT:
	Hibernate: select subje0_.SUBJECT_ID as SUBJECT_1_2_, subje0_.SUBJECT_NAME as SUBJECT_2_2_ from SUBJECT subje0_
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, stude1_.STUDENT_ID as STUDENT_1_0_1_, stude1_.FIRS
	T_NAME as FIRST_NA2_0_1_, stude1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT stude1_ on students0_.STUDENT_ID=stud
	e1_.STUDENT_ID where students0_.SUBJECT_ID=?
	Economics [Student [id=1, firstName=Sam, lastName=Disilva], Student [id=2, firstName=Joshua, lastName=Brill]]
	
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, stude1_.STUDENT_ID as STUDENT_1_0_1_, stude1_.FIRS
	T_NAME as FIRST_NA2_0_1_, stude1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT stude1_ on students0_.STUDENT_ID=stud
	e1_.STUDENT_ID where students0_.SUBJECT_ID=?
	Politics [Student [id=1, firstName=Sam, lastName=Disilva], Student [id=2, firstName=Joshua, lastName=Brill]]
	
	Hibernate: select students0_.SUBJECT_ID as SUBJECT_2_1_0_, students0_.STUDENT_ID as STUDENT_1_1_0_, stude1_.STUDENT_ID as STUDENT_1_0_1_, stude1_.FIRS
	T_NAME as FIRST_NA2_0_1_, stude1_.LAST_NAME as LAST_NAM3_0_1_ from STUDENT_SUBJECT students0_ inner join STUDENT stude1_ on students0_.STUDENT_ID=stud
	e1_.STUDENT_ID where students0_.SUBJECT_ID=?
	Foreign Affairs [Student [id=1, firstName=Sam, lastName=Disilva]]
	 */
}
