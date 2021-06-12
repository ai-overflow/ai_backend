package de.hskl.ki.services;

import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.models.proxy.RequestMethods;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class ProxyService {

    @Autowired
    private ProjectRepository projectRepository;

    public Optional<byte[]> proxyRequest(ProxyFormRequest formRequest) {
        var project = projectRepository.findById(formRequest.getId());

        try {
            URL url = new URL(parseUrl(formRequest.getUrl()));
            // TODO: This is sent as POST?
            System.out.println("Url: " + url + ", " + formRequest.getMethod().name());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(formRequest.getMethod().name());
            for (Map.Entry<String, String> entry : formRequest.getHeaderMap().entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            con.setRequestProperty("Content-Type", formRequest.getContentType());
            con.setInstanceFollowRedirects(true);
            con.setDoOutput(true);

            if(!formRequest.getMethod().equals(RequestMethods.GET)) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = null;
                    if (formRequest.getDataBinary() != null) {
                        input = formRequest.getDataBinary();
                    } else if (formRequest.getData() != null) {
                        input = formRequest.getData().getBytes(StandardCharsets.UTF_8);
                    }

                    if (input != null) {
                        os.write(input, 0, input.length);
                    }
                }
            }
            InputStream responseStream = con.getInputStream();
            return Optional.of(IOUtils.toByteArray(responseStream));
        } catch (ConnectException e) {
            System.out.println("Failed to connect: " + parseUrl(formRequest.getUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String parseUrl(String url) {
        // TODO: Load hostname from DB
        return url.replace("{{internal.HOST_URL}}", "localhost");
    }
}
