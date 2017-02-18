/*
------------------
Hibernate Caching:
------------------
Caching is all about application performance optimization and it sits between your application and the database to avoid the number of database hits as 
much as possible to give a better performance. Caching is important to Hibernate as well which utilizes a multilevel caching schemes as explained below:

First-level cache: 
The first-level cache is the Session cache and is a mandatory cache through which all requests must pass. The Session cache keeps an object under its 
own control before committing it to the database. If you issue multiple updates to an object, Hibernate tries to delay doing the update as long as possible 
to reduce the number of update SQL statements issued. If you close the session, all the objects being cached are lost and either persisted or updated in 
the database.

Second Level Cache:
1. In the first level cache or session cache, the objects are cached as long as the session is not closed. If we want the same object again in some other 
session, we have to load it again in the cache. What if we need some object across multiple session. Second level of cache to the rescue. It provides data:
- Across sessions in an application
- Across applications (provided they are all talking to the same data & all are using hibernate)
- Across clusters (provided they are all talking to the same data & all are using hibernate)
If any part of the application is not using hibernate, it will not be aware of the cache data and will directly talk to DB and dirtying the data. This will 
make hibernate cache invalid, as hibernate would not know about this change. But if we are configuring 2nd level cache across application, hibernate will 
take care of these issues.

2. Second level cache is an optional cache and first-level cache will always be consulted before any attempt is made to locate an object in the second-level 
cache. The second-level cache can be configured on a per-class and per-collection basis and mainly responsible for caching objects across sessions. 

3. Hibernate uses first-level cache by default and you have nothing to do to use first-level cache. To use 2nd level cache: 
Include cache provider jar & related configuration into your project (3rd party cache providers)
Configure hibernate configuration file
Update the entity to make it cacheable & provide caching strategy (annotations are hibernate specific). Objects configured to cache goes to 2nd level cache.

Concurrency strategies: A concurrency strategy is a mediator which responsible for storing items of data in the cache and retrieving them from the 
cache. If you are going to enable a second-level cache, you will have to decide, for each persistent class and collection, which cache concurrency 
strategy to use.
TRANSACTIONAL: Use this strategy for read-mostly data where it is critical to prevent stale data in concurrent transactions, in the rare case of an 
update.
READ-WRITE: Again use this strategy for read-mostly data where it is critical to prevent stale data in concurrent transactions, in the rare case of 
an update.
NONSTRICT-READ-WRITE: This strategy makes no guarantee of consistency between the cache and the database. Use this strategy if data hardly ever 
changes and a small likelihood of stale data is not of critical concern.
READ-ONLY: A concurrency strategy suitable for data which never changes. Use it for reference data only.

Cache provider: Your next step is to pick a cache provider. Hibernate forces you to choose a single cache provider for the whole application.
- EHCache: It can cache in memory or on disk and clustered caching and it supports the optional Hibernate query result cache.
- OSCache: Supports caching to memory and disk in a single JVM, with a rich set of expiration policies and query cache support.
- warmCache: A cluster cache based on JGroups. It uses clustered invalidation but doesn't support the Hibernate query cache
- JBoss Cache: A fully transactional replicated clustered cache also based on the JGroups multicast library. It supports replication or invalidation, 
synchronous or asynchronous communication, and optimistic and pessimistic locking. The Hibernate query cache is supported.
Every cache provider is not compatible with every concurrency strategy. The following compatibility matrix will help you choose an appropriate 
combination.

EHCACHE.xml
You need to specify the properties of the cache regions. EHCache has its own configuration file, ehcache.xml, which should be in the CLASSPATH of 
the application. The various attributes are explained below:
path:  Sets the path to the directory where cache .data files are created.
name: Sets the name of the cache. It must be unique.
maxInMemory: Sets the maximum number of objects that will be created in memory
eternal: Sets whether elements are eternal. If eternal, timeouts are ignored and the element is never expired.
timeToIdleSecond: Sets the time to idle for an element before it expires. Is only used if the element is not eternal. Idle time is now last accessed time.
timeToLiveSeconds: Sets the time to live for an element before it expires. Is only used if the element is not eternal. TTL is now - creation time.
overflowToDisk: Sets whether elements can overflow to disk when the in-memory cache has reached the maxInMemory limit.

In the demo we have second-level caching enabled for the Employee class and Hibernate now hits the second-level cache whenever you navigate to a Employee or 
when you load a Employee by identifier. Emp cache can contain 1000 elements. Elements will always be held in memory.
You should analyze your all the classes and choose appropriate caching strategy for each of the classes. Sometime, second-level caching may downgrade the 
performance of the application. So it is recommended to benchmark your application first without enabling caching and later on enable your well suited 
caching and check the performance. If caching is not improving system performance then there is no point in enabling any type of caching.

*/

package _001;

import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Entity
@Table(name="EMP")
class _001Emp{
	
	@Id
	@Column(name = "ENO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	int eid;
	
	@Column(name = "ENAME")
	String name;
	
	@Column(name = "EADDRESS")
	String address;
	
	@Column(name = "ESALARY")
	double salary;
	
	public _001Emp() {	}

	public _001Emp(String name, String address, double salary) {
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
}

public class _001_NoCache {	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Configuration cfg = new Configuration().configure("001.hibernate.cfg.xml");
		SessionFactory sf = cfg.buildSessionFactory();
		System.out.println("1-Session");
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		_001Emp emp1 = new _001Emp("Bimal","Pune",23456);
		session.save(emp1);
		tx.commit();
		session.close();
		
		System.out.println("2-Session");
		session = sf.openSession();
		List<_001Emp> employees = (List<_001Emp>)session.createQuery(" FROM _001Emp").list();
		System.out.println(employees);
		session.close();
		
		System.out.println("3-Session");
		session = sf.openSession();
		employees = (List<_001Emp>)session.createQuery(" FROM _001Emp").list();
		employees.get(employees.size()-1).setName("BJ");
		System.out.println(employees);
		session.close();
		
		System.out.println("4-Session");
		session = sf.openSession();
		employees = (List<_001Emp>)session.createQuery(" FROM _001Emp").list();
		System.out.println(employees);
		session.close();
		
	}
	/*
	OUTPUT:
	1-Session
	Hibernate: insert into EMP (EADDRESS, ENAME, ESALARY) values (?, ?, ?)
	
	2-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 Bimal Pune 23456.0]
	
	3-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 BJ Pune 23456.0]
	Hibernate: update EMP set EADDRESS=?, ENAME=?, ESALARY=? where ENO=?
	
	4-Session
	Hibernate: select emp0_.ENO as ENO1_0_, emp0_.EADDRESS as EADDRESS2_0_, emp0_.ENAME as ENAME3_0_, emp0_.ESALARY as ESALARY4_0_ from EMP emp0_
	[1 BJ Pune 23456.0]
	 */
}
