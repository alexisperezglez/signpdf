package cu.cujae.aica.signdigital.application;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import cu.cujae.aica.signdigital.client.IClientAgent;
import cu.cujae.aica.signdigital.client.ISignsConfAgent;
import cu.cujae.aica.signdigital.client.dto.WorkersResponse;
import cu.cujae.aica.signdigital.cross.Setting;
import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import cu.cujae.aica.signdigital.repositories.IConfigDb;
import cu.cujae.aica.signdigital.repositories.dto.UserConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class DigitalSignatureService implements IDigitalSignatureService {
    private final Setting setting;
    private final IClientAgent clientAgent;
    private final ISignsConfAgent signsConfAgent;
    private final IConfigDb configDb;

    public DigitalSignatureService(Setting setting, IClientAgent clientAgent, ISignsConfAgent signsConfAgent, IConfigDb configDb) {
        this.setting = setting;
        this.clientAgent = clientAgent;
        this.signsConfAgent = signsConfAgent;
        this.configDb = configDb;
    }

    @Override
    public Boolean saveConfig(DigitalSignConfIn digitalSignConfIn) {
        return this.configDb.saveConfig(digitalSignConfIn);
    }

    @Override
    public Mono<Boolean> signPdf(SignPDFIn signPDFIn) {
        UserConfigDTO configByUser = this.configDb.getConfigByUser(signPDFIn.getUsername());

        byte[] pdf = Base64.getDecoder().decode(signPDFIn.getFile());
        InputStream pdfIs = new ByteArrayInputStream(pdf);

        byte[] cert = Base64.getDecoder().decode(signPDFIn.getPrivateKey());
        InputStream certIs = new ByteArrayInputStream(cert);

        byte[] img = Base64.getDecoder().decode(configByUser.getImage());

        return this.signsConfAgent.getSignsConf(signPDFIn.getStage())
                .publishOn(Schedulers.boundedElastic())
                .map(signsConfResponses -> {
                    try {
                        BouncyCastleProvider provider = new BouncyCastleProvider();
                        Security.addProvider(provider);
                        KeyStore keyStore = KeyStore.getInstance("pkcs12", provider);
                        keyStore.load(certIs, signPDFIn.getPassword().toCharArray());
                        String alias = keyStore.aliases().nextElement();
                        PrivateKey pk = (PrivateKey) keyStore.getKey(alias, signPDFIn.getPassword().toCharArray());
                        Certificate[] chain = keyStore.getCertificateChain(alias);

                        X509Certificate x509Certificate = ((X509Certificate)keyStore.getCertificate("key"));
                        Map<String, String> info = this.getCertificateInformation(x509Certificate);

                        AtomicReference<WorkersResponse> workerResponse = new AtomicReference<>();
                        signsConfResponses.forEach(signsConfResponse -> {
                            signsConfResponse.getStepFormsResponses().forEach(stepFormsResponse -> {
                                stepFormsResponse.getWorkersResponses().forEach(workersResponse -> {
                                    if (workersResponse.getFullName().equalsIgnoreCase(info.get("Usuario"))) {
                                        workerResponse.set(workersResponse);
                                    }
                                });
                            });
                        });

                        if (Optional.ofNullable(workerResponse.get()).isPresent()) {
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
                            // String val = Base64.getEncoder().encodeToString(((ByteArrayOutputStream) os).toByteArray());
                            File file = File.createTempFile("formulario", ".pdf");
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(((ByteArrayOutputStream) os).toByteArray());
                            fileOutputStream.close();
                            return file;
                        }

                        throw new RuntimeException("No se encuentra el usuario");
                    } catch (GeneralSecurityException | IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("No se pudo firmar el documento", e);
                    }
                })
                .zipWhen(this.clientAgent::sendSignedPdf, (file, oBoolean) -> oBoolean);
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

            URL url = new URL(setting.getCrlPath());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(setting.getAicaProxyIp(), setting.getAicaProxyPort()));
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
