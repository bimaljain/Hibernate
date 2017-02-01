/* 
Joined strategy is again TablePerClass strategy.
But this is strategy creates more normalized tables than TablePerClass strategy, since the parent columns are not copied to each child tables
We will have to do a join to get the entire data as:
select * from table1 join table2 on table1.id=table2.id

drop table VEHICLE;
drop table FOUR_WHEELER;
drop table TWO_WHEELER;

CREATE TABLE VEHICLE (
  ID INT NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(15) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE FOUR_WHEELER (
  ID INT NOT NULL,
  STEERINGWHEEL VARCHAR(15)
);

CREATE TABLE TWO_WHEELER (
  ID INT NOT NULL,
  HANDLE VARCHAR(15)
);

select * from VEHICLE;
select * from FOUR_WHEELER;
select * from TWO_WHEELER;

*/

package _001;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="VEHICLE")
@Inheritance(strategy=InheritanceType.JOINED)
class _018Vehicle {
	
	@Id
	@Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.TABLE)
	int vehicleId;
	
	@Column(name = "NAME")
	String vehicleName;

	public _018Vehicle() {
		super();
	}

	public _018Vehicle(String vehicleName) {
		super();
		this.vehicleName = vehicleName;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	@Override
	public String toString() {
		return "_018Vehicle [vehicleId=" + vehicleId + ", vehicleName=" + vehicleName + "]";
	}
}

@Entity
@Table(name="FOUR_WHEELER")
class _018FourWheeler extends _018Vehicle {	
	
	@Column(name = "STEERINGWHEEL")
	private String steeringWheel;

	public _018FourWheeler() {
		super();
	}

	public _018FourWheeler(String steeringWheel) {
		super();
		this.steeringWheel = steeringWheel;
	}

	public String getSteeringWheel() {
		return steeringWheel;
	}

	public void setSteeringWheel(String steeringWheel) {
		this.steeringWheel = steeringWheel;
	}
}

@Entity
@Table(name="TWO_WHEELER")
class _018TwoWheeler extends _018Vehicle {
	
	@Column(name = "HANDLE")
	private String handle;

	public _018TwoWheeler() {
		super();
	}

	public _018TwoWheeler(String handle) {
		super();
		this.handle = handle;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}	
}

public class _018_Inheritance_Joined{
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/018.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_018_Inheritance_Joined demo = new _018_Inheritance_Joined();
		demo.insert(sf);
	}
	
	public void insert(SessionFactory sf ){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_018Vehicle vehicle1 = new _018Vehicle("gaadi");
			_018FourWheeler vehicle2 = new _018FourWheeler("civic"); vehicle2.setVehicleName("honda");
			_018TwoWheeler vehicle3 = new _018TwoWheeler("activa"); vehicle3.setVehicleName("kinetic");
			session.save(vehicle1);
			session.save(vehicle2);
			session.save(vehicle3);
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
	Hibernate: select tbl.next_val from hibernate_sequences tbl where tbl.sequence_name=? for update
	Hibernate: update hibernate_sequences set next_val=?  where next_val=? and sequence_name=?
	Hibernate: select tbl.next_val from hibernate_sequences tbl where tbl.sequence_name=? for update
	Hibernate: update hibernate_sequences set next_val=?  where next_val=? and sequence_name=?
	Hibernate: select tbl.next_val from hibernate_sequences tbl where tbl.sequence_name=? for update
	Hibernate: update hibernate_sequences set next_val=?  where next_val=? and sequence_name=?
	Hibernate: insert into VEHICLE (NAME, ID) values (?, ?)
	Hibernate: insert into VEHICLE (NAME, ID) values (?, ?)
	Hibernate: insert into FOUR_WHEELER (STEERINGWHEEL, ID) values (?, ?)
	Hibernate: insert into VEHICLE (NAME, ID) values (?, ?)
	Hibernate: insert into TWO_WHEELER (HANDLE, ID) values (?, ?)

	ID          NAME            
	----------- --------------- 
	9           gaadi           
	10          honda           
	11          kinetic         
	
	
	ID          STEERINGWHEEL   
	----------- --------------- 
	10          civic           
	
	
	ID          HANDLE          
	----------- --------------- 
	11          activa 
	 
	 */
}
