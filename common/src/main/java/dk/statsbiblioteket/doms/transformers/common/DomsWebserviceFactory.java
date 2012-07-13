package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Map;

/**
 * Get Doms Webservice.
 */
public class DomsWebserviceFactory {
    private CentralWebservice centralWebservice;
    private DomsConfig config;

    public DomsWebserviceFactory(DomsConfig config) {
        this.config = config;
    }

    /**
     * Get doms webservice singleton.
     *
     * @return A doms webservice.
     */
    public synchronized CentralWebservice getWebservice() {
        try {
            if (centralWebservice == null) {
                this.config = config;
                CentralWebservice webservice = new CentralWebserviceService(
                        new URL(this.config.getDomsWebserviceUrl()),
                        new QName("http://central.doms.statsbiblioteket.dk/", "CentralWebserviceService"))
                        .getCentralWebservicePort();
                Map<String, Object> domsAPILogin = ((BindingProvider) webservice).getRequestContext();
                domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, config.getDomsUsername());
                domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, config.getDomsPassword());
                centralWebservice = webservice;
            }
            return centralWebservice;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
