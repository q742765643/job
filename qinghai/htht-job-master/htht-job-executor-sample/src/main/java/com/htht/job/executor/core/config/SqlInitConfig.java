package com.htht.job.executor.core.config;

import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DubboIpUtil;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class SqlInitConfig implements ApplicationRunner{

	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.driver-class-name}")
	private String driver;
	@Autowired
	private RegistryService registryService;
	@Override
	public void run(ApplicationArguments applicationarguments) throws Exception {
		boolean flag=registryService.existTable();
		if(flag){
			return;
		}
		this.initSql();
		this.insertLogSql();




	}

	public  void initSql(){
		SQLExec sqlExec = new SQLExec();
		sqlExec.setDriver(driver);
		sqlExec.setUrl(url);
		sqlExec.setUserid(username);
		sqlExec.setPassword(password);
		sqlExec.setEncoding("UTF8");

		String file=  this.getClass().getClassLoader().getResource("htht_data.sql").getFile();

		sqlExec.setSrc(new File(file));

		sqlExec.setOnerror((SQLExec.OnError)(EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错
		sqlExec.execute();
	}
	public  void insertLogSql(){
		SQLExec sqlExec = new SQLExec();
		sqlExec.setDriver(driver);
		sqlExec.setUrl(url);
		sqlExec.setUserid(username);
		sqlExec.setPassword(password);
		sqlExec.setEncoding("UTF8");

		String file=  this.getClass().getClassLoader().getResource("htht_executesql_log.sql").getFile();

		sqlExec.setSrc(new File(file));
		sqlExec.setOnerror((SQLExec.OnError)(EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错
		sqlExec.execute();
	}
}


