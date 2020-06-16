package com.htht.job.executor;

import org.jeesys.common.jpa.dao.GenericDaoImpl;
import org.jeesys.common.jpa.repository.support.GenericJpaRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.htht.job.executor.util.SpringContextUtil;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(basePackages={"com.htht.job"})
@SpringBootApplication(scanBasePackages = {"com.htht.job"})
@EnableAutoConfiguration(exclude = {RedisRepositoriesAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class})
@PropertySource("classpath:jdbc.properties")
@PropertySource("classpath:config.properties")
@EnableJpaRepositories(basePackages = "com.htht.job", repositoryBaseClass = GenericDaoImpl.class, repositoryFactoryBeanClass = GenericJpaRepositoryFactoryBean.class)
@ImportResource({
	"classpath:dubbo-provider.xml",
	"classpath:dubbo-consumer.xml"
})
@EnableTransactionManagement
@EnableCaching
@Import(value={SpringContextUtil.class})
public class Application extends SpringBootServletInitializer  {
	public static void main(String[] args) {
		configureApplication(new SpringApplicationBuilder()).run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return configureApplication(builder);
	}

	private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

}