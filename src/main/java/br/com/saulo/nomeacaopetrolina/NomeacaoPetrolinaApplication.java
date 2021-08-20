package br.com.saulo.nomeacaopetrolina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.saulo.nomeacaopetrolina.server.ExecucaoVerificacao;
import br.com.saulo.nomeacaopetrolina.server.VerificaNomeacao;

@SpringBootApplication
@RestController
@EnableScheduling
public class NomeacaoPetrolinaApplication {
	private final Log LOGGER = LogFactory.getLog(NomeacaoPetrolinaApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(NomeacaoPetrolinaApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Olá Nomeação Petrolina!";
	}
	
	@GetMapping("/verificaLorena")
	public void verificaLorena()
	{
		LOGGER.info("Executa verificacao agora");
		ExecucaoVerificacao.verificaNomeacao.run();
		LOGGER.info("fim verificacao agora");
	}
	
	@GetMapping("/habilitaValidacao")
	public String habilitaValidacao()
	{
		LOGGER.info("habilita validação");
		VerificaNomeacao.flagEnviado = false;
		String message = "VerificaNomeacao.flagEnviado = "+VerificaNomeacao.flagEnviado;
		LOGGER.info(message);
		return message;
		
	}
	
	@GetMapping("/checaFlag")
	public String checaFlag()
	{
		String message = "VerificaNomeacao.flagEnviado = "+VerificaNomeacao.flagEnviado;
		LOGGER.info(message);
		return message;
		
	}
}
