package com.ClinicaDelCalzado_BackEnd;

import com.ClinicaDelCalzado_BackEnd.util.ScopeUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ScopeUtils.calculateScopeSuffix();
		new SpringApplicationBuilder(Application.class).registerShutdownHook(true).run(args);
	}

}
