package br.com.saulo.nomeacaopetrolina.server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExecucaoVerificacao
{
    public static final VerificaNomeacao verificaNomeacao = new VerificaNomeacao();
    private final Log LOGGER = LogFactory.getLog(ExecucaoVerificacao.class);
    
    @Scheduled(cron = "0 10-30 17 * * ?")
//    @Scheduled(cron ="0 * * * * ?")
    public void execute()
    {
    	
        System.out.println("execucao de verificação");
        LOGGER.info("Inicio Execucao serviço");
        verificaNomeacao.run();
        
        LOGGER.info("Fim Execucao serviço");
        
    }
}
