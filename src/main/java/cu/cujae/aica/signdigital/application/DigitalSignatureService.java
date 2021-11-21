package cu.cujae.aica.signdigital.application;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.*;
import cu.cujae.aica.signdigital.client.IClientAgent;
import cu.cujae.aica.signdigital.cross.Setting;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Base64;

@Service
@Slf4j
public class DigitalSignatureService implements IDigitalSignatureService {
    private final Setting setting;
    private final IClientAgent clientAgent;

    public DigitalSignatureService(Setting setting, IClientAgent clientAgent) {
        this.setting = setting;
        this.clientAgent = clientAgent;
    }

    @Override
    public Boolean signPdf(SignPDFIn signPDFIn) {
        byte[] pdf = Base64.getDecoder().decode(signPDFIn.getFile());
        InputStream pdfIs = new ByteArrayInputStream(pdf);

        byte[] cert = Base64.getDecoder().decode(signPDFIn.getPrivateKey());
        InputStream certIs = new ByteArrayInputStream(cert);

        KeyStore keyStore = null;
        try {
            byte[] img = Base64.getDecoder().decode(setting.getImgSrc());
//            Image image = Image.getInstance(img);

            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            keyStore = KeyStore.getInstance("pkcs12", provider);
            keyStore.load(certIs, signPDFIn.getPassword().toCharArray());
            String alias = keyStore.aliases().nextElement();
            PrivateKey pk = (PrivateKey) keyStore.getKey(alias, signPDFIn.getPassword().toCharArray());
            Certificate[] chain = keyStore.getCertificateChain(alias);


            PdfReader reader = new PdfReader(pdfIs);
            OutputStream os = new FileOutputStream(Setting.DEST);
            PdfSigner signer = new PdfSigner(reader, os, true);

            // Create the signature appearance
            Rectangle rect = new Rectangle(36, 648, 200, 100);
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            appearance
                    .setReason("reason")
                    .setLocation("location")

                    // Specify if the appearance before field is signed will be used
                    // as a background for the signed field. The "false" value is the default value.
                    .setReuseAppearance(false)
                    .setPageRect(rect)
                    .setImage(ImageDataFactory.createRawImage(img))
                    .setPageNumber(1);
            signer.setFieldName("sig");

            IExternalSignature pks = new PrivateKeySignature(pk, pk.getAlgorithm(), provider.getName());
            IExternalDigest digest = new BouncyCastleDigest();

            // Sign the document using the detached mode, CMS or CAdES equivalent.
            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
