package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    private static String token = "none";

    @RequestMapping("/login")
    public String login(@RequestParam(value="name", defaultValue="user") String name, @RequestParam(value="password", defaultValue="") String password) {
        if (token.equals("none")){
            boolean isLogged = ServerData.getDbaction().verifyUserPassword2(name, password);
            if (isLogged){
                token = "loggedIn";
                return "Bem-vindo";
            }
        }
        return "Login incorreto";
    }

    @RequestMapping("/logout")
    public void logout() {
        token = "none";
    }

    @RequestMapping("/list")
    public String listMusics() {

        if (token.equals("loggedIn")){

            StringBuffer table = new StringBuffer();
            table.append("<style>\n" +
                    "table {\n" +
                    "  font-family: arial, sans-serif;\n" +
                    "  border-collapse: collapse;\n" +
                    "  width: 100%;\n" +
                    "}\n" +
                    "\n" +
                    "td, th {\n" +
                    "  border: 1px solid #dddddd;\n" +
                    "  text-align: left;\n" +
                    "  padding: 8px;\n" +
                    "}\n" +
                    "\n" +
                    "tr:nth-child(even) {\n" +
                    "  background-color: #dddddd;\n" +
                    "}\n" +
                    "</style><table> " +
                    "<tr>\n" +
                    "    <th>Nome da Música</th>\n" +
                    "    <th>Nome do Artista</th>\n" +
                    "    <th>Albúm</th>\n" +
                    "    <th>Ano de lançamento</th>" +
                    "    <th>Duração</th>" +
                    "    <th>Género</th>" +
                    "    <th>Ouvir</th>" +
                    "</tr>");
            JSONObject obj = ServerData.getDbaction().listMusics();
            int numberOfMusics = (int) obj.get("numberOfMusics");

            for (int i = 1; i <= numberOfMusics; i++) {

                String nome = "music" + i;
                JSONArray array = (JSONArray) obj.get(nome);

                table.append("<tr>");
                table.append("<td>" + array.get(0) + "</td>");
                table.append("<td>" + array.get(1) + "</td>");
                table.append("<td>" + array.get(2) + "</td>");
                table.append("<td>" + array.get(3) + "</td>");
                table.append("<td>" + array.get(4) + "</td>");
                table.append("<td>" + array.get(5) + "</td>");
                table.append("<td><a href='http://localhost:8080/download?name=" + array.get(0) +" &&author=" + array.get(1) + "'>Ouvir</a></td>");
                table.append("</tr>");
            }
            table.append("</table>");
            return table.toString();

        }

        return "Não existem dados para serem apresentados";

    }

    @RequestMapping("/download")
    public void downloadMusic(@RequestParam(value="name", defaultValue="none") String name,
                              @RequestParam(value="author", defaultValue="none") String autor,
                              HttpServletResponse response) throws IOException {

        if (!name.equals("none") && !autor.equals("none") && token.equals("loggedIn")){

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
