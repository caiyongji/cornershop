package iuv.cns.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
@Deprecated
class MyX509TrustManager implements X509TrustManager {
	private final static Log LOG = LogFactory.getLog(MyX509TrustManager.class);
	X509TrustManager trustManager;

	MyX509TrustManager() throws Exception {
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream(Constants.CA_KEY_DIR), Constants.CA_PWD);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SSL", "SunJSSE");
		tmf.init(ks);

		TrustManager tms[] = tmf.getTrustManagers();

		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509TrustManager) {
				trustManager = (X509TrustManager) tms[i];
				return;
			}
		}

		throw new Exception("Couldn't initialize");
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			trustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException excep) {
			LOG.error("【CertificateException】",excep);
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			trustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException excep) {
			LOG.error("【CertificateException】",excep);
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return trustManager.getAcceptedIssuers();
	}
}