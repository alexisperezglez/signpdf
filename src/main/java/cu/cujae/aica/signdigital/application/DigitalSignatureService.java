package cu.cujae.aica.signdigital.application;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import cu.cujae.aica.signdigital.client.IClientAgent;
import cu.cujae.aica.signdigital.cross.Setting;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import cu.cujae.aica.signdigital.repositories.IConfigDb;
import cu.cujae.aica.signdigital.repositories.dto.UserConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DigitalSignatureService implements IDigitalSignatureService {
    private final Setting setting;
    private final IClientAgent clientAgent;
    private final IConfigDb configDb;

    public DigitalSignatureService(Setting setting, IClientAgent clientAgent, IConfigDb configDb) {
        this.setting = setting;
        this.clientAgent = clientAgent;
        this.configDb = configDb;
    }

    @Override
    public Boolean signPdf(SignPDFIn signPDFIn) {
        UserConfigDTO configByUser = this.configDb.getConfigByUser(signPDFIn.getUsername());
        byte[] pdf = Base64.getDecoder().decode(signPDFIn.getFile());
        InputStream pdfIs = new ByteArrayInputStream(pdf);

        byte[] cert = Base64.getDecoder().decode(signPDFIn.getPrivateKey());
        InputStream certIs = new ByteArrayInputStream(cert);

        KeyStore keyStore;
        try {
            byte[] img = Base64.getDecoder().decode(configByUser.getImage());

            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            keyStore = KeyStore.getInstance("pkcs12", provider);
            keyStore.load(certIs, signPDFIn.getPassword().toCharArray());
            String alias = keyStore.aliases().nextElement();
            PrivateKey pk = (PrivateKey) keyStore.getKey(alias, signPDFIn.getPassword().toCharArray());
            Certificate[] chain = keyStore.getCertificateChain(alias);

            X509Certificate x509Certificate = ((X509Certificate)keyStore.getCertificate("key"));
            Map<String, String> info = this.getCertificateInformation(x509Certificate);

            PdfReader reader = new PdfReader(pdfIs);
            OutputStream os = new ByteArrayOutputStream();
            PdfSigner signer = new PdfSigner(reader, os, true);

            // Create the signature appearance
            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            appearance
                    .setReason(signPDFIn.getStage())
                    .setLocation(info.get("Provincia"))
                    .setContact(info.get("Usuario"))

                    // Specify if the appearance before field is signed will be used
                    // as a background for the signed field. The "false" value is the default value.
                    .setReuseAppearance(false)
                    .setPageRect(rect)
                    .setImage(ImageDataFactory.createRawImage(img))
                    .setSignatureGraphic(ImageDataFactory.createRawImage(img))
                    .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION)
                    .setPageNumber(1);
            signer.setFieldName("sig");

            IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA512, provider.getName());
            IExternalDigest digest = new BouncyCastleDigest();

            // Sign the document using the detached mode, CMS or CAdES equivalent.
            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
            String val = Base64.getEncoder().encodeToString(((ByteArrayOutputStream) os).toByteArray());
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean isDateValid(X509Certificate x509Certificate) {
        try {
            x509Certificate.checkValidity();
            return true;
        } catch (java.security.cert.CertificateNotYetValidException | java.security.cert.CertificateExpiredException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Map<String, String> getCertificateInformation(java.security.cert.X509Certificate x509Certificate) {
        Map<String, String> info = new HashMap<>();
        String dn = x509Certificate.getSubjectDN().getName();
        String[] arr = dn.split(",");
        for (String item : arr) {
            String[] clave = item.split("=");
            switch (clave[0]) {
                case "CN":           info.put("CommonName", clave[1]);break;
                case "OU":           info.put("Unidad", clave[1]);break;
                case "O":            info.put("Organizacion", clave[1]);break;
                case "L":            info.put("Municipio", clave[1]);break;
                case "ST":           info.put("Provincia", clave[1]);break;
                case "C":            info.put("Pais", clave[1]);break;
                case "UID":          info.put("Usuario", clave[1]);break;
                case "EMAILADDRESS": info.put("Email", clave[1]);break;
                default:;
            }
        }
        return info;
    }

    private X509CRLEntry x509CRLValidator(java.security.cert.X509Certificate x509Certificate) throws Exception {
        X509CRLEntry crlEntry = null;

        // Activate the new trust manager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL url = new URL(Setting.CRLPATH);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Setting.AICAPROXYIP, Setting.AICAPROXYPORT));
            URLConnection connection = url.openConnection(proxy);
            InputStream is = connection.getInputStream();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            X509CRL x509CRL = (X509CRL) certificateFactory.generateCRL(is);

            crlEntry = x509CRL.getRevokedCertificate(x509Certificate);

            return crlEntry;
        } catch (IOException | NoSuchAlgorithmException | CRLException |KeyManagementException e) {
            e.printStackTrace();
            throw e;
        }

    }

}
