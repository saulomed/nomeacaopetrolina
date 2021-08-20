package br.com.saulo.nomeacaopetrolina.server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class VerificaNomeacao implements Runnable {

    public static final String TEXTO_SUCESSO = "Verifique o diario, possivel nomeação";
    private final Log LOGGER = LogFactory.getLog(Mail.class);
    public static final String LORENA_GRACIELY_NEVES_TABLADA = "LORENA GRACIELY NEVES TABLADA";
    public static final String ALAIN = "ALAIN ESMERALDO LOPES";
    public static final String BRUNO = "BRUNO DE CASTRO FREITAS";
    private static String textoFalha = "Nenhum resultado foi encontrado para sua pesquisa.";
    public static boolean flagEnviado = false;

    public void run() {
        System.out.println("Verificacao executando");
        LOGGER.info("Verificacao executando");
        Calendar cal = Calendar.getInstance();
        // cal.set(Calendar.DAY_OF_MONTH,11);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 20);

        Date now = new Date();
        System.out.println("Agora: " + now);
        System.out.println("Hora Disparo: " + cal.getTime());
        System.out.println("Data de verificacao: " + getCurrentDay());
        System.out.println("flag: " + flagEnviado);

        if (!flagEnviado && now.after(cal.getTime())) {
            System.out.println("Realizar verificação");
            LOGGER.info("Realizar verificação");
//            verificaNomeacao(LORENA_GRACIELY_NEVES_TABLADA, "Lorena");
//            verificaNomeacao(ALAIN, "Alain");
//            verificaNomeacao(BRUNO, "Bruno");
            String diario = buscarDiarioDia();

            enviaEmail(LORENA_GRACIELY_NEVES_TABLADA, buscaNome(diario, LORENA_GRACIELY_NEVES_TABLADA));
            enviaEmail(ALAIN, buscaNome(diario, ALAIN));
            enviaEmail(BRUNO, buscaNome(diario, BRUNO));

            System.out.println("Verificação realizada com sucesso");
        } else if (flagEnviado && now.before(cal.getTime())) {
            String flagFalseMensagem = "Marcar flag false";
            System.out.println(flagFalseMensagem);
            flagEnviado = false;
            LOGGER.info(flagFalseMensagem);
        } else {
            String mensagem = "Verificação já realizada, ainda não atingiu o horário de validação"
                    + "ou flag já alterada. Nada a ser realizado";
            LOGGER.info(mensagem);
            System.out.println(mensagem);
        }

        System.out.println("Flag: " + flagEnviado);

    }

    private void verificaNomeacao(String nomeBusca, String nomeEmail) {

        String codigoPagina = null;
        int code = 0;
        String endereco = "http://doem.org.br/pe/petrolina/pesquisar?keyword=%s&data_publicacao=%s";
        endereco = String.format(endereco, nomeBusca, getCurrentDay());
        try {


            // String endereco =
            // "http://doem.org.br/pe/petrolina/pesquisar?keyword=LORENA+GRACIELY+NEVES+TABLADA&data_publicacao=2021-02-09";
            System.out.println(endereco);
            LOGGER.info("endereço: " + endereco);
            HttpURLConnection connection = realizaConsulta(endereco);
            code = connection.getResponseCode();
            System.out.println("Response code of the object is " + code);

            if (code != 200) {

                LOGGER.info("falha");
                endereco = endereco.replace("http", "https");
                connection = realizaConsulta(endereco);
                code = connection.getResponseCode();

            }

            if (code == 200) {
                LOGGER.info("sucesso");
                System.out.println("OK");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                codigoPagina = response.toString();
                // System.out.println("Pagina Resposta: "+codigoPagina);
                flagEnviado = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("falha", e);
        }

        Mail email = new Mail();
        nomeEmail = nomeEmail + " - " + getCurrentDay();
        if (codigoPagina != null && codigoPagina.contains(textoFalha)) {
            email.enviaEmailFalha(textoFalha, nomeEmail);
        } else if (codigoPagina != null && !codigoPagina.contains(textoFalha)) {
            email.enviaEmailSucesso(TEXTO_SUCESSO, nomeEmail);
        } else if (codigoPagina == null) {
            StringBuilder textoFalhaConsulta = new StringBuilder();
            textoFalhaConsulta.append("Falha ao consultar nomeação")
                    .append("\n")
                    .append("codigo resposta: ")
                    .append(code)
                    .append("URL: ")
                    .append(endereco);
            email.enviaEmailFalha(textoFalhaConsulta.toString(), nomeEmail);
        }

        System.out.println("fim Verificacao");
    }

    private HttpURLConnection realizaConsulta(String endereco) throws IOException {
        URL url = null;
        url = new URL(endereco);
        System.setProperty("http.agent", "Chrome");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        return connection;
    }

    private String getCurrentDay() {
        // Create a Calendar Object
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String padraoData = "YYYY-MM-dd";

        // Integer to String Conversion
        String todayStr = new SimpleDateFormat(padraoData).format(calendar.getTime());
        System.out.println("Today Str: " + todayStr + "\n");

        return todayStr;
    }

    public static void main(String[] args) {
        VerificaNomeacao teste = new VerificaNomeacao();
        String diario = teste.buscarDiarioDia();

        teste.enviaEmail(LORENA_GRACIELY_NEVES_TABLADA, teste.buscaNome(diario, LORENA_GRACIELY_NEVES_TABLADA));
        teste.enviaEmail(ALAIN, teste.buscaNome(diario, LORENA_GRACIELY_NEVES_TABLADA));
        teste.enviaEmail(BRUNO, teste.buscaNome(diario, LORENA_GRACIELY_NEVES_TABLADA));
    }

    private void enviaEmail(String nome, Boolean sucesso) {
        Mail email = new Mail();
        String nomeEmail = nome + " - " + getCurrentDay();

        if (sucesso) {
            email.enviaEmailSucesso(TEXTO_SUCESSO, nomeEmail);
        } else {
            email.enviaEmailFalha(textoFalha, nomeEmail);
        }
    }

    private boolean buscaNome(String texto, String nome) {
        boolean retorno = false;
        LOGGER.info("Busca nome no arquivo");
        if (StringUtils.containsIgnoreCase(texto, nome)) {
            retorno = true;
        }
        return retorno;
    }

    private String buscarDiarioDia() {
        String paginaDiarios = "https://doem.org.br/pe/Petrolina/diarios";
        String basediarioDia = "https://doem.org.br/pe/Petrolina/diarios/previsualizar/";
        int code = 0;
        String codigoPagina = null;
        HttpURLConnection connection = null;
        try {
            connection = realizaConsulta(paginaDiarios);
            code = connection.getResponseCode();
            System.out.println("Response code of the object is " + code);
            LOGGER.info("realizou consulta "+code);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = null;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            codigoPagina = response.toString();

            int beginIndex = codigoPagina.indexOf(basediarioDia) + basediarioDia.length();
            String codigoDiario = codigoPagina.substring(beginIndex, beginIndex + 8);

            System.out.println(basediarioDia + codigoDiario);
            LOGGER.info("basediarioDia + codigoDiario ="+basediarioDia + codigoDiario);

            connection = realizaConsulta(basediarioDia + codigoDiario);
            code = connection.getResponseCode();
            System.out.println("Response code of the object is " + code);
            LOGGER.info("realizou consulta "+code);
            inputLine = null;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            codigoPagina = response.toString();

            String html = StringEscapeUtils.unescapeHtml3(codigoPagina);
            String urlDownload = html.substring(html.indexOf("file=") + 5, html.indexOf("&"));
            System.out.println(URLDecoder.decode(urlDownload));
            downloadFile(urlDownload);
            String textoPDF = readPDF("diario.pdf");
            return textoPDF;
        } catch (IOException e) {
            LOGGER.info("falha de conexão", e);
        }
        return null;
    }

    private void downloadFile(String url) {
        String download = URLDecoder.decode(url, Charset.defaultCharset());

        try {
            saveFileFromUrlWithCommonsIO("diario.pdf", download);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // Using Commons IO library
    // Available at http://commons.apache.org/io/download_io.cgi
    public void saveFileFromUrlWithCommonsIO(String fileName,
                                             String fileUrl)
            throws MalformedURLException, IOException {
        FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
    }

    private String readPDF(String arquivo) {
        LOGGER.info("Leitura do arquivo"+arquivo);
        PDFParser parser = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;

        String parsedText;
        String fileName = arquivo;
        File file = new File(fileName);
        try {
            parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
//            System.out.println(parsedText.replaceAll("[^A-Za-z0-9. ]+", ""));
            return parsedText.replaceAll("[^A-Za-z0-9. ]+", "");
        } catch (Exception e) {
            LOGGER.error("falha na leitura do arquivo",e);
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
        return null;
    }
}
