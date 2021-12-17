package cu.cujae.aica.signdigital.cross;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Setting {

    @Value("${api.rest.client}")
    private String clientBase;
    @Value("${api.rest.associates_signs}")
    private String signsBase;

    @Value("${pki.integro.crl_path}")
    private String crlPath;
    @Value("${aica.proxy.ip}")
    private String aicaProxyIp;
    @Value("${aica.proxy.port}")
    private Integer aicaProxyPort;

    // public static final String DEST = "./target/signatures/chapter02/";
    public static final String DEST = "D:\\Condultoria\\source\\signdigital\\target\\signatures\\chapter02\\";

    public static final String KEYSTORE = "./src/test/resources/encryption/ks";
    public static final String SRC = "./src/test/resources/pdfs/hello.pdf";

    public static final char[] PASSWORD = "password".toCharArray();

    public static final String[] RESULT_FILES = new String[] {
            "hello_signed1.pdf",
            "hello_signed2.pdf",
            "hello_signed3.pdf",
            "hello_signed4.pdf"
    };

}
