package com.onlydb;

import com.onlydb.config.utils.LocalUtils;
import com.onlydb.data.mac.dao.TestMapper;
import com.onlydb.global.task.ElectryTask;
import com.onlydb.global.task.NormalTask;
import com.onlydb.global.task.PumbTask;
import com.onlydb.httpServer.HttpServer;
import com.onlydb.macServer.MessageServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@MapperScan("com.example.onlydb.data.mac.dao")
public class OnlydbApplication implements CommandLineRunner{

	@Autowired
	private MessageServer macserver;
	@Autowired
	private HttpServer httpserver;
	@Autowired
	private TestMapper mapper;

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(OnlydbApplication.class);
		LocalUtils.context = app.run(args);

	}


	@Override
	public void run(String... args) throws Exception {
		mapper.initJQZT();
		mapper.initJZZT();
		ExecutorService threadpool = Executors.newFixedThreadPool(2);
		ScheduledExecutorService sespool = Executors.newScheduledThreadPool(3);
		Runnable socketT = ()->{
			try {
				macserver.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Runnable httpT = ()->{
			try {
				httpserver.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		threadpool.execute(httpT);
		threadpool.execute(socketT);
		sespool.scheduleAtFixedRate(new NormalTask(),1,10, TimeUnit.MINUTES);
		sespool.scheduleAtFixedRate(new ElectryTask(),3,10,TimeUnit.MINUTES);
		sespool.scheduleAtFixedRate(new PumbTask(),5,10,TimeUnit.MINUTES);
	}
}
