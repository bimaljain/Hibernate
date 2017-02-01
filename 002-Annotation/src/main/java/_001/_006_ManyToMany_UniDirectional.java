/*
PURPOSE: Hibernate Many-To-Many Unidirectional mapping using annotation based configuration.

In Many-To-Many association, an extra table is used (known as Joined table) whose primary key is the combination of primary key of both the associated 
tables.In other words there is a foreign key association between the joined table and the associated tables.

@ManyToMany indicates that there is a Many-to-Many relationship between Student and subject. A Student can enroll for multiple subjects, and a subject can 
have multiple students enrolled.Notice cascade = CascadeType.ALL, with cascading while persisting (update/delete) Student tuples, subjects tuples will 
also be persisted (updated/deleted).

@JoinTable indicates that there is a link table which joins two tables via containing there keys.This annotation is mainly used on the owning side of the 
relationship.joinColumns refers to the column name of owning side(STUDENT_ID of STUDENT), and inverseJoinColumns refers to the column of inverse side of 
relationship(SUBJECT_ID of SUBJECT).Primary key of this joined table is combination of STUDENT_ID & SUBJECT_ID.

In case of *Many* association, always override hashcode and equals method which are looked by hibernate when holding entities into collections.

DROP TABLE STUDENT_SUBJECT;
DROP TABLE STUDENT;
DROP TABLE SUBJECT;

create table STUDENT (
   student_id INT NOT NULL AUTO_INCREMENT,
   first_name VARCHAR(30) NOT NULL,
   last_name  VARCHAR(30) NOT NULL,
   PRIMARY KEY (student_id)
); 
 
create table SUBJECT (
   subject_id INT NOT NULL AUTO_INCREMENT,
   subject_name VARCHAR(30) NOT NULL,
   PRIMARY KEY (subject_id)
); 
 
CREATE TABLE STUDENT_SUBJECT (
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    PRIMARY KEY (student_id, subject_id),
    CONSTRAINT FK_STUDENT FOREIGN KEY(student_id) REFERENCES STUDENT (student_id),
    CONSTRAINT FK_SUBJECT FOREIGN KEY(subject_id) REFERENCES SUBJECT (subject_id)
);

select * from STUDENT;
select * from SUBJECT;
select * from STUDENT_SUBJECT;
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
class _006Student {
	
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
    private List<_006Subject> subjects = new ArrayList<_006Subject>();
 
    public _006Student() {
    }
 
    public _006Student(String firstName, String lastName) {
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
 
    public List<_006Subject> getSubjects() {
        return subjects;
    }
 
    public void setSubjects(List<_006Subject> subjects) {
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
        if (!(obj instanceof _006Student))
            return false;
        _006Student other = (_006Student) obj;
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
class _006Subject {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBJECT_ID")
    private long id;
 
    @Column(name = "SUBJECT_NAME")
    private String name;
     
    public _006Subject(){         
    }
     
    public _006Subject(String name){
        this.name = name;
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
        if (!(obj instanceof _006Subject))
            return false;
        _006Subject other = (_006Subject) obj;
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

public class _006_ManyToMany_UniDirectional {
	
    public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/006.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
    	_006_ManyToMany_UniDirectional demo = new _006_ManyToMany_UniDirectional();
		demo.insert(sf);
		demo.select(sf);
	}
    
	public void insert(SessionFactory sf){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_006Student student1 = new _006Student("Sam","Disilva");
			_006Student student2 = new _006Student("Joshua", "Brill");
	         
			_006Subject subject1 = new _006Subject("Economics");
			_006Subject subject2 = new _006Subject("Politics");
			_006Subject subject3 = new _006Subject("Foreign Affairs");
	 
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
			List<_006Student> students = (List<_006Student>)session.createQuery(" FROM _001._006Student").list();
			for(_006Student student : students){
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
}
