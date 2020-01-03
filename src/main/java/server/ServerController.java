package server;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.logic.ServerData;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class ServerController{

    private static final String template = "Username: %s Password: %s";

    @RequestMapping("/login")
    public String login(@RequestParam(value="name", defaultValue="user") String name, @RequestParam(value="password", defaultValue="") String password) {
        return String.format(template, name, password);
    }

    @RequestMapping("/list")
    public String listMusics() {

        return ServerData.getDbaction().listMusics().toString();

    }

    @RequestMapping("/download")
    public void downloadMusic(@RequestParam(value="name", defaultValue="user") String name,
                              @RequestParam(value="author", defaultValue="") String autor,
                              HttpServletResponse response) throws IOException {

        if (name != null && autor != null){

            String path = ServerData.getDbaction().getFileName2(name, autor);
            File file = new File(path);

            if (file.exists()){

                response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

                response.setContentLength((int) file.length());

                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                FileCopyUtils.copy(inputStream, response.getOutputStream());

            }

        }

    }

}
