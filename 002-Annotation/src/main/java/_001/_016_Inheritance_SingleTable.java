/* 
Default class inheritance mapping strategy. Its one common table per class hierarchy
Discriminator column maintains the actual class name. This column tells you what kind of object the row represents.


drop table VEHICLE;

CREATE TABLE VEHICLE (
  ID INT NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(15) NOT NULL,
  STEERINGWHEEL VARCHAR(15),
  HANDLE VARCHAR(15),
  VEHICLE_TYPE VARCHAR(15),
  PRIMARY KEY (ID)
);

select * from VEHICLE
*/

package _001;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
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
@Inheritance(strategy=InheritanceType.SINGLE_TABLE) // default strategy. We can comment this line, and get the same result.
@DiscriminatorColumn(name="VEHICLE_TYPE", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("vehicle")
class _016Vehicle {
	
	@Id
	@Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int vehicleId;
	
	@Column(name = "NAME")
	String vehicleName;

	public _016Vehicle() {
		super();
	}

	public _016Vehicle(String vehicleName) {
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
		return "_016Vehicle [vehicleId=" + vehicleId + ", vehicleName=" + vehicleName + "]";
	}
}

@Entity
@DiscriminatorValue("fourwheeler")
class _016FourWheeler extends _016Vehicle {	
	
	@Column(name = "STEERINGWHEEL")
	private String steeringWheel;

	public _016FourWheeler() {
		super();
	}

	public _016FourWheeler(String steeringWheel) {
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
@DiscriminatorValue("twoweeler")
class _016TwoWheeler extends _016Vehicle {
	
	@Column(name = "HANDLE")
	private String handle;

	public _016TwoWheeler() {
		super();
	}

	public _016TwoWheeler(String handle) {
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

public class _016_Inheritance_SingleTable{
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001/016.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		_016_Inheritance_SingleTable demo = new _016_Inheritance_SingleTable();
		demo.insert(sf);
	}
	
	public void insert(SessionFactory sf ){
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_016Vehicle vehicle1 = new _016Vehicle("gaadi");
			_016FourWheeler vehicle2 = new _016FourWheeler("civic"); vehicle2.setVehicleName("honda");
			_016TwoWheeler vehicle3 = new _016TwoWheeler("activa"); vehicle3.setVehicleName("kinetic");
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
	Hibernate: insert into VEHICLE (NAME, VEHICLE_TYPE) values (?, 'vehicle')
	Hibernate: insert into VEHICLE (NAME, STEERINGWHEEL, VEHICLE_TYPE) values (?, ?, 'fourwheeler')
	Hibernate: insert into VEHICLE (NAME, HANDLE, VEHICLE_TYPE) values (?, ?, 'twoweeler')
	
	ID          NAME            STEERINGWHEEL   HANDLE          VEHICLE_TYPE    
	----------- --------------- --------------- --------------- --------------- 
	1           gaadi                                           vehicle         
	2           honda           civic                           fourwheeler     
	3           kinetic                         activa          twoweeler 
	 
	 */
}
