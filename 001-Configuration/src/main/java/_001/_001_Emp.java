/* 
------------------------------------
Why Object Relational Mapping (ORM)?
------------------------------------
When we work with an object-oriented system, there's a mismatch between the object model and the relational database. Consider the following Employee Class
and the EMPLOYEE table. Also consider that the Employee objects need to be stored and retrieved into the EMPLOYEE table:	

	public class Employee {
	   private int id;
	   private String first_name; 
	   private int salary;  
	 
	   public Employee() {}
	   public Employee(String fname, int salary) {
	      this.first_name = fname;  this.salary = salary; 
	   }
	   public int getId() { return id; }
	   public String getFirstName() { return first_name; }
	   public int getSalary() { return salary; }
	   }

	create table EMPLOYEE (
	   id INT NOT NULL auto_increment,
	   first_name VARCHAR(20) default NULL,
	   salary     INT  default NULL,
	   PRIMARY KEY (id)
	
	);

Loading and storing objects in a relational database exposes us to the following mismatch problems:	
Granularity: Sometimes you will have an object model which has more classes than the number of corresponding tables in the database.
Inheritance: RDBMSs do not define anything similar to Inheritance which is a natural paradigm in object-oriented programming languages.
Identity: A RDBMS defines exactly one notion of 'sameness': the primary key. Java, however, defines both object identity (a==b) and object equality 
	(a.equals(b)).
Associations: Object-oriented languages represent associations using object references whereas an RDBMS represents an association as a foreign key column.
Navigation: RDBMSs represent data in a tabular format whereas object-oriented languages represent it as an interconnected graph of objects. The ways you
 	access objects in Java and in a RDBMS are fundamentally different.
The ORM is the solution to handle all the above mismatches.	
 
ORM Overview: 
ORM is a programming technique for converting data between relational databases and object oriented programming languages. An ORM system has following 
advantages over plain JDBC.
· No need to deal with the database implementation.
· Hides details of SQL queries from OO logic.
· Entities based on business concepts rather than database structure.
· Lets business code access objects rather than DB tables.
· Transaction management and automatic key generation.
· Fast development of application.

-------------------
Hibernate Overview:
------------------- 
In layman’s terms, Hibernate sits between your application and database and provide the persistence for your application via set of API’s, performing 
transformation(based on defined metadata) transparently between java objects and database representation. Hibernate functionality/flow/usage can be 
described as follows:
1. On Application startup, hibernate reads it configuration file(hibernate.cfg.xml or hibernate.properties) which contains information required to make 
the connection with underlying database and mapping information. Based on this information, hibernate creates Configuration Object , which in turns creates
SessionFactory which acts as singleton for the whole application.
2. Hibernate creates instances of entity classes.Entity classes are java classes which are mapped to the database table using metadata(XML/Annotaitons).
These instances are called transient objects as they are not yet persisted in database.
3. To persist an object, application ask for a Session from SessionFactory which is a factory for Session.Session represent a physical database connection.
4. Application then starts the transaction to make the unit of work atomic, &  uses Session API’s to finally persist the entity instance in database.Once 
the entity instance persisted in database, it’s  known as  persistent object as it represent a row in database table.Application then closes/commits the 
transaction followed by session close.
5. Once the session gets closed , the entity instance becomes detatched which means it still contains data but no more attached to the database table & no 
more under the management of Hibernate. Detatched objects can again become persistent when associated with a new Session, or can be garbage collected once 
no more used.

------------------
Hibernate Objects:
------------------
Configuration (org.hibernate.cfg.Configuration): It allows the application on startup, to specify properties and mapping documents to be used when 
creating a SessionFactory. Properties file contains database connection setup info while mapping specifies the classes to be mapped.  
If we use hibernate.cfg.xml or hibernate.properties as file names and saves it at the root of application classpath, we don’t have to specify anything to 
hibernate when we create the configuration object.

SessionFactory (org.hibernate.SessionFactory): It’s a thread-safe immutable object created per database & mainly used for creating Sessions. It caches 
generated SQL statements and other mapping metadata that Hibernate uses at runtime. You would need one SessionFactory object per database using a separate 
configuration file. So if you are using multiple databases then you would have to create multiple SessionFactory objects.

Session (org.hibernate.Session): A Session is used to get a physical connection with a database. It’s a single-threaded object used to perform create, 
read, update and delete operations for instances of mapped entity classes. Since it’s not thread-safe, it should not be long-lived and each 
thread/transaction should obtain its own instance from a SessionFactory. Instances may exist in one of the following three states at a given point in time:
1. transient: A new instance of a persistent class which is not associated with a Session and has no representation in the database and no identifier 
	value, is considered transient by Hibernate.
2. persistent: You can make a transient instance persistent by associating it with a Session. A persistent instance has a representation in the database, 
	an identifier value and is associated with a Session.
3. detached: Once we close the Hibernate Session, the persistent instance will become a detached instance.

Transaction (org.hibernate.Transaction): It’s a single-thread object used by the application to define units of work. A transaction is associated with a 
Session. Transactions abstract application code from underlying transaction implementations(JTA/JDBC), allowing the application to control transaction 
boundaries via a consistent API. It’s an Optional API and application may choose not to use it.

Query (org.hibernate.Query): A single-thread object used to perform query on underlying database. A Session is a factory for Query. Both HQL(Hibernate 
Query Language) & SQL can be used with Query object.

Criteria (org.hibernate.Criteria): It is an alternative to HQL, very useful for the search query involving multiple conditions.

---------------------------
Hibernate Persistent Class:
---------------------------
Java classes whose instances will be stored in database tables are called persistent classes in Hibernate. When you design a class to be persisted by 
Hibernate, it's important to provide JavaBeans compliant code. There are following main rules of persistent classes, however, none of these rules are hard 
requirements.
- All Java classes that will be persisted need a default constructor.
- All attributes that will be persisted should be declared private and have public getXXX and setXXX methods.
- All classes should contain an ID so that the objects can be easily identified within Hibernate and the database. This property maps to the primary key 
column of a database table.

------------------------
Hibernate Configuration:
------------------------
Following is the list of important properties you would require to configure for a database in a standalone situation:
hibernate.dialect: This property makes Hibernate generate the appropriate SQL for the chosen database.
hibernate.connection.driver_class: Defines the database specific driver hibernate will use to make connection. 
hibernate.connection.url: The JDBC URL to the database instance.
hibernate.connection.username: The database username.
hibernate.connection.password: The database password.

If you are using a database along with an application server and JNDI then you would have to configure the following properties:
hibernate.connection.datasource: The JNDI name defined in the application server context you are using for the app.
hibernate.jndi.class: The InitialContext class for JNDI.
hibernate.jndi.<JNDIpropertyname>: Passes any JNDI property you like to the JNDI InitialContext.
hibernate.jndi.url: Provides the URL for JNDI.
hibernate.connection.username: The database username.
hibernate.connection.password: The database password.

------------------
Hibernate Dialect:
------------------
Following is the list of various important databases dialect property type:
DB2: org.hibernate.dialect.DB2Dialect
Informix: org.hibernate.dialect.InformixDialect
Microsoft SQL Server 2008: org.hibernate.dialect.SQLServer2008Dialect
MySQL: org.hibernate.dialect.MySQLDialect
Oracle 10g: org.hibernate.dialect.Oracle10gDialect
SAP DB: org.hibernate.dialect.SAPDBDialect
Sybase:	org.hibernate.dialect.SybaseDialect

------------------------
Hibernate Mapping Files:
------------------------
An Object/relational mappings are usually defined in an XML document. This mapping file instructs Hibernate how to map the defined class/classes to the 
database tables. You should save the mapping document in a file with the format <classname>.hbm.xml.
1. The <class> elements are used to define specific mappings from a Java classes to the database tables. The Java class name is specified using the name 
	attribute of the class element and the database table name is specified using the table attribute.
2. The <id> element maps the unique ID attribute in class to the primary key of the database table. The name attribute of the id element refers to the
	property in the class and the column attribute refers to the column in the database table. The type attribute holds the hibernate mapping type, this 
	mapping types will convert from Java to SQL data type.
3. The <generator> element within the id element is used to automatically generate the primary key values.
4. The <property> element is used to map a Java class property to a column in the database table.

------------------------
Hibernate Mapping Types:
------------------------
When you prepare a Hibernate mapping document, we have seen that you map Java data types into RDBMS data types. The types declared and used in the mapping 
files are not Java data types; they are not SQL database types either. These types are called Hibernate mapping types, which can translate from Java to 
SQL data types and vice versa.

----------------------------------------------------------------------------------------------------
Primitive types			Mapping type		Java type							ANSI SQL Type
----------------------------------------------------------------------------------------------------
						Integer				int or java.lang.Integer			INTEGER
						Long				long or java.lang.Long				BIGINT
						Short				short or java.lang.Short			SMALLINT
						Float				float or java.lang.Float			FLOAT
						Double				double or java.lang.Double			DOUBLE
						big_decimal			java.math.BigDecimal				NUMERIC
						Character			java.lang.String					CHAR(1)
						String				java.lang.String					VARCHAR
						Byte				byte or java.lang.Byte				TINYINT
						Boolean				boolean or java.lang.Boolean		BIT
						yes/no				boolean or java.lang.Boolean		CHAR(1) ('Y' or 'N')
						true/false			boolean or java.lang.Boolean		CHAR(1) ('T' or 'F')
----------------------------------------------------------------------------------------------------
Date and time types:	Mapping type		Java type								ANSI SQL Type
----------------------------------------------------------------------------------------------------
					 	Date				java.util.Date or java.sql.Date			DATE
						Time				java.util.Date or java.sql.Time			TIME
						Timestamp			java.util.Date or java.sql.Timestamp	TIMESTAMP
						Calendar			java.util.Calendar						TIMESTAMP
						calendar_date		java.util.Calendar						DATE
-------------------------------------------------------------------------------------------------------------------
						Mapping type		Java type											ANSI SQL Type
-------------------------------------------------------------------------------------------------------------------
					 	Binary				byte[]												VARBINARY (or BLOB)
						Text				java.lang.String									CLOB
						Serializable		any Java class that implements java.io.Serializable	VARBINARY (or BLOB)
						Clob				java.sql.Clob										CLOB
						Blob				java.sql.Blob										BLOB
-------------------------------------------------------------------------------------
						Mapping type		Java type					ANSI SQL Type
-------------------------------------------------------------------------------------
					 	Class				java.lang.Class				VARCHAR
						Locale				java.util.Locale			VARCHAR
						Timezone			java.util.TimeZone			VARCHAR
						Currency			java.util.Currency			VARCHAR

------------------
Generator Classes:
------------------
Increment: This is used to generate primary keys of type long, short or int that are unique only. It should not be used in the clustered deployment 
environment.
Sequence: Hibernate can also use the sequences to generate the primary key. It can be used with DB2, Oracle, SAP DB databases.
foreign: Uses the identifier of another associated object. Usually used in conjunction with a <one-to-one> primary key association.
Assigned: Lets the application to assign an identifier to the object before save() is called. This is the default strategy if no <generator> element is 
specified.
identity: The returned identifier is of type long, short or int. It can be used with DB2, MySQL, MSSQL Server, Sybase.
hilo: The hilo generator uses a hi/lo algorithm to efficiently generate identifiers of type long, short or int, given a table and column (by default 
hibernate_unique_key and next_hi respectively) as a source of hi values. The hi/lo algorithm generates identifiers that are unique only for a particular 
database. Do not use this generator with connections enlisted with JTA or with a user-supplied connection.
seqhilo: The seqhilo generator uses a hi/lo algorithm to efficiently generate identifiers of type long, short or int, given a named database sequence.
uuid: The uuid generator uses a 128-bit UUID algorithm to generate identifiers of type string, unique within a network (the IP address is used). The UUID is encoded as a string of hexadecimal digits of length 32.
guid: It uses a database-generated GUID string on MS SQL Server and MySQL.
native: It picks identity, sequence or hilo depending upon the capabilities of the underlying database.
select: Retrieves a primary key assigned by a database trigger by selecting the row by some unique key and retrieving the primary key value.

Put simply, Hibernate operations follow a pattern like:
Configuration -> Create SessionFactory -> Open session -> begin transaction-> do something with entities -> commit transaction
*/

package _001; 

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class _001_Emp {
	int eid;
	String name;
	String address;
	double salary;
	
	//Constructor with arguments is for our use only. Hibernate need only no-arg constructor.
	public _001_Emp() {	}

	public _001_Emp(String name, String address, double salary) {
		this.name = name;
		this.address = address;
		this.salary = salary;	}

	public int getEid() { return eid; }
	public void setEid(int eid) { this.eid = eid; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public double getSalary() { return salary; }
	public void setSalary(double salary) { this.salary = salary; }
	
	public String toString(){
		return this.eid + " " + this.name + " " + this.address + " " + this.salary;
	}

	public static void main(String[] args) throws IOException {
		Demo demo = new Demo();
		demo.insert();
		demo.select();
		demo.update();
		demo.delete();
		demo.selectAll();
	}
}

class Demo{
	//Using custom hibernate.cfg.xml
	public void insert(){
		Configuration cfg = new Configuration().configure("hibernate1.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001_Emp emp1 = new _001_Emp("Bimal","Pune",23456);
			_001_Emp emp2 = new _001_Emp("meghna","Pune",98765);
			session.save(emp1);
			session.save(emp2);
			tx.commit();
		} catch (HibernateException e) {
			//If the Session throws an exception, the transaction must be rolled back and the session must be closed.
			tx.rollback();
			e.printStackTrace();
		}finally {
			session.close(); 
		}
	}
	/*
	OUTPUT:
	Hibernate: select max(ENO) from EMP
	Hibernate: insert into EMP (ENAME, EADDRESS, ESALARY, ENO) values (?, ?, ?, ?)
	Hibernate: insert into EMP (ENAME, EADDRESS, ESALARY, ENO) values (?, ?, ?, ?)
	 */
	
	//Using custom hibernate.properties
	public void select() throws IOException{
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate1.properties"));
		//hibernate.properties is a standard Property file containing database connection information. In this case, you can provide mapping information 
		//directly while creating Configuration Object.		
		Configuration cfg = new Configuration().addResource("001_Emp.hbm.xml").setProperties(properties);
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001_Emp emp=(_001_Emp)session.get(_001_Emp.class, new Integer(5));
			System.out.println(emp);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_0_, emp0_.ENAME as ENAME2_0_0_, emp0_.EADDRESS as EADDRESS3_0_0_, emp0_.ESALARY as ESALARY4_0_0_ from EMP emp0_ 
	where emp0_.ENO=?
	5 Bimal Pune 23456.0
	 */
	
	//Using default hibernate.cfg.xml
	public void update(){
		/*This code will call hibernate.cfg.xml by default:
		Configuration configuration = new Configuration().configure();*/
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001_Emp emp=(_001_Emp)session.get(_001_Emp.class, new Integer(5));
			emp.setName("Bimal Bakliwal-2");
			emp.setAddress("NJ");
			emp.setSalary(58906);
			//No need to update manually as it will be updated automatically on transaction close.
			//session.update(emp); 
			tx.commit();
			System.out.println(emp);
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_0_, emp0_.ENAME as ENAME2_0_0_, emp0_.EADDRESS as EADDRESS3_0_0_, emp0_.ESALARY as ESALARY4_0_0_ from EMP emp0_ 
	where emp0_.ENO=?
	Hibernate: update EMP set ENAME=?, EADDRESS=?, ESALARY=? where ENO=?
	5 Bimal Bakliwal-2 NJ 58906.0
	 */
	
	//Using default hibernate.properties
	public void delete(){
		/*This code will call hibernate.properties by default:
		Configuration configuration = new Configuration(); */
		Configuration cfg = new Configuration().addResource("001_Emp.hbm.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			_001_Emp emp=(_001_Emp)session.get(_001_Emp.class, new Integer(6));
			session.delete(emp);
			tx.commit();
			System.out.println(emp);
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_0_, emp0_.ENAME as ENAME2_0_0_, emp0_.EADDRESS as EADDRESS3_0_0_, emp0_.ESALARY as ESALARY4_0_0_ from EMP emp0_ 
	where emp0_.ENO=?
	Hibernate: delete from EMP where ENO=?
	6 meghna Pune 98765.0
	 */
	
	public void selectAll() throws IOException{
		Configuration cfg = new Configuration().configure();
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			List<_001_Emp> employees = (List<_001_Emp>)session.createQuery(" FROM _001_Emp").list();
			System.out.println(employees);
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/*
	OUTPUT:
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.ENAME as ENAME2_0_, emp0_.EADDRESS as EADDRESS3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[4 Bimal Bakliwal-2 NJ 58906.0, 5 Bimal Bakliwal-2 NJ 58906.0, 7 Bimal Pune 23456.0, 8 meghna Pune 98765.0, 9 Bimal Pune 23456.0, 10 meghna Pune 98765
	.0, 11 Bimal Pune 23456.0, 12 meghna Pune 98765.0]
	 */
}
